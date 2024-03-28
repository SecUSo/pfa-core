/*
 This file is part of the application Privacy Friendly Notes.
 Privacy Friendly Notes is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.
 Privacy Friendly Notes is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Notes. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.pfacore.backup

import android.content.Context
import androidx.preference.PreferenceManager
import android.util.JsonReader
import android.util.Log
import androidx.room.Room
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil
import org.secuso.privacyfriendlybackup.api.backup.FileUtil
import org.secuso.privacyfriendlybackup.api.pfa.IBackupRestorer
import org.secuso.pfacore.model.PFApplication
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.system.exitProcess

class BackupRestorer : IBackupRestorer {

    @Throws(IOException::class)
    private fun readDatabase(reader: JsonReader, context: Context) {
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
        val restoreDatabase = Room.databaseBuilder(context.applicationContext, PFApplication.instance.Database::class.java, restoreDatabaseName).build()
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
        val actualDatabaseFile = context.getDatabasePath(PFApplication.instance.Database.name)

        DatabaseUtil.deleteRoomDatabase(context, PFApplication.instance.Database.name)

        FileUtil.copyFile(restoreDatabaseFile, actualDatabaseFile)
        Log.d("NoteRestore", "Backup Restored")

        // delete restore database
        DatabaseUtil.deleteRoomDatabase(context, restoreDatabaseName)
    }

    @Throws(IOException::class)
    private fun readPreferences(reader: JsonReader, context: Context) {
        reader.beginObject()

        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()

        while (reader.hasNext()) {
            val name = reader.nextName()
            val pref = PFApplication.instance.Settings.all.firstOrNull { it.data.key == name }
            if (pref == null) {
                throw RuntimeException("Unknown preference $name")
            } else {
                pref.restore(reader)
            }
        }

        editor.apply()

        reader.endObject()
    }

    override fun restoreBackup(context: Context, restoreData: InputStream): Boolean {
        try {
            val isReader = InputStreamReader(restoreData)
            val reader = JsonReader(isReader)

            // START
            reader.beginObject()

            while (reader.hasNext()) {
                when (val type = reader.nextName()) {
                    "database" -> readDatabase(reader, context)
                    "preferences" -> readPreferences(reader, context)
                    else -> PFApplication.instance.Backup.restore(type, reader, context)
                }
            }

            reader.endObject()

            // END

            // stop app to trigger migration on wakeup
            Log.d("NoteRestore", "Restore completed successfully.")
            exitProcess(0)
        } catch (e: Exception) {
            return false
        }
    }
}
