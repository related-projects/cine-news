package com.sample.movienews.models.typesconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sample.movienews.models.ProductionCompany

class ProductionCompaniesConverters {
    /**
     * Convert a a list of [ProductionCompany] to a Json
     */
    @TypeConverter
    fun fromProductionCompaniesJson(productionCompanies: List<ProductionCompany>?)
    : String {
        return Gson().toJson(productionCompanies) ?: ""
    }

    /**
     * Convert a json to a list of [ProductionCompany]
     */
    @TypeConverter
    fun toProductionCompaniesList(jsonTypes: String?): List<ProductionCompany> {
        val notesType = object : TypeToken<List<ProductionCompany>>() {}.type
        return Gson().fromJson(jsonTypes, notesType) ?: listOf()
    }
}