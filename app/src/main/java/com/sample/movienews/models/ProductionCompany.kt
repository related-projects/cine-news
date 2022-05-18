package com.sample.movienews.models

import androidx.room.ColumnInfo

//@Entity(tableName = "production_company")
data class ProductionCompany (
    val id: Int?,
    val name: String?,
    val logo_path: String?,
    @ColumnInfo(name = "origin_country")
    val originCountry: String?
)