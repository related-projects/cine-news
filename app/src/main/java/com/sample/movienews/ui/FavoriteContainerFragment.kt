package com.sample.movienews.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sample.movienews.R
import com.sample.movienews.databinding.FragmentFavoriteContainerBinding
import com.sample.movienews.databinding.FragmentFavoritesBinding

class FavoriteContainerFragment: Fragment() {
    private lateinit var binding: FragmentFavoriteContainerBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteContainerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val navHostFragment = childFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    companion object {
        private val TAG: String = FragmentFavoriteContainerBinding::class.java.simpleName
    }
}