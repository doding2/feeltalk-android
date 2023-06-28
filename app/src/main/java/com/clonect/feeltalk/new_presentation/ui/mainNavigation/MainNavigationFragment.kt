package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentMainNavigationBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainNavigationFragment : Fragment() {

    private lateinit var binding: FragmentMainNavigationBinding
    private val viewModel: MainNavigationViewModel by activityViewModels()
    private lateinit var windowManager: WindowManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainNavigationBinding.inflate(inflater, container, false)
//        extendRootViewLayout(activity?.window)
//        binding.root.setPadding(0, 0, 0, getNavigationBarHeight())
//        binding.clFloatingChatContainer.setPadding(0, getStatusBarHeight(), 0, 0)
        setUpBottomNavigation()
        binding.mcvChatRounder.setBackgroundResource(R.drawable.background_dialog_round_top)

        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window?.statusBarColor = requireContext().getColor(R.color.main_500)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivFloatingChat.setOnClickListener {
                viewModel.toggleShowChatNavigation()
            }

            viewChatBehind.setOnClickListener {
                viewModel.toggleShowChatNavigation()
            }
        }
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
//        bottomNav.setupWithNavController(navController)
        bottomNav.setupWithMainNavController(navController)
    }


    private fun showChatSheet(isShow: Boolean) = binding.run {
        if (isShow) {
            flLatestChat.visibility = View.GONE
            viewChatBehind.visibility = View.VISIBLE
            flChatContainer.visibility = View.VISIBLE
        } else {
            flLatestChat.visibility = View.VISIBLE
            viewChatBehind.visibility = View.GONE
            flChatContainer.visibility = View.GONE
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.showChatNavigation.collectLatest(::showChatSheet) }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        closeRootViewLayout(activity?.window)
    }

}