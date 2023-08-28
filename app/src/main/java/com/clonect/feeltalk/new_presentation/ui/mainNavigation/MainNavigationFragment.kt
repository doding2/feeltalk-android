package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
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
import com.clonect.feeltalk.new_domain.model.chat.PartnerLastChatDto
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
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

        // set fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.clFloatingChatContainer.setPadding(0, getStatusBarHeight(), 0, 0)
        } else {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        val showChat = arguments?.getBoolean("showChat", false) ?: false
        if (showChat) {
            viewModel.setShowChatNavigation(showChat)
        }

        val questionIndex = arguments?.getLong("questionIndex", -1) ?: -1
        val isTodayQuestion = arguments?.getBoolean("isTodayQuestion", false) ?: false
        viewModel.initShowQuestionAnswerSheet(questionIndex)
        if (questionIndex >= 0 && !isTodayQuestion) {
            navigateFragment("question")
        }

        val challengeIndex = arguments?.getLong("challengeIndex", -1) ?: -1
        viewModel.initShowChallengeDetail(challengeIndex)
        if (challengeIndex >= 0) {
            navigateFragment("challenge")
        }

        arguments?.clear()

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
                        viewModel.setUserAnswering(false)
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

            viewModel.setPartnerLastChat(null)

            if (viewModel.showAnswerSheet.value) {
                viewAnswerBehind.visibility = View.VISIBLE
            }
        }
    }

    private fun showAnswerSheet(isShow: Boolean) {
        val behavior = BottomSheetBehavior.from(binding.flAnswerSheet)

        if (isShow) {
            if (viewModel.showChatNavigation.value) {
                binding.viewAnswerBehind.visibility = View.GONE
            } else {
                binding.viewAnswerBehind.visibility = View.VISIBLE
            }
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else {
            viewModel.setAnswerTargetQuestion(null)
            binding.viewAnswerBehind.visibility = View.GONE
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showPartnerLastChatView(isShow: Boolean) = binding.run {
        flLatestChat.visibility = if (isShow) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun changePartnerLastChatView(partnerLastChatDto: PartnerLastChatDto?) = binding.run {
        tvLatestChat.text = partnerLastChatDto?.message
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
            launch { viewModel.showPartnerLastChat.collectLatest(::showPartnerLastChatView) }
            launch { viewModel.partnerLastChat.collectLatest(::changePartnerLastChatView) }
            launch { viewModel.showAnswerSheet.collectLatest(::showAnswerSheet) }
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isUserAnswering.value) {
                    showConfirmDialog(
                        title = requireContext().getString(R.string.answer_cancel_title),
                        body = requireContext().getString(R.string.answer_cancel_body),
                        confirmButton = requireContext().getString(R.string.answer_cancel_confirm),
                    ) {
                        viewModel.setShowAnswerSheet(false)
                    }
                    return
                }

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