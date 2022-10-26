package com.sample.movienews.ui.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.sample.movienews.databinding.ActivityWatcherBinding

class WatcherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWatcherBinding
    private lateinit var name: String
    private lateinit var link: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatcherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initListener()
    }



    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        try {
//            name = "${intent.getStringExtra(NAME)}"
//            link = "${intent.getStringExtra(SITE)}"

            binding.topAppBar.title = name

            binding.videoPlayer.resumeTimers()
            binding.videoPlayer.webChromeClient = object: WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    return true
                }
            }
            binding.videoPlayer.settings.apply {
                javaScriptEnabled = true
                allowFileAccess = true
                domStorageEnabled = true
//                pluginState = WebSettings.PluginState.ON
                mediaPlaybackRequiresUserGesture = false
                defaultTextEncodingName = "utf-8"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    safeBrowsingEnabled = false
                } else {
                    CookieManager.getInstance()
                        .acceptThirdPartyCookies(binding.videoPlayer)
                }
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            binding.videoPlayer.apply {
                clearCache(true)
            }
            WebView.setWebContentsDebuggingEnabled(true)
            binding.videoPlayer.loadUrl(link)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.videoPlayer.reload()
            },100)
            binding.videoPlayer.webViewClient = object: WebViewClient() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    try {
                        view?.loadUrl("${request?.url}")
                    } catch (e: Exception) {
                        Log.e(TAG, "${e.message}")
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    try {
                        Log.e(TAG, "${error?.description}")
                        binding.videoPlayer.reload()
                    } catch (e: Exception) {
                        Log.e(TAG, "${e.message}")
                    }
                    super.onReceivedError(view, request, error)
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)

                    Log.e(TAG, "$errorResponse")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun initListener() {
        try {
            binding.topAppBar.setNavigationOnClickListener {
                this.onBackPressed()
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onStop() {
        try {
            binding.videoPlayer.apply {
                removeAllViews()
                clearHistory()
                clearCache(true)
                onPause()
                removeAllViews()
                destroy()
            }
        } catch (e: Exception) {
           Log.e(TAG, "${e.message}")
        }
        super.onStop()
    }

    companion object {
        private val TAG: String = WatcherActivity::class.java.simpleName
    }
}