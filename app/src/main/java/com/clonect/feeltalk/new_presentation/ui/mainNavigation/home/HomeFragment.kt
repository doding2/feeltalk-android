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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentHomeBinding
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.signal.SignalBottomSheetFragment
import com.clonect.feeltalk.new_presentation.ui.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, 0)
            setLightStatusBars(false, activity, binding.root)
        } else {
            activity.setStatusBarColor(binding.root, requireContext().getColor(R.color.main_500), false)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setLightStatusBars(false, activity, binding.root)
    }


    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectViewModel()

        binding.run {
            mcvMySignal.setOnClickListener {
//                showSignalBottomSheet()
                notificationHelper.showChatNotification(title = "연인", message = "채팅 내용")
            }
        }
    }


    private fun showSignalBottomSheet() {
        val bottomSheet = SignalBottomSheetFragment(onSendSignal = viewModel::setMySignal)

        val currentSignal = viewModel.mySignal.value
        val bundle = Bundle()
        bundle.putString("currentSignal", currentSignal.raw)
        bottomSheet.arguments = bundle

        bottomSheet.show(requireActivity().supportFragmentManager, SignalBottomSheetFragment.TAG)
    }


    private fun changeQuestionCountTitle(count: Int) {
        val title = count.toString() + getString(R.string.home_main_title_deco)
        val countFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold)
        val decoFont = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular)

        val spanString = SpannableStringBuilder(title).apply {
            setSpan(CustomTypefaceSpan("", countFont), 0, count.toString().length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(CustomTypefaceSpan("", decoFont), count.toString().length, title.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(AbsoluteSizeSpan(activity.dpToPx(28f).toInt()), 0, count.toString().length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(CountCenterVerticalSpan(), 0, count.toString().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.tvQuestionCount.text = spanString
    }


    // TODO 나중에 이미지로 바꾸기
    private fun changeMySignalView(signal: Signal) {
        binding.ivMySignal.setBackgroundColor(signal.getColorResource())
    }

    private fun changePartnerSignalView(signal: Signal) {
        binding.ivPartnerSignal.setBackgroundColor(signal.getColorResource())
    }

    // TODO 나중에 이미지로 바꾸기
    private fun Signal.getColorResource(): Int = when (this) {
        Signal.Seduce -> ContextCompat.getColor(requireContext(), R.color.signal_seduce)
        Signal.Passion -> ContextCompat.getColor(requireContext(), R.color.signal_passion)
        Signal.Skinship -> ContextCompat.getColor(requireContext(), R.color.signal_skinship)
        Signal.Puzzling -> ContextCompat.getColor(requireContext(), R.color.signal_puzzling)
        Signal.Nope -> ContextCompat.getColor(requireContext(), R.color.signal_nope)
        Signal.Tired -> ContextCompat.getColor(requireContext(), R.color.signal_tired)
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.questionCount.collectLatest(::changeQuestionCountTitle) }
            launch { viewModel.mySignal.collectLatest(::changeMySignalView) }
            launch { viewModel.partnerSignal.collectLatest(::changePartnerSignalView) }
        }
    }
}