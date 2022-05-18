package com.sample.movienews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sample.movienews.models.Movie
import com.sample.movienews.models.typesconverters.MovieTypesConverters
import com.sample.movienews.models.typesconverters.ProductionCompaniesConverters
import com.sample.movienews.models.typesconverters.ProductionCountriesConverters
import com.sample.movienews.models.typesconverters.SpokenLanguagesConverters

/**
 * Room database to persist data for the app
 * Create a database with all necessary annotations,
 * methods, variables, etc.
 */
@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(
    MovieTypesConverters::class,
    ProductionCountriesConverters::class,
    ProductionCompaniesConverters::class,
    SpokenLanguagesConverters::class
)
abstract class MovieDb: RoomDatabase() {

    abstract fun getMovieDao(): MovieDao

    companion object {

        @Volatile
        private var INSTANCE: MovieDb?  = null

        fun getDatabase(context: Context): MovieDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDb::class.java,
                    "movie_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }

}