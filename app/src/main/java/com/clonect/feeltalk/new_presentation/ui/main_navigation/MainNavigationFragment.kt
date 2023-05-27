package com.clonect.feeltalk.new_presentation.ui.main_navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentMainNavigationBinding
import com.clonect.feeltalk.new_presentation.ui.util.closeRootViewLayout
import com.clonect.feeltalk.new_presentation.ui.util.extendRootViewLayout
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars

class MainNavigationFragment : Fragment() {

    private lateinit var binding: FragmentMainNavigationBinding
    private val viewModel: MainNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainNavigationBinding.inflate(inflater, container, false)
        extendRootViewLayout(activity?.window)
        binding.root.setPadding(0, 0, 0, getNavigationBarHeight())
        setUpBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    
    //TODO 다른 fragment로 이동할때 setLightStatusBars(true, activity, binding.root) 꼭 호출시키기

    private fun navigateToChat() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_chatFragment)
        setLightStatusBars(true, activity, binding.root)
    }


    private fun setUpBottomNavigation() {
        val bottomNav = binding.mnvBottomNavigation.apply {
            itemIconTintList = null
            menu.forEach {
                findViewById<View>(it.itemId).setOnLongClickListener { true }
            }
        }
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fcv_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNav.setupWithMainNavController(
            navController = navController,
            onClickChat = ::navigateToChat
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        closeRootViewLayout(activity?.window)
    }

}