package com.sample.movienews.models.typesconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sample.movienews.models.ProductionCountry

class ProductionCountriesConverters {
    /**
     * Convert a a list of [ProductionCountry] to a Json
     */
    @TypeConverter
    fun fromProductionCountriesJson(productionCountries: List<ProductionCountry>?)
    : String {
        return Gson().toJson(productionCountries) ?: ""
    }

    /**
     * Convert a json to a list of [ProductionCountry]
     */
    @TypeConverter
    fun toProductionCountriesList(jsonTypes: String?): List<ProductionCountry> {
        val notesType = object : TypeToken<List<ProductionCountry>>() {}.type
        return Gson().fromJson(jsonTypes, notesType) ?: listOf()
    }
}