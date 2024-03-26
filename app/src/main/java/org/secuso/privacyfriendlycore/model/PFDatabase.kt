package org.secuso.privacyfriendlycore.model

import androidx.room.RoomDatabase

abstract class PFDatabase: RoomDatabase() {
    abstract val name: String
}