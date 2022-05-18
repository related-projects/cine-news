package com.sample.movienews.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sample.movienews.app.BaseApp
import com.sample.movienews.databinding.FragmentFavoritesBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.viewModel.MainViewModel
import com.sample.movienews.viewModel.MainViewModelFactory

class FavoritesFragment : Fragment(), MoviesAdapter.OnMovieClickedListener {

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as BaseApp).database.getMovieDao()
        )
    }

    private lateinit var binding: FragmentFavoritesBinding

    private lateinit var listener: OnFavoritesFragListener

    companion object {
        private val TAG = FavoritesFragment::class.java.simpleName
    }

    interface OnFavoritesFragListener {
        fun onFavoriteMovieClicked(movie: Movie)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData
        // with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the MainViewModel
        binding.viewModel = viewModel

        binding.favoriteMovies.adapter = MoviesAdapter(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {

            viewModel.getListOfFavoriteMovies()

        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            if (context is OnFavoritesFragListener) {
                listener = context
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onMovieClicked(movie: Movie) {
        listener.onFavoriteMovieClicked(movie)
    }
}