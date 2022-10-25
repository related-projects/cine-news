package com.sample.movienews

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sample.movienews.databinding.ActivityMainBinding
import com.sample.movienews.ui.FavoriteContainerFragment
import com.sample.movienews.ui.HomeContainerFragment
import com.sample.movienews.ui.SettingsFragment
import com.sample.movienews.ui.TvShowFragment

class MainActivity : AppCompatActivity() {

    // Binding object instance corresponding to the activity_main.xml layout
    // when the view hierarchy is attached to the fragment.
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentFragment: Fragment

    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the HomeFragment as the current fragment
        currentFragment = HomeContainerFragment()

    }

    override fun onResume() {
        super.onResume()
        try {
            init()
            initListener()
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun init() {
        try {
            // Load the HomeFragment by default
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, currentFragment)
                .commit()

            // Change bottomNavigationView shape
//            val shapeDrawable : MaterialShapeDrawable =
//                binding.bottomNavigationBar.background as MaterialShapeDrawable
//            shapeDrawable.shapeAppearanceModel = shapeDrawable.shapeAppearanceModel
//                .toBuilder()
//                .setAllCorners(CornerFamily.ROUNDED, 65.0F)
//                .build()
//            shapeDrawable.elevation = 5F

        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun initListener() {
        try {
            binding.bottomNavigationBar.setOnItemSelectedListener { item ->

                when (item.itemId) {
                    R.id.home -> {
                        currentFragment = HomeContainerFragment()
                    }
                    R.id.tv_show -> {
                        currentFragment = TvShowFragment()
                    }
                    R.id.favorite -> {
                        currentFragment = FavoriteContainerFragment()
                    }
                    R.id.settings -> {
                        currentFragment = SettingsFragment()
                    }
                }

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame, currentFragment)
                    .commit()

                true
            }

            binding.bottomNavigationBar.setOnItemReselectedListener {  }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }
}