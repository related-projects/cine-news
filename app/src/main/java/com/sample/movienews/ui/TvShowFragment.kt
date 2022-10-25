package com.sample.movienews.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sample.movienews.R
import com.sample.movienews.databinding.FragmentTvShowBinding

class TvShowFragment : Fragment() {
    private lateinit var binding: FragmentTvShowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTvShowBinding.inflate(inflater)
        return binding.root
    }

    companion object {
        private val TAG: String = TvShowFragment::class.java.simpleName
    }
}