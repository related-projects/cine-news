package com.sample.movienews.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sample.movienews.R
import com.sample.movienews.app.BaseApp
import com.sample.movienews.databinding.FragmentFavoritesBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.utils.Constant
import com.sample.movienews.utils.Constant.MOVIES_TYPE_KEY
import com.sample.movienews.utils.Constant.MOVIE_ID_KEY
import com.sample.movienews.utils.Constant.PARENT_FRAG
import com.sample.movienews.viewModel.MainViewModel
import com.sample.movienews.viewModel.MainViewModelFactory

class FavoritesFragment : Fragment(), MoviesAdapter.OnMovieClickedListener {

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as BaseApp).database.getMovieDao()
        )
    }

    private lateinit var binding: FragmentFavoritesBinding

    companion object {
        private val TAG = FavoritesFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData
        // with the lifecycle of this Fragment
        binding.apply {
            lifecycleOwner = this@FavoritesFragment
            viewModel = mainViewModel
            favoriteMovies.adapter = MoviesAdapter(this@FavoritesFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            mainViewModel.getListOfFavoriteMovies()
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onMovieClicked(movie: Movie) {
        val args = bundleOf(
            PARENT_FRAG to TAG,
            MOVIE_ID_KEY to "${movie.id}"
        )
        findNavController().navigate(
            R.id.action_favoritesFragment_to_movieDetailsFragment,
            args
        )
    }
}