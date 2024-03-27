package org.secuso.pfacore.model

import androidx.room.RoomDatabase

abstract class PFDatabase: RoomDatabase() {
    abstract val name: String
}