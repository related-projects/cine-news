package com.sample.movienews.ui.activities

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.webkit.*
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import com.sample.movienews.databinding.ActivityWatcherBinding
import com.sample.movienews.utils.Constant.CINE_NEWS_API_KEY
import com.sample.movienews.utils.Constant.SITE


class WatcherActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    private lateinit var binding: ActivityWatcherBinding
    private lateinit var name: String
    private lateinit var videoKey: String
    private var mPlayer: YouTubePlayer? = null

    companion object {
        private val TAG: String = WatcherActivity::class.java.simpleName

        /** Intent action for stopwatch controls from Picture-in-Picture mode.  */
        private const val ACTION_STOPWATCH_CONTROL = "stopwatch_control"

        /** Intent extra for stopwatch controls from Picture-in-Picture mode.  */
        private const val EXTRA_CONTROL_TYPE = "control_type"
        private const val CONTROL_TYPE_CLEAR = 1
        private const val CONTROL_TYPE_START_OR_PAUSE = 2

        private const val REQUEST_CLEAR = 3
        private const val REQUEST_START_OR_PAUSE = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatcherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoKey = "${intent.getStringExtra(SITE)}"
    }

    override fun onResume() {
        super.onResume()
        try {
            binding.youtubePlayer.initialize(CINE_NEWS_API_KEY, this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val supportsPIP = packageManager.hasSystemFeature(
                    PackageManager.FEATURE_PICTURE_IN_PICTURE)
                if (supportsPIP) {
                    updatePictureInPictureParams()?.let {
                        this.enterPictureInPictureMode(it)
                    }
                }
            }
        } catch(e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    // This is called when the activity gets into or out of the picture-in-picture mode.
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    /**
     * Updates the parameters of the picture-in-picture mode for this activity based on the current
     * [started] state of the stopwatch.
     */
    private fun updatePictureInPictureParams(): PictureInPictureParams? {
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val width = 350
            val height = 200
            PictureInPictureParams.Builder()
                // Set the aspect ratio of the picture-in-picture mode.
                .setAspectRatio(Rational(width, height))
                .setSourceRectHint(Rect())
                .build()
        } else {
            Log.e(TAG, "PictureInPictureParams not supported")
//            TODO: "VERSION.SDK_INT < O"
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
        override fun onPaused() {
            mPlayer?.play()
        }
        override fun onPlaying() {}
        override fun onSeekTo(arg0: Int) {}
        override fun onStopped() {
            mPlayer?.setFullscreen(true)
            mPlayer?.play()
        }
    }

}