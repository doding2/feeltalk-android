package com.clonect.feeltalk.new_presentation.ui.mainNavigation.home

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentHomeBinding
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.home.signal.SignalBottomSheetFragment
import com.clonect.feeltalk.new_presentation.ui.util.CustomTypefaceSpan
import com.clonect.feeltalk.new_presentation.ui.util.PokeSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.new_presentation.ui.util.setStatusBarColor
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()
    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, 0)
            setLightStatusBars(true, activity, binding.root)
        } else {
            activity.setStatusBarColor(binding.root, requireContext().getColor(R.color.white), false)
        }
        navViewModel.lastChatColor = requireContext().getColor(R.color.gray_200)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setLightStatusBars(true, activity, binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectViewModel()

        binding.run {
            mcvAnswer.setOnClickListener { clickAnswerButton() }
            acvMySignal.setOnClickListener { showSignalBottomSheet() }
        }
    }

    private fun clickAnswerButton() {
        viewModel.todayQuestion.value?.let {
            if (it.myAnswer != null && it.partnerAnswer == null) {
                showPokeSnackBar()
            } else {
                showAnswerBottomSheet()
            }
        }
    }

    private fun showAnswerBottomSheet() {
        val todayQuestion = viewModel.todayQuestion.value ?: return

        navViewModel.setAnswerTargetQuestion(todayQuestion)
        navViewModel.setShowAnswerSheet(true)
    }

    private fun showPokeSnackBar() {
        val decorView = activity?.window?.decorView ?: return
        PokeSnackbar.make(
            view = decorView,
            message = requireContext().getString(R.string.home_poke_snackbar_title),
            pokeText = requireContext().getString(R.string.home_poke_snackbar_button),
            duration = Snackbar.LENGTH_SHORT,
            bottomMargin = activity.dpToPx(56f).toInt(),
            onClick = {
                it.dismiss()
            },
            onPoke = {
                it.dismiss()
                viewModel.pressForAnswer(requireContext())
            }
        ).show()
    }


    private fun showSignalBottomSheet() {
        val bottomSheet = SignalBottomSheetFragment(onSendSignal = ::changeMySignal)

        val currentSignal = viewModel.mySignal.value
        val bundle = Bundle()
        bundle.putString("currentSignal", currentSignal.raw)
        bottomSheet.arguments = bundle

        bottomSheet.show(requireActivity().supportFragmentManager, SignalBottomSheetFragment.TAG)
    }

    private fun changeMySignal(signal: Signal) {
        viewModel.setMySignal(signal)
        navViewModel.setShowSignalCompleteSheet(true)
    }



    private fun changeTodayQuestionView(todayQuestion: Question?) = binding.run {
        val index = (todayQuestion?.index ?: 0).toString()
        val title = index + getString(R.string.home_main_title_deco)
        val countFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold)
        val decoFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular)

        val spanString = SpannableStringBuilder(title).apply {
            setSpan(CustomTypefaceSpan("", countFont), 0, index.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(CustomTypefaceSpan("", decoFont), index.length, title.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(AbsoluteSizeSpan(activity.dpToPx(28f).toInt()), 0, index.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(CountCenterVerticalSpan(), 0, index.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        tvQuestionCount.text = spanString

        if (todayQuestion?.myAnswer != null) {
            tvAnswer.setText(R.string.home_main_answer_button_2)
//            mcvAnswer.setCardBackgroundColor(requireContext().getColor(R.color.main_500))
            mcvAnswer.strokeWidth = requireContext().dpToPx(1f)
            llAnswer.setBackgroundResource(R.drawable.n_background_button_main)
            tvAnswer.setTextColor(requireContext().getColor(R.color.white))
            ivAnswerArrow.setColorFilter(requireContext().getColor(R.color.white))
        } else {
            tvAnswer.setText(R.string.home_main_answer_button_1)
//            mcvAnswer.setCardBackgroundColor(Color.WHITE)
            mcvAnswer.strokeWidth = 0
            llAnswer.setBackgroundResource(R.drawable.n_background_button_white)
            tvAnswer.setTextColor(requireContext().getColor(R.color.main_500))
            ivAnswerArrow.setColorFilter(requireContext().getColor(R.color.main_500))
        }

    }


    private fun changeMySignalView(signal: Signal) {
        binding.ivMySignal.setImageResource(signal.getImageResource())
    }

    private fun changePartnerSignalView(signal: Signal) {
        binding.ivPartnerSignal.setImageResource(signal.getImageResource())
    }

    private fun Signal.getImageResource(): Int = when (this) {
        Signal.Zero -> R.drawable.n_image_home_signal_0
        Signal.Quarter -> R.drawable.n_image_home_signal_25
        Signal.Half -> R.drawable.n_image_home_signal_50
        Signal.ThreeFourth -> R.drawable.n_image_home_signal_75
        Signal.One -> R.drawable.n_image_home_signal_100
    }


    private fun showSnackBar(message: String?) {
        if (message == null) return
        val decorView = activity?.window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = message,
            duration = Snackbar.LENGTH_SHORT,
            bottomMargin = activity.dpToPx(56f).toInt(),
            onClick = {
                it.dismiss()
            }
        ).show()
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.todayQuestion.collectLatest(::changeTodayQuestionView) }
            launch { viewModel.mySignal.collectLatest(::changeMySignalView) }
            launch { viewModel.partnerSignal.collectLatest(::changePartnerSignalView) }
            launch { viewModel.snackbarMessage.collectLatest(::showSnackBar) }
        }
    }


    override fun onDetach() {
        super.onDetach()
        navViewModel.lastChatColor = requireContext().getColor(R.color.white)
    }
}