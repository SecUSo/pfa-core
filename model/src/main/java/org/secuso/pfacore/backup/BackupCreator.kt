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
import android.os.Build
import android.util.JsonWriter
import android.util.Log
import org.secuso.pfacore.application.PFApplication
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil
import org.secuso.privacyfriendlybackup.api.backup.PreferenceUtil
import org.secuso.privacyfriendlybackup.api.pfa.IBackupCreator
//import org.secuso.pfacore.model.PFApplication
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class BackupCreator : IBackupCreator {
    override fun writeBackup(context: Context, outputStream: OutputStream): Boolean {
        Log.d("PFA BackupCreator", "createBackup() started")
        val outputStreamWriter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
        } else {
            OutputStreamWriter(outputStream)
        }
        val writer = JsonWriter(outputStreamWriter)
        writer.setIndent("")

        try {
            writer.beginObject()

            val dataBase = DatabaseUtil.getSupportSQLiteOpenHelper(context, PFApplication.instance.databaseName).writableDatabase

            Log.d("PFA BackupCreator", "Writing database")
            writer.name("database")
            DatabaseUtil.writeDatabase(writer, dataBase)
            dataBase.close()

            Log.d("PFA BackupCreator", "Writing preferences")
            writer.name("preferences")
            val pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            PreferenceUtil.writePreferences(writer, pref)

            // Do other, app-specific backups
            Log.d("PFA BackupCreator", "Writing app-specific backups")
            PFApplication.instance.backup.backup(writer)
            Log.d("PFA BackupCreator", "finished app-specific backups")

            writer.endObject()

            writer.close()
        } catch (e: Exception) {
            Log.e("PFA BackupCreator", "Error occurred", e)
            e.printStackTrace()
            return false
        }

        Log.d("PFA BackupCreator", "Backup created successfully")

        return true
    }
}
