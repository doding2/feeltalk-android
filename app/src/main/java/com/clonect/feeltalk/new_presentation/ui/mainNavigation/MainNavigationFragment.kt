package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentChatBinding
import com.clonect.feeltalk.databinding.FragmentMainNavigationBinding
import com.clonect.feeltalk.new_domain.model.chat.PartnerLastChatDto
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.new_presentation.ui.activity.MainViewModel
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.chat.ChatFragment
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import com.clonect.feeltalk.new_presentation.ui.util.stateFlow
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.annotations.Async


class MainNavigationFragment : Fragment() {

    private lateinit var binding: FragmentMainNavigationBinding
    private val viewModel: MainNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = viewModel.mainNavView
            ?.let { FragmentMainNavigationBinding.bind(it) }
            ?: FragmentMainNavigationBinding.inflate(inflater, container, false)

        viewModel.mainNavView = binding.root

        setUpBottomNavigation()
        setUpBottomSheets()
        parseArguments()

        // set fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val statusHeight = getStatusBarHeight()
            val navHeight = getNavigationBarHeight()
            binding.clFloatingChatContainer.setPadding(0, statusHeight, 0, 0)
            binding.flInquirySucceedSheet.setPadding(0, 0, 0, navHeight)
            binding.flSuggestionSucceedSheet.setPadding(0, 0, 0, navHeight)
            binding.flSignalCompleteSheet.setPadding(0, 0, 0, navHeight)
        } else {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        setFragmentResultListener("inquireFragment") { _, bundle ->
            val inquirySucceed = bundle.getBoolean("inquirySucceed", false)
            viewModel.setShowInquirySucceedSheet(inquirySucceed)
        }
        setFragmentResultListener("suggestQuestionFragment") { _, bundle ->
            val suggestionSucceed = bundle.getBoolean("suggestionSucceed", false)
            viewModel.setShowSuggestionSucceedSheet(suggestionSucceed)
        }

        viewModel.setShortcut(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.job?.cancel()
        viewModel.job = collectViewModel()

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

            sheetInquirySucceed.mcvConfirm.setOnClickListener {
                viewModel.setShowInquirySucceedSheet(false)
            }
            viewInquirySucceedBehind.setOnClickListener {
                viewModel.setShowInquirySucceedSheet(false)
            }

            sheetSuggestionSucceed.mcvConfirm.setOnClickListener {
                viewModel.setShowSuggestionSucceedSheet(false)
            }
            viewSuggestionSucceedBehind.setOnClickListener {
                viewModel.setShowSuggestionSucceedSheet(false)
            }

            sheetSignalComplete.mcvConfirm.setOnClickListener {
                viewModel.setShowSignalCompleteSheet(false)
            }
            viewSignalCompleteBehind.setOnClickListener {
                viewModel.setShowSignalCompleteSheet(false)
            }
        }
    }

    private fun parseArguments() {
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

        val isLockReset = arguments?.getBoolean("isLockReset", false) ?: false
        if (isLockReset) {
            navigateFragment("mypage")
        }

        arguments?.clear()
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

    private fun setUpBottomSheets() {
        BottomSheetBehavior.from(binding.flAnswerSheet).apply {
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

        BottomSheetBehavior.from(binding.flInquirySucceedSheet).apply {
            peekHeight = 0
            skipCollapsed = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.setShowInquirySucceedSheet(false)
                    }
                }
            })
        }

        BottomSheetBehavior.from(binding.flSuggestionSucceedSheet).apply {
            peekHeight = 0
            skipCollapsed = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.setShowSuggestionSucceedSheet(false)
                    }
                }
            })
        }

        BottomSheetBehavior.from(binding.flSignalCompleteSheet).apply {
            peekHeight = 0
            skipCollapsed = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.setShowSignalCompleteSheet(false)
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
        infoLog("showAnswerSheet: $isShow")
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

    private fun showSubmitSucceedSheet(isShow: Boolean) {
        val behavior = BottomSheetBehavior.from(binding.flInquirySucceedSheet)

        if (isShow) {
            binding.viewInquirySucceedBehind.visibility = View.VISIBLE
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else {
            binding.viewInquirySucceedBehind.visibility = View.GONE
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showSuggestionSucceedSheet(isShow: Boolean) {
        val behavior = BottomSheetBehavior.from(binding.flSuggestionSucceedSheet)

        if (isShow) {
            binding.viewSuggestionSucceedBehind.visibility = View.VISIBLE
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else {
            binding.viewSuggestionSucceedBehind.visibility = View.GONE
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showSignalCompleteSheet(isShow: Boolean) {
        val behavior = BottomSheetBehavior.from(binding.flSignalCompleteSheet)

        if (isShow) {
            binding.viewSignalCompleteBehind.visibility = View.VISIBLE
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else {
            binding.viewSignalCompleteBehind.visibility = View.GONE
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

    private fun navigateFragment(target: String) = binding.mnvBottomNavigation.run {
        if (target == "home") {
            selectedItemId = R.id.navigation_home
        }
        if (target == "question") {
            selectedItemId = R.id.navigation_question
        }
        if (target == "challenge") {
            selectedItemId = R.id.navigation_challenge
        }
        if (target == "mypage") {
            selectedItemId = R.id.navigation_my_page
        }
    }

    private fun applyLastChatColorChange(color: Int) = binding.run {
        mcvLatestChatBody.setCardBackgroundColor(color)
        ivLatestChatTail.setColorFilter(color)
    }

    @SuppressLint("SetTextI18n")
    private fun applyPartnerInfoChanges(partnerInfo: PartnerInfo?) = binding.run {
        sheetSignalComplete.tvBody.text = partnerInfo?.nickname + requireContext().getString(R.string.signal_complete_body)
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.navigateTo.collectLatest(::navigateFragment) }
            launch { viewModel.showChatNavigation.collectLatest(::showChatSheet) }
            launch { viewModel.showPartnerLastChat.collectLatest(::showPartnerLastChatView) }
            launch { viewModel.partnerLastChat.collectLatest(::changePartnerLastChatView) }
            launch { viewModel.showAnswerSheet.collectLatest(::showAnswerSheet) }
            launch { viewModel.showInquirySucceedSheet.collectLatest(::showSubmitSucceedSheet) }
            launch { viewModel.showSuggestionSucceedSheet.collectLatest(::showSuggestionSucceedSheet) }
            launch { viewModel.showSignalCompleteSheet.collectLatest(::showSignalCompleteSheet) }
            launch { viewModel.lastChatColor.collectLatest(::applyLastChatColorChange) }
            launch { viewModel.partnerInfo.collectLatest(::applyPartnerInfoChanges) }
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.showAnswerSheet.value && viewModel.isUserAnswering.value) {
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
                if (viewModel.showSignalCompleteSheet.value) {
                    viewModel.setShowSignalCompleteSheet(false)
                    return
                }
                if (viewModel.showInquirySucceedSheet.value) {
                    viewModel.setShowInquirySucceedSheet(false)
                    return
                }
                if (viewModel.showSuggestionSucceedSheet.value) {
                    viewModel.setShowSuggestionSucceedSheet(false)
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