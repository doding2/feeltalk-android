package com.clonect.feeltalk.presentation.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ActivityMainBinding
import com.clonect.feeltalk.presentation.ui.FeeltalkApp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        waitForSplash()

        tryAutoLogIn()
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
                R.id.userNicknameInputFragment
            else if (!isUserCouple.value)
                R.id.coupleRegistrationFragment
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
    }

    private fun tryGoogleAutoLogIn(): Boolean {
        val googleAccount = GoogleSignIn.getLastSignedInAccount(this@MainActivity)
        googleAccount?.let {
            viewModel.autoGoogleLogIn()
            return true
        }
        return false
    }

    // TODO 얘네들 다 만들어야댐
    private fun tryKakaoAutoLogIn(): Boolean {
        return false
    }

    private fun tryNaverAutoLogIn(): Boolean {
        return false
    }

    private fun tryAppleAutoLogIn(): Boolean {
        return false
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