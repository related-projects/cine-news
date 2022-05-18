package com.sample.movienews.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.movienews.databinding.HomeLinearViewItemBinding
import com.sample.movienews.models.Movie

class UpComingMoviesAdapter: ListAdapter<Movie,
        UpComingMoviesAdapter.UpComingMovieViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpComingMovieViewHolder {
        return UpComingMovieViewHolder(
            HomeLinearViewItemBinding.inflate(
            LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: UpComingMovieViewHolder,
                                  position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    class UpComingMovieViewHolder(private var binding:
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