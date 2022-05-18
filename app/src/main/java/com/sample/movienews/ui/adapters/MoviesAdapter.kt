package com.sample.movienews.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.movienews.databinding.HomeLinearViewItemBinding
import com.sample.movienews.models.Movie

class MoviesAdapter(
    private val listener: OnMovieClickedListener
): ListAdapter<Movie,
        MoviesAdapter.MovieViewHolder>(DiffCallback) {

    interface OnMovieClickedListener {
        fun onMovieClicked(movie: Movie)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {

        return MovieViewHolder(
            HomeLinearViewItemBinding.inflate(
                LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MovieViewHolder,
                                  position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
        holder.itemView.setOnClickListener {
            listener.onMovieClicked(movie)
        }
    }

    class MovieViewHolder(private var binding:
                                  HomeLinearViewItemBinding
    ):
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