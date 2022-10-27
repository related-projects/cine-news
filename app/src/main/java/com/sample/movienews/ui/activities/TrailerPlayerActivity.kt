package com.sample.movienews.ui.activities

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.webkit.*
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import com.sample.movienews.databinding.ActivityTrailerPlayerBinding
import com.sample.movienews.utils.Constant.CINE_NEWS_API_KEY
import com.sample.movienews.utils.Constant.SITE


class TrailerPlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private lateinit var binding: ActivityTrailerPlayerBinding
    private lateinit var name: String
    private lateinit var videoKey: String
    private var mPlayer: YouTubePlayer? = null

    companion object {
        private val TAG: String = TrailerPlayerActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrailerPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoKey = "${intent.getStringExtra(SITE)}"
        binding.youtubePlayer.initialize(CINE_NEWS_API_KEY, this)
    }

    // This is called when the activity gets into or out of the picture-in-picture mode.
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        try {
            binding.youtubePlayer.post {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val supportsPIP = packageManager.hasSystemFeature(
                        PackageManager.FEATURE_PICTURE_IN_PICTURE)
                    if (supportsPIP) {
                        updatePictureInPictureParams()?.let {
                            this.enterPictureInPictureMode(it)
                        }
                    }
                }
            }
        } catch(e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    /**
     * Updates the parameters of the picture-in-picture mode for this activity based on the current
     * [started] state of the stopwatch.
     */
    private fun updatePictureInPictureParams(): PictureInPictureParams? {
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val width = binding.youtubePlayer.width
            val height = binding.youtubePlayer.height
            PictureInPictureParams.Builder()
                .setAspectRatio(Rational(width, height))
                .build()
        } else {
            Log.e(TAG, "PictureInPictureParams not supported")
            null
        }
        params?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setPictureInPictureParams(it)
            }
        }
        return params
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        player: YouTubePlayer?,
        b: Boolean
    ) {
        try {
            /** add listeners to YouTubePlayer instance **/
            player?.setPlaybackEventListener(playbackEventListener)
            player?.loadVideo(videoKey)
            mPlayer = player
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        result: YouTubeInitializationResult?
    ) {
        Log.e(TAG, "${result?.toString()}")
    }

    private val playbackEventListener: PlaybackEventListener = object : PlaybackEventListener {
        override fun onBuffering(arg0: Boolean) {}
        override fun onPaused() {}
        override fun onPlaying() {}
        override fun onSeekTo(arg0: Int) {}
        override fun onStopped() {
            mPlayer?.setFullscreen(true)
        }
    }

}