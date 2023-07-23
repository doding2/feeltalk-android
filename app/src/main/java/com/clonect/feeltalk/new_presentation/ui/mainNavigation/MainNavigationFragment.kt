package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentMainNavigationBinding
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainNavigationFragment : Fragment() {

    private lateinit var binding: FragmentMainNavigationBinding
    private val viewModel: MainNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainNavigationBinding.inflate(inflater, container, false)

        setUpBottomNavigation()
        binding.mcvChatRounder.setBackgroundResource(R.drawable.background_dialog_round_top)

        // set fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            binding.clFloatingChatContainer.setPadding(0, getStatusBarHeight(), 0, 0)
        } else {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        val showChat = arguments?.getBoolean("showChat", false) ?: false
        viewModel.setShowChatNavigation(showChat)

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
        bottomNav.setupWithNavController(navController)
//        bottomNav.setupWithMainNavController(navController)
    }


    private fun showChatSheet(isShow: Boolean) = binding.run {
        if (isShow) {
            flLatestChat.visibility = View.GONE
            viewChatBehind.visibility = View.VISIBLE
            flChatContainer.visibility = View.VISIBLE
        } else {
            flLatestChat.visibility =  if (viewModel.partnerLastChat.value == null) View.GONE
            else View.VISIBLE
            viewChatBehind.visibility = View.GONE
            flChatContainer.visibility = View.GONE
        }
    }

    private fun changeLatestChatMessage(message: String?) = binding.run {
        tvLatestChat.text = message

        if (message == null) {
            flLatestChat.visibility = View.GONE
        } else {
            flLatestChat.visibility = if (viewModel.showChatNavigation.value) View.GONE
            else View.VISIBLE
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.showChatNavigation.collectLatest(::showChatSheet) }
            launch { viewModel.partnerLastChat.collectLatest(::changeLatestChatMessage) }
        }
    }
}