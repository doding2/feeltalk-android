package com.clonect.feeltalk.presentation.ui.activity

import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ActivityMainBinding
import com.clonect.feeltalk.presentation.ui.FeeltalkApp
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLoginState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        collectToast()

        waitForSplash()

        tryAutoLogIn()

    }

    private fun collectToast() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.toast.collectLatest {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun waitForSplash() {
        binding.root.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (viewModel.isReady.value) {
                        setNavigationGraph()
                        binding.root.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
                    }
                    return false
                }
            }
        )
    }

    private fun setNavigationGraph() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.overall_nav_graph)
        val startDestination = viewModel.run {
            if (!isLoggedIn.value)
                R.id.signUpFragment
            else if (!isUserInfoEntered.value)
                R.id.userGenderInputFragment
            else if (!isUserCouple.value)
                R.id.guideFragment
            else
                R.id.bottomNavigationFragment
        }

        navGraph.setStartDestination(startDestination)

        navController.graph = navGraph
    }


    private fun tryAutoLogIn() = lifecycleScope.launch {
        if (tryGoogleAutoLogIn()) return@launch
        if (tryKakaoAutoLogIn()) return@launch
        if (tryNaverAutoLogIn()) return@launch
        if (tryAppleAutoLogIn()) return@launch

        viewModel.setReady()
        infoLog("로그인 된 계정이 없음")
    }

    private fun tryGoogleAutoLogIn(): Boolean {
        val googleAccount = GoogleSignIn.getLastSignedInAccount(this@MainActivity)
        googleAccount?.let {
            viewModel.autoGoogleLogIn()
            return true
        }
        return false
    }

    private suspend fun tryKakaoAutoLogIn(): Boolean = suspendCoroutine { continuation ->
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error == null) {
                    viewModel.autoKakaoLogIn()
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        } else {
            continuation.resume(false)
        }
    }

    private fun tryNaverAutoLogIn(): Boolean {
        try {
            val state = NaverIdLoginSDK.getState()
            if (state == NidOAuthLoginState.OK) {
                viewModel.autoNaverLogIn()
                return true
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    private suspend fun tryAppleAutoLogIn(): Boolean {
        val isLoggedIn = viewModel.checkIsAppleLoggedIn()
        if (isLoggedIn) {
            viewModel.autoAppleLogIn()
        }
        return isLoggedIn
    }



    override fun onResume() {
        super.onResume()
        FeeltalkApp.onAppResumed()
    }

    override fun onPause() {
        super.onPause()
        FeeltalkApp.onAppPaused()
    }


}