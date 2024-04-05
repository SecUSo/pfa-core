package org.secuso.pfacore.backup

import android.util.JsonReader
import kotlinx.serialization.json.Json

typealias Restorer<T> = (JsonReader) -> T

val stringRestorer: Restorer<String> = { it.nextString() }
val booleanRestorer: Restorer<Boolean> = { it.nextBoolean() }
val doubleRestorer: Restorer<Double> = { it.nextDouble() }
val floatRestorer: Restorer<Float> = { it.nextDouble().toFloat() }
val intRestorer: Restorer<Int> = { it.nextInt() }

inline fun <reified T> serializableRestorer(): Restorer<T> = {
    Json.decodeFromString<T>(it.nextString())
}

fun <T> restoreList(restorer: Restorer<T>): Restorer<List<T>> = {
    it.beginArray()
    val list = mutableListOf<T>()
    while (it.hasNext()) {
        list.add(restorer(it))
    }
    list
}

