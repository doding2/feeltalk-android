package com.clonect.feeltalk.new_presentation.ui.activity

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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

        val navGraph = navController.navInflater.inflate(R.navigation.feeltalk_nav_graph)
        val startDestination = viewModel.run {
            if (!isLoggedIn.value)
                R.id.signUpFragment
            else if (!isUser.value)
                R.id.signUpFragment
            else if (!isUserCouple.value)
                R.id.signUpNavigationFragment
            else
                R.id.mainNavigationFragment
        }

        navGraph.setStartDestination(startDestination)

        navController.graph = navGraph
    }


    private fun tryAutoLogIn() = lifecycleScope.launch {
        viewModel.autoLogIn()
        viewModel.setReady()
        infoLog("로그인 된 계정이 없음")
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