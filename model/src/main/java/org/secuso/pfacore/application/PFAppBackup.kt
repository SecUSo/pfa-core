package org.secuso.pfacore.application

import android.content.Context
import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil.getSupportSQLiteOpenHelper
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil.writeDatabase
import org.secuso.privacyfriendlybackup.api.backup.FileUtil

/**
 * Add custom, app-specific backup and restore functionality to the backup/restore process provided by the PFA-Core library.
 * This is intended to backup and restore e.g. files.
 *
 * @author Patrick Schneider
 * @see org.secuso.privacyfriendlybackup.api.common.BackupApi
 */
interface PFAppBackup {
    val key: String
    fun backup(writer: JsonWriter): JsonWriter = writer
    fun restore(key: String, reader: JsonReader, context: Context): JsonReader = reader
}

/**
 * Add a database to the backup/restore process provided by the PFA-Core library.
 *
 * @see RoomDatabaseConfig
 * @see SQLiteHelperConfig
 * @author Patrick Schneider
 */
interface BackupDatabaseConfig {
    fun backup(writer: JsonWriter)
    fun restore(reader: JsonReader)
}

/**
 * This is a default implementation to backup and restore a room database and should work out-of-box.
 *
 * @author Patrick Schneider
 */
open class RoomDatabaseConfig(val context: Context, val name: String, val clazz: Class<out RoomDatabase>): BackupDatabaseConfig {
    companion object {
        val TAG = PFModelApplication.instance.name
    }

    override fun backup(writer: JsonWriter) {
        val dataBase = DatabaseUtil.getSupportSQLiteOpenHelper(context, name).writableDatabase

        Log.d(TAG, "Writing database")
        writer.name("database")
        DatabaseUtil.writeDatabase(writer, dataBase)
        dataBase.close()
    }

    override fun restore(reader: JsonReader) {
        reader.beginObject()

        val n1 = reader.nextName()
        if (n1 != "version") {
            throw RuntimeException("Unknown value $n1")
        }
        val version = reader.nextInt()

        val n2 = reader.nextName()
        if (n2 != "content") {
            throw RuntimeException("Unknown value $n2")
        }

        val restoreDatabaseName = "restoreDatabase"

        // delete if file already exists
        val restoreDatabaseFile = context.getDatabasePath(restoreDatabaseName)
        if (restoreDatabaseFile.exists()) {
            DatabaseUtil.deleteRoomDatabase(context, restoreDatabaseName)
        }

        // create new restore database
        val restoreDatabase = Room.databaseBuilder(context.applicationContext, clazz, restoreDatabaseName).build()
        val db = restoreDatabase.openHelper.writableDatabase

        db.beginTransaction()
        db.version = version

        // make sure no tables are in the database
        DatabaseUtil.deleteTables(db)

        // create database from backup
        DatabaseUtil.readDatabaseContent(reader, db)

        db.setTransactionSuccessful()
        db.endTransaction()
        db.close()

        reader.endObject()

        // copy file to correct location
        val actualDatabaseFile = context.getDatabasePath(name)

        DatabaseUtil.deleteRoomDatabase(context, name)

        FileUtil.copyFile(restoreDatabaseFile, actualDatabaseFile)
        Log.d(TAG, "Backup Restored")

        // delete restore database
        DatabaseUtil.deleteRoomDatabase(context, restoreDatabaseName)
    }
}

/**
 * This is a default implementation to backup and restore a SQLite database and should work for most setups.
 * Make sure that the backup-process is working fine on your own.
 *
 * @author Patrick Schneider
 */
open class SQLiteHelperConfig(val context: Context, val name: String): BackupDatabaseConfig {
    companion object {
        val TAG = PFModelApplication.instance.name
    }

    override fun backup(writer: JsonWriter) {
        writer.name("database")

        val database = getSupportSQLiteOpenHelper(context, name).readableDatabase

        writeDatabase(writer, database)
        database.close()
    }

    override fun restore(reader: JsonReader) {
        reader.beginObject()
        val n1: String = reader.nextName()
        if (n1 != "version") {
            throw RuntimeException("Unknown value $n1")
        }
        val version: Int = reader.nextInt()
        val n2: String = reader.nextName()
        if (n2 != "content") {
            throw RuntimeException("Unknown value $n2")
        }

        Log.d(TAG, "Restoring database...")
        val restoreDatabaseName = "restoreDatabase"

        // delete if file already exists
        val restoreDatabaseFile = context.getDatabasePath(restoreDatabaseName)
        if (restoreDatabaseFile.exists()) {
            DatabaseUtil.deleteRoomDatabase(context, restoreDatabaseName)
        }

        // create new restore database
        val db = DatabaseUtil.getSupportSQLiteOpenHelper(context, restoreDatabaseName, version).writableDatabase

        db.beginTransaction()
        db.version = version

        Log.d(TAG, "Copying database contents...")
        DatabaseUtil.readDatabaseContent(reader, db)
        Log.d(TAG, "succesfully read database")
        db.setTransactionSuccessful()
        db.endTransaction()
        db.close()

        reader.endObject()

        // copy file to correct location
        val actualDatabaseFile = context.getDatabasePath(name)

        DatabaseUtil.deleteRoomDatabase(context, name)

        FileUtil.copyFile(restoreDatabaseFile, actualDatabaseFile)
        Log.d(TAG, "Database Restored")

        // delete restore database
        DatabaseUtil.deleteRoomDatabase(context, restoreDatabaseName)
    }
}