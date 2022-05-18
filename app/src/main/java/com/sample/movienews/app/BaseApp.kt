package com.sample.movienews.app

import android.app.Application
import com.sample.movienews.database.MovieDb

class BaseApp: Application() {

    val database: MovieDb by lazy {
        MovieDb.getDatabase(this)
    }
}