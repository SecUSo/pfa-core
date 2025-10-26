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
import androidx.preference.PreferenceManager
import org.secuso.pfacore.application.PFModelApplication
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.privacyfriendlybackup.api.backup.PreferenceUtil
import org.secuso.privacyfriendlybackup.api.pfa.IBackupCreator
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

            Log.d("PFA BackupCreator", "Creating app backup")
            for (backupManager in PFModelApplication.instance.appBackup) {
                Log.d("PFA BackupCreator", "Creating backup for ${backupManager.key}")
                backupManager.backup(writer)
                Log.d("PFA BackupCreator", "Finished backup for ${backupManager.key}")
            }
            Log.d("PFA BackupCreator", "Finished app backup")

            Log.d("PFA BackupCreator", "Backup Preference")
            val excludedKeys = PFModelApplication.instance.data
                .preferences
                .all
                .filter { !it.backup }
                .map { it.key }
                .toTypedArray()
            Log.d("PFA BackupCreator", "Found ${excludedKeys.size} keys to exclude. \n ${excludedKeys.joinToString(",")}")
            writer.name("preferences")
            PreferenceUtil.writePreferences(writer, PreferenceManager.getDefaultSharedPreferences(context), excludedKeys)

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
