package org.secuso.pfacore.backup

import android.util.JsonReader

typealias Restorer<T> = (JsonReader) -> T

val stringRestorer: Restorer<String> = { it.nextString() }
val booleanRestorer: Restorer<Boolean> = { it.nextBoolean() }
val doubleRestorer: Restorer<Double> = { it.nextDouble() }
val intRestorer: Restorer<Int> = { it.nextInt() }

fun <T> restoreList(restorer: Restorer<T>): Restorer<List<T>> = {
    it.beginArray()
    val list = mutableListOf<T>()
    while (it.hasNext()) {
        list.add(restorer(it))
    }
    list
}

