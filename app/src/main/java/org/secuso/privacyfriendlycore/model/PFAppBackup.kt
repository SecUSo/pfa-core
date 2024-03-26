package org.secuso.privacyfriendlycore.model

import android.content.Context
import android.util.JsonReader
import android.util.JsonWriter

interface PFAppBackup {
    fun backup(writer: JsonWriter): JsonWriter = writer
    fun restore(key: String, reader: JsonReader, context: Context): JsonReader = reader
}