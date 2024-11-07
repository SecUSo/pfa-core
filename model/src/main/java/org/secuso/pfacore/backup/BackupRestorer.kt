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

import android.annotation.SuppressLint
import android.content.Context
import android.util.JsonReader
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.Room
import org.secuso.pfacore.application.PFModelApplication
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.privacyfriendlybackup.api.backup.DatabaseUtil
import org.secuso.privacyfriendlybackup.api.backup.FileUtil
import org.secuso.privacyfriendlybackup.api.pfa.IBackupRestorer
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.system.exitProcess

class BackupRestorer : IBackupRestorer {

    @SuppressLint("ApplySharedPref")
    @Throws(IOException::class)
    private fun readPreferences(reader: JsonReader, context: Context) {
        reader.beginObject()

        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()

        while (reader.hasNext()) {
            val name = reader.nextName()
            val pref = PFModelApplication.instance.data.settings.all.map { it.setting.data }.filterIsInstance<Preferable<*>>().firstOrNull { it.key == name }
            if (pref == null) {
                throw RuntimeException("Unknown preference $name")
            } else {
                pref.restore(reader)
            }
        }

        // To ensure that the changes made by the restoration are persisted,
        // commit the changes directly to disk instead of using apply to defer that write.
        editor.commit()

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
                    "database" -> PFModelApplication.instance.database!!.restore(reader)
                    "preferences" -> readPreferences(reader, context)
                    else -> PFModelApplication.instance.backup.restore(type, reader, context)
                }
            }

            reader.endObject()

            // END

            // stop app to trigger migration on wakeup
            Log.d("NoteRestore", "Restore completed successfully.")
            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
