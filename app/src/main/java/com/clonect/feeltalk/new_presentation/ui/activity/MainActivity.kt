package com.clonect.feeltalk.new_presentation.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLink
import androidx.navigation.fragment.NavHostFragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ActivityMainBinding
import com.clonect.feeltalk.databinding.FragmentMainNavigationBinding
import com.clonect.feeltalk.new_presentation.ui.FeeltalkApp
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationFragment
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val mainNavViewModel: MainNavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        onTransformationStartContainer()
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        } else {
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        val showChat = intent?.getBooleanExtra("showChat", false) ?: false
        if (showChat) {
            mainNavViewModel.setShowChatNavigation(showChat)
            intent?.extras?.clear()
        }

//        waitForSplash()
//        tryAutoLogIn()
//
//        checkPostNotificationsPermission {
//            viewModel.enablePushNotificationEnabled(it)
//        }
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
        val bundle: Bundle? = null
        val startDestination = viewModel.run {
            if (isNetworkErrorOccurred.value)
                R.id.networkErrorFragment
            else if (isServerDown.value)
                R.id.serverDownFragment
            else if (!isLoggedIn.value)
                R.id.onboardingFragment
            else if (!isUser.value)
                R.id.onboardingFragment
            else if (!isUserCouple.value)
                R.id.signUpNavigationFragment
            else if (isAccountLocked.value)
                R.id.passwordFragment
            else
                R.id.mainNavigationFragment
        }

        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }


    private fun tryAutoLogIn() = lifecycleScope.launch {
//        viewModel.autoLogIn()
        viewModel.getUserStatus()
        viewModel.setReady()
    }



    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.enablePushNotificationEnabled(isGranted)
    }

    private fun checkPostNotificationsPermission(onCompleted: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onCompleted(true)
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        val isAlreadyGranted = ContextCompat.checkSelfPermission(
            applicationContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (isAlreadyGranted) {
            onCompleted(true)
            return
        }

        permissionLauncher.launch(permission)
    }

    override fun onResume() {
        super.onResume()
        FeeltalkApp.onAppScreenResumed()
    }

    override fun onPause() {
        super.onPause()
        FeeltalkApp.onAppScreenPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        FeeltalkApp.onAppDestroyed()
    }
}