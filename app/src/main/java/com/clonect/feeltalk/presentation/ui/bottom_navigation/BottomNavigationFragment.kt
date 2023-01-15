package com.clonect.feeltalk.presentation.ui.bottom_navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentBottomNavigationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavigationFragment : Fragment() {

    private lateinit var binding: FragmentBottomNavigationBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.container_bottom_navigation) as NavHostFragment
        navController = navHostFragment.navController
        navHostFragment.navController.clearBackStack(R.id.homeFragment)
        navHostFragment.navController.clearBackStack(R.id.questionListFragment)

        binding.btnHome.setOnClickListener {
            navigateToHomePage()
        }
        binding.btnQuestionList.setOnClickListener {
            navigateToQuestionListPage()
        }
        binding.btnSetting.setOnClickListener {
            navigateToSettingPage()
        }

        return binding.root
    }

    private fun navigateToHomePage() {
        if (navController.currentDestination?.id == R.id.homeFragment)
            return

        navController.popBackStack(R.id.questionListFragment, true)
        navController.popBackStack(R.id.settingFragment, true)

        navController.navigate(R.id.homeFragment)

        binding.btnHome.setImageResource(R.drawable.ic_home_button_clicked)
        binding.btnQuestionList.setImageResource(R.drawable.ic_chat)
        binding.btnSetting.setImageResource(R.drawable.ic_users_couple)
    }

    private fun navigateToQuestionListPage() {
        if (navController.currentDestination?.id == R.id.questionListFragment)
            return

        navController.popBackStack(R.id.homeFragment, true)
        navController.popBackStack(R.id.settingFragment, true)

        navController.navigate(R.id.questionListFragment)

        binding.btnHome.setImageResource(R.drawable.ic_home_button)
        binding.btnQuestionList.setImageResource(R.drawable.ic_chat_clicked)
        binding.btnSetting.setImageResource(R.drawable.ic_users_couple)
    }

    private fun navigateToSettingPage() {
        if (navController.currentDestination?.id == R.id.settingFragment)
            return

        navController.popBackStack(R.id.homeFragment, true)
        navController.popBackStack(R.id.questionListFragment, true)

        navController.navigate(R.id.settingFragment)

        binding.btnHome.setImageResource(R.drawable.ic_home_button)
        binding.btnQuestionList.setImageResource(R.drawable.ic_chat)
        binding.btnSetting.setImageResource(R.drawable.ic_users_couple_clicked)
    }

}