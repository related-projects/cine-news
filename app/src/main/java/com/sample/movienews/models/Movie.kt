package com.sample.movienews.models

import androidx.room.*
import com.sample.movienews.models.typesconverters.MovieTypesConverters
import com.sample.movienews.models.typesconverters.ProductionCompaniesConverters
import com.sample.movienews.models.typesconverters.ProductionCountriesConverters
import com.sample.movienews.models.typesconverters.SpokenLanguagesConverters
import com.sample.movienews.utils.Utils.getFormattedPrice
import com.squareup.moshi.Json
import java.lang.StringBuilder
import java.util.*

@Entity(
    tableName = "movie_db",
    indices = [Index(value = ["id", "imdb_id"], unique = true)]
)
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val roomId: Long = 0,
    val id: Int?,
    val adult: Boolean?,
    @Json(name = "backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,
    val budget: Double?,
    @Json(name = "genres")
    @TypeConverters(MovieTypesConverters::class)
    var types: List<Type>?,
    @Json(name = "home_page")
    @ColumnInfo(name = "home_page")
    val homePage: String?,
    @Json(name = "imdb_id")
    @ColumnInfo(name = "imdb_id")
    val imdbId: String?,
    @Json(name = "original_language")
    @ColumnInfo(name = "original_language")
    val originalLanguage: String?,
    @Json(name = "original_title")
    @ColumnInfo(name = "original_title")
    val originalTitle: String?,
    @Json(name = "overview")
    val overView: String?,
    val popularity: Double?,
    @Json(name = "poster_path")
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    @Json(name = "production_companies")
    @ColumnInfo(name = "production_companies")
    @TypeConverters(ProductionCompaniesConverters::class)
    val productionCompanies: List<ProductionCompany>?,
    @Json(name = "production_countries")
    @ColumnInfo(name = "production_countries")
    @TypeConverters(ProductionCountriesConverters::class)
    val productionCountries: List<ProductionCountry>?,
    @Json(name = "release_date")
    @ColumnInfo(name = "release_date")
    val releaseDate: String?,
    val revenue: Double?,
    val runtime: Int?,
    @Json(name = "spoken_language")
    @ColumnInfo(name = "spoken_language")
    @TypeConverters(SpokenLanguagesConverters::class)
    val spokenLanguage: List<SpokenLanguage>?,
    val status: String?,
    val tagLine: String?,
    val title: String?,
    val video: Boolean?,
    @Json(name = "vote_average")
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double?,
    @Json(name = "vote_count")
    @ColumnInfo(name = "vote_count")
    val voteCount: Int?,
    var favorite: Boolean? = false
) {

    fun getFormattedTitle(): String = title ?: " - "

    fun getFormattedOriginalTitle(): String = originalTitle ?: " - "

    fun getFormattedStatus(): String  = status ?: " - "

    fun getFormattedVoteAverage(): String {
        return if (voteAverage == 0.0) {
            " - "
        } else {
            "$voteAverage"
        }
    }

    fun getFormattedBudget(): String {
        val res = if (budget?.equals(0.0) == true) {
            " - "
        } else {
            getFormattedPrice(budget ?: 0.0)
        }

        return res
    }

    fun getFormattedRevenue(): String {
        val res =  if (revenue?.equals(0.0) == true) {
            " - "
        } else {
            getFormattedPrice(revenue ?: 0.0)
        }

        return res
    }

    fun getFormattedGenres(): String {
        val spaceChar = " | "
        val genres = StringBuilder()

        if (types.isNullOrEmpty()) {
            return  " - "
        } else {
            types?.forEach { it ->
                genres.append(it.name).append(spaceChar)
            }
        }

        return genres.dropLast(3).toString()
    }

    fun getFormattedLanguage(): String {
        return if (originalLanguage.isNullOrEmpty()) {
            " - "
        } else {
            Locale(originalLanguage).displayLanguage
        }
    }

    fun getFormattedOverview(): String = overView ?: " - "
}