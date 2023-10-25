package com.clonect.feeltalk.new_presentation.ui.signUp.authHelper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.activity.ComponentDialog
import androidx.activity.OnBackPressedCallback
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.DialogAppleSignInBinding
import com.clonect.feeltalk.presentation.utils.infoLog
import kotlinx.coroutines.*
import java.util.*

class AppleSignInDialog(
    context: Context,
    private val onCompleted: (state: String?) -> Unit,
): ComponentDialog(context) {

    private lateinit var binding: DialogAppleSignInBinding
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var state: String

    private val timerJob = SupervisorJob()
    private val timerScope = CoroutineScope(Dispatchers.Default + timerJob)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAppleSignInBinding.inflate(layoutInflater)
        initWebView()
        setContentView(binding.root)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webView.apply {
            isHorizontalScrollBarEnabled = false
            isVerticalFadingEdgeEnabled = false
            clearSslPreferences()

            settings.javaScriptEnabled = true
            webViewClient = AppleWebViewClient()

            state = UUID.randomUUID().toString()

            val appleAuthUrl = StringBuilder().run {
                append(Constants.APPLE_AUTH_BASE_URL)
                append("?response_type=code&v=1.1.6&response_mode=form_post&client_id=")
                append(Constants.APPLE_AUTH_CLIENT_ID)
                append("&scope=name%20email")
                append("&state=")
                append(state)
                append("&redirect_uri=")
                append(Constants.APPLE_AUTH_REDIRECT_URI)
                toString()
            }
            loadUrl(appleAuthUrl)
        }
    }


    inner class AppleWebViewClient: WebViewClient() {

        @Deprecated("Deprecated in Java")
        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            if (url == BuildConfig.APPLE_AUTH_REDIRECT_URI) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.flLoading.visibility = View.VISIBLE
                }
            }
            return super.shouldInterceptRequest(view, url)
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?,
        ): WebResourceResponse? {
            val url = request?.url?.toString()
            if (url == BuildConfig.APPLE_AUTH_REDIRECT_URI) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.flLoading.visibility = View.VISIBLE
                }
            }
            return super.shouldInterceptRequest(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            infoLog("Apple Sign In Page Loading Finished: ${url}")

            if (url == BuildConfig.APPLE_AUTH_REDIRECT_URI) {
                binding.flLoading.visibility = View.VISIBLE
                onCompleted(state)
                dismiss()
            } else {
                binding.flLoading.visibility = View.GONE
            }

            binding.webView.progress

            val displayRectangle = Rect()
            val window = window
            window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)

            val layoutParams = view?.layoutParams
            layoutParams?.height = (displayRectangle.height() * 0.9f).toInt()
            view?.layoutParams = layoutParams
        }
    }


    private fun startCoroutineTimer(
        delayMillis: Long = 5000,
    ) = timerScope.launch(Dispatchers.IO) {
        infoLog("started")
        delay(delayMillis)
        onCompleted(null)
        dismiss()
    }

    private fun stopCoroutineTimer() {
        infoLog("canceled")
        timerJob.cancel()
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    dismiss()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onBackCallback.remove()
    }
}