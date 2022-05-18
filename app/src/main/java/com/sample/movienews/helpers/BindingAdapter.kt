package com.sample.movienews.helpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.sample.movienews.R
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.utils.Constant.MOVIE_IMAGE_BASE_URL
import com.sample.movienews.viewModel.RequestsStatus

@BindingAdapter("postPath")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = "$MOVIE_IMAGE_BASE_URL$imgUrl"
            .toUri()
            .buildUpon()
            .scheme("https")
            .build()
        imgView
            .load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
                .scale(Scale.FILL)
        }
    }
}

@BindingAdapter("moviesData")
fun bindMoviesRecyclerView(recyclerView: RecyclerView,
                     data: List<Movie>?) {

    val adapter = recyclerView.adapter as MoviesAdapter?
    adapter?.submitList(data)

    if (data != null && data.isNotEmpty()) {
        recyclerView.visibility = View.VISIBLE
    } else {
        recyclerView.visibility = View.GONE
    }
}

@BindingAdapter("upComingMoviesData")
fun bindUpComingMoviesRecyclerView(recyclerView: RecyclerView,
                     data: List<Movie>?) {
    val adapter = recyclerView.adapter as MoviesAdapter
    adapter.submitList(data)
}

@BindingAdapter("popularMoviesData")
fun bindPopularMoviesRecyclerView(recyclerView: RecyclerView,
                     data: List<Movie>?) {
    val adapter = recyclerView.adapter as MoviesAdapter
    adapter.submitList(data)
}

@BindingAdapter("topRatedMoviesData")
fun bindTopRatedMoviesRecyclerView(recyclerView: RecyclerView,
                     data: List<Movie>?) {
    val adapter = recyclerView.adapter as MoviesAdapter
    adapter.submitList(data)
}

@BindingAdapter("recommendedMovies")
fun bindRecommendedMoviesRecyclerView(recyclerView: RecyclerView,
                                       data: List<Movie>?) {
    val adapter = recyclerView.adapter as MoviesAdapter
    adapter.submitList(data)
}

@BindingAdapter("moviesApiStatus")
fun bindStatus(statusImageView: ImageView,
               status: RequestsStatus?) {
    when (status) {
        RequestsStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        RequestsStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        RequestsStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        else -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
    }
}

@BindingAdapter("recommendedMoviesStatus")
fun bindRecommendedMoviesStatus(statusImageView: ImageView,
               status: RequestsStatus?) {
    when (status) {
        RequestsStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        RequestsStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        RequestsStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        else -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
    }
}

@BindingAdapter("searchedMovieApiStatus")
fun bindSearchMovieStatus(
    statusImageView: ImageView,
    status: RequestsStatus?) {

    when (status) {
        RequestsStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        RequestsStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        RequestsStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        else -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("favoriteMoviesStatus")
fun bindFavoriteMoviesStatus(tvWarning: TextView,
                             status: RequestsStatus) {
    when (status) {
        RequestsStatus.LOADING -> {
            tvWarning.visibility = View.GONE
        }

        RequestsStatus.ERROR -> {
            tvWarning.visibility = View.VISIBLE
        }

        RequestsStatus.DONE -> {
            tvWarning.visibility = View.GONE
        }
    }
}