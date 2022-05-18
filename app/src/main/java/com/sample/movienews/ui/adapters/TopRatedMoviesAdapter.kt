package com.sample.movienews.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.movienews.databinding.HomeLinearViewItemBinding
import com.sample.movienews.models.Movie

class TopRatedMoviesAdapter: ListAdapter<Movie,
        TopRatedMoviesAdapter.TopRatedMovieViewHolder>(TopRatedMoviesAdapter) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopRatedMovieViewHolder {
        return TopRatedMovieViewHolder(
            HomeLinearViewItemBinding.inflate(
            LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: TopRatedMovieViewHolder,
                                  position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    class TopRatedMovieViewHolder(private var binding:
                                  HomeLinearViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movie = movie
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.posterPath == newItem.posterPath
        }
    }
}