package com.clonect.feeltalk.presentation.ui.bottom_navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentBottomNavigationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomNavigationFragment : Fragment() {

    private lateinit var binding: FragmentBottomNavigationBinding
    private val viewModel: BottomNavigationViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.container_bottom_navigation) as NavHostFragment
        navController = navHostFragment.navController
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnHome.setOnClickListener {
                navigateToHomePage()
            }
            btnQuestionList.setOnClickListener {
                navigateToQuestionListPage()
            }
            btnSetting.setOnClickListener {
                navigateToSettingPage()
            }
        }

        checkPostNotificationsPermission { isGranted ->
            viewModel.run {
                if (getAppSettingsNotChanged()) {
                    enablePushNotificationEnabled(isGranted)
                    enableUsageInfoNotification(isGranted)
                }
            }
        }

        collectIsKeyPairsExist()
    }

    private fun navigateToKeyRestoringRequestPage() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_keyRestoringRequestFragment)
    }

    private fun collectIsKeyPairsExist() = CoroutineScope(Dispatchers.Main).launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.isKeyPairsExist.collectLatest { exist ->
                if (!exist) {
                    viewModel.disableKeyPairsExist()
                    navigateToKeyRestoringRequestPage()
                }
            }
        }
    }


    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.enablePushNotificationEnabled(isGranted)
        viewModel.enableUsageInfoNotification(isGranted)
    }

    private fun checkPostNotificationsPermission(onCompleted: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onCompleted(true)
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        val isAlreadyGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (isAlreadyGranted) {
            onCompleted(true)
            return
        }

        permissionLauncher.launch(permission)
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
        val navigateFragmentId = R.id.setting2Fragment
        if (navController.currentDestination?.id == navigateFragmentId)
            return

        popBottomNavBackStack(R.id.setting2Fragment)
        navController.navigate(R.id.setting2Fragment)
        correctClickedBottomButton(navigateFragmentId)
    }

    private fun popBottomNavBackStack(currentFragmentId: Int) {
        mutableListOf(
            R.id.homeFragment,
            R.id.questionListFragment,
            R.id.setting2Fragment
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

            val settingButtonDrawable = if (correctFragmentId == R.id.setting2Fragment)
                R.drawable.ic_users_couple_clicked
            else
                R.drawable.ic_users_couple

            btnHome.setImageResource(homeButtonDrawable)
            btnQuestionList.setImageResource(questionListButtonDrawable)
            btnSetting.setImageResource(settingButtonDrawable)
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