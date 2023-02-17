package com.clonect.feeltalk.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentDialog
import androidx.activity.OnBackPressedCallback
import com.clonect.feeltalk.BuildConfig
import java.util.*

class AppleSignInDialog(
    context: Context,
    private val onSuccess: (email: String) -> Unit
): ComponentDialog(context) {

    private lateinit var webView: WebView
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWebView()
        setContentView(webView)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView = WebView(context).apply {
            isHorizontalScrollBarEnabled = false
            isVerticalFadingEdgeEnabled = false
            clearSslPreferences()

            settings.javaScriptEnabled = true
            webViewClient = AppleWebViewClient()

            val appleAuthUrl = StringBuilder().run {
                append(BuildConfig.APPLE_AUTH_BASE_URL)
                append("?response_type=code&v=1.1.6&response_mode=form_post&client_id=")
                append(BuildConfig.APPLE_AUTH_CLIENT_ID)
                append("&scope=name%20email")
                append("&state=")
                append(UUID.randomUUID().toString())
                append("&redirect_uri=")
                append(BuildConfig.APPLE_AUTH_REDIRECT_URI)
                toString()
            }
            loadUrl(appleAuthUrl)
        }
    }


    inner class AppleWebViewClient: WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url ?: return false)
            return true
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?,
        ): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            infoLog("Apple Sign In Result: ${url}")

            if (url != null) {
                val uri = Uri.parse(url)
                val status = uri.getQueryParameter("status")

                infoLog("Apple Sign In status: $status")

                if (status == "fail") {
                    Toast.makeText(context, "애플 로그인에 실패했습니다", Toast.LENGTH_SHORT).show()
                    infoLog("Fail to sign up with apple")
                    dismiss()
                }

                if (status == "success") {
                    val email = uri.getQueryParameter("email")
                    email?.let { onSuccess(it) }
                    dismiss()
                }
            }

            val displayRectangle = Rect()
            val window = window
            window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)

            val layoutParams = view?.layoutParams
            layoutParams?.height = (displayRectangle.height() * 0.9f).toInt()
            view?.layoutParams = layoutParams
        }

    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
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