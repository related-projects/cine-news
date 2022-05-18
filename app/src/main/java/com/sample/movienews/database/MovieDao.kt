package com.sample.movienews.database

import androidx.room.*
import com.sample.movienews.models.Movie
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for database interaction
 */
@Dao
interface MovieDao {

    // Implement a method to read all the movies from the database
    @Query("SELECT * FROM movie_db")
    fun getMovies(): Flow<List<Movie>>

    // Implement a method to read a movie from the database by id
    @Query("SELECT * FROM movie_db WHERE id = :id")
    suspend fun getMovie(id: Int): Movie?

    // Implement a method to insert a movie into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    // Implement a method to update a movie that's already in the database
    @Update
    suspend fun update(movie: Movie)

    // Implement a method to delete a movie that's already in the database
    @Delete
    suspend fun delete(movie: Movie)
}