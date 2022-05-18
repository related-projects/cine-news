package com.sample.movienews.models.typesconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sample.movienews.models.Type

class MovieTypesConverters {
    /**
     * Convert a a list of [Type] to a Json
     */
    @TypeConverter
    fun fromTypesJson(types: List<Type>?): String {
        return Gson().toJson(types) ?: ""
    }

    /**
     * Convert a json to a list of [Type]
     */
    @TypeConverter
    fun toTypesList(jsonTypes: String?): List<Type> {
        val notesType = object : TypeToken<List<Type>>() {}.type
        return Gson().fromJson(jsonTypes, notesType) ?: listOf()
    }
}