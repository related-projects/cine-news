package com.sample.movienews.models.typesconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sample.movienews.models.SpokenLanguage

class SpokenLanguagesConverters {
    /**
     * Convert a a list of [SpokenLanguage] to a Json
     */
    @TypeConverter
    fun fromSpokenLanguagesJson(spokenLanguages: List<SpokenLanguage>?)
    : String {
        return Gson().toJson(spokenLanguages) ?: ""
    }

    /**
     * Convert a json to a list of [SpokenLanguage]
     */
    @TypeConverter
    fun toSpokenLanguagesList(jsonTypes: String?): List<SpokenLanguage> {
        val notesType = object : TypeToken<List<SpokenLanguage>>() {}.type
        return Gson().fromJson(jsonTypes, notesType) ?: listOf()
    }
}