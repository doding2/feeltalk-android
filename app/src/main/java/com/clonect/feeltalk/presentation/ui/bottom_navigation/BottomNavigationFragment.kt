package com.clonect.feeltalk.presentation.ui.bottom_navigation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentBottomNavigationBinding
import com.clonect.feeltalk.presentation.service.FirebaseCloudMessagingService
import com.clonect.feeltalk.presentation.utils.delegates.PostNotificationsPermission
import com.clonect.feeltalk.presentation.utils.delegates.PostNotificationsPermissionImpl
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavigationFragment : Fragment(), PostNotificationsPermission by PostNotificationsPermissionImpl() {

    private lateinit var binding: FragmentBottomNavigationBinding
    private val viewModel: BottomNavigationViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.container_bottom_navigation) as NavHostFragment
        navController = navHostFragment.navController

        initFirebase()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHome.setOnClickListener {
            navigateToHomePage()
        }
        binding.btnQuestionList.setOnClickListener {
            navigateToQuestionListPage()
        }
        binding.btnSetting.setOnClickListener {
            navigateToSettingPage()
        }


        createNotificationChannel()
        checkPostNotificationsPermission()

    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.i("BottomNavFragment", "FcmToken: $it")
            FirebaseMessaging.getInstance().subscribeToTopic("Android")
        }
    }


    private fun navigateToHomePage() {
        val navigateFragmentId = R.id.homeFragment
        if (navController.currentDestination?.id == navigateFragmentId)
            return

        popBottomNavBackStack(R.id.homeFragment)
        navController.navigate(R.id.homeFragment)
        correctClickedBottomButton(navigateFragmentId)
    }

    private fun navigateToQuestionListPage() {
        val navigateFragmentId = R.id.questionListFragment
        if (navController.currentDestination?.id == navigateFragmentId)
            return

        popBottomNavBackStack(R.id.questionListFragment)
        navController.navigate(R.id.questionListFragment)
        correctClickedBottomButton(navigateFragmentId)
    }

    private fun navigateToSettingPage() {
        val navigateFragmentId = R.id.settingFragment
        if (navController.currentDestination?.id == navigateFragmentId)
            return

        popBottomNavBackStack(R.id.settingFragment)
        navController.navigate(R.id.settingFragment)
        correctClickedBottomButton(navigateFragmentId)
    }

    private fun popBottomNavBackStack(currentFragmentId: Int) {
        mutableListOf(
            R.id.homeFragment,
            R.id.questionListFragment,
            R.id.settingFragment
        ).run {
            remove(currentFragmentId)
            forEach {
                navController.popBackStack(
                    destinationId = it,
                    inclusive = true
                )
            }
        }
    }

    private fun correctClickedBottomButton(correctFragmentId: Int) {
        binding.apply {
            val homeButtonDrawable = if (correctFragmentId == R.id.homeFragment)
                R.drawable.ic_home_button_clicked
            else
                R.drawable.ic_home_button

            val questionListButtonDrawable = if (correctFragmentId == R.id.questionListFragment)
                R.drawable.ic_chat_clicked
            else
                R.drawable.ic_chat

            val settingButtonDrawable = if (correctFragmentId == R.id.settingFragment)
                R.drawable.ic_users_couple_clicked
            else
                R.drawable.ic_users_couple

            btnHome.setImageResource(homeButtonDrawable)
            btnQuestionList.setImageResource(questionListButtonDrawable)
            btnSetting.setImageResource(settingButtonDrawable)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channelName = "연인의 알림"
            val channel = NotificationChannel(FirebaseCloudMessagingService.CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "띠링 띠링"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun clearScreenCapture() {
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onStart() {
        super.onStart()
        clearScreenCapture()
        navController.currentDestination?.id?.let {
            correctClickedBottomButton(it)
        }
    }
}