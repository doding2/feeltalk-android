package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainNavigationFragment : Fragment() {

    private lateinit var binding: FragmentMainNavigationBinding
    private val viewModel: MainNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainNavigationBinding.inflate(inflater, container, false)

        setUpBottomNavigation()
        setUpAnswerSheet()
        binding.mcvChatRounder.setBackgroundResource(R.drawable.background_dialog_round_top)

        // set fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.clFloatingChatContainer.setPadding(0, getStatusBarHeight(), 0, 0)
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        } else {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        val showChat = arguments?.getBoolean("showChat", false) ?: false
        viewModel.initShowChatNavigation(showChat)

        val questionIndex = arguments?.getLong("questionIndex", -1) ?: -1
        val isTodayQuestion = arguments?.getBoolean("isTodayQuestion", false) ?: false
        viewModel.initShowQuestionAnswerSheet(questionIndex, isTodayQuestion)

        viewModel.setShortcut(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        collectViewModel()

        binding.run {
            mcvFloatingChat.setOnClickListener {
                viewModel.toggleShowChatNavigation()
            }

            viewChatBehind.setOnClickListener {
                viewModel.toggleShowChatNavigation()
            }

            viewAnswerBehind.setOnClickListener {
                viewModel.setShowAnswerSheet(false)
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
    }

    private fun setUpAnswerSheet() {
        val behavior = BottomSheetBehavior.from(binding.flAnswerSheet).apply {
            peekHeight = 0
            skipCollapsed = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.setShowAnswerSheet(false)
                    }
                }
            })
        }
    }


    private fun showChatSheet(isShow: Boolean) = binding.run {
        if (isShow) {
            viewChatBehind.visibility = View.VISIBLE
            flChatContainer.visibility = View.VISIBLE

            viewAnswerBehind.visibility = View.GONE
        } else {
            viewChatBehind.visibility = View.GONE
            flChatContainer.visibility = View.GONE

            if (viewModel.showAnswerSheet.value) {
                viewAnswerBehind.visibility = View.VISIBLE
            }
        }
    }

    private fun showAnswerSheet(isShow: Boolean) {
        val behavior = BottomSheetBehavior.from(binding.flAnswerSheet)

        if (isShow) {
            binding.viewAnswerBehind.visibility = View.VISIBLE
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else {
            viewModel.setAnswerTargetQuestion(null)
            binding.viewAnswerBehind.visibility = View.GONE
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun changePartnerLastChatView(isShow: Boolean) = binding.run {
        tvLatestChat.text = viewModel.partnerLastChat.value

        flLatestChat.visibility = if (isShow) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun changePartnerLastChatColor(color: Int) {
        binding.mcvLatestChatBody.setCardBackgroundColor(color)
        binding.ivLatestChatTail.setColorFilter(color)
    }

    private fun navigateFragment(target: String) {
        if (target == "home") {
            binding.mnvBottomNavigation.selectedItemId = R.id.navigation_home
        }
        if (target == "question") {
            binding.mnvBottomNavigation.selectedItemId = R.id.navigation_question
        }
        if (target == "challenge") {
            binding.mnvBottomNavigation.selectedItemId = R.id.navigation_bucket_list
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.navigateTo.collectLatest(::navigateFragment) }
            launch { viewModel.showChatNavigation.collectLatest(::showChatSheet) }
            launch { viewModel.showPartnerLastChat.collectLatest(::changePartnerLastChatView) }
            launch { viewModel.partnerLastChatColor.collectLatest(::changePartnerLastChatColor) }
            launch { viewModel.showAnswerSheet.collectLatest(::showAnswerSheet) }
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.showAnswerSheet.value) {
                    viewModel.setShowAnswerSheet(false)
                    return
                }
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}