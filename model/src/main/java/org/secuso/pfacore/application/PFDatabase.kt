package org.secuso.pfacore.application

import androidx.room.RoomDatabase

abstract class PFDatabase : RoomDatabase() {
    abstract val name: String
}