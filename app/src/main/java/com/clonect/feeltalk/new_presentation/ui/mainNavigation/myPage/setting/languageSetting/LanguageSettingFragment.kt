package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.languageSetting

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentLanguageSettingBinding
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageSettingFragment : Fragment() {

    private lateinit var binding: FragmentLanguageSettingBinding
    private val viewModel: LanguageSettingViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLanguageSettingBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
        initRecyclerView()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            llNoticeCheck.setOnClickListener { viewModel.toggleNoticeChecked() }
            mcvChangeLanguage.setOnClickListener { changeLanguage() }
        }
    }


    private fun changeLanguage() {
        navigateBack()
    }

    private fun navigateBack() {
        setFragmentResult(
            requestKey = "languageSettingFragment",
            result = bundleOf("selectedLanguage" to viewModel.selectedLanguage.value)
        )
        findNavController().popBackStack()
    }


    private fun initRecyclerView() = binding.run {
        val adapter = LanguageAdapter(
            selectedItem = viewModel.appliedLanguage.value,
            onSelectItem = {
                viewModel.setSelectedLanguage(it)
            }
        )
        rvLanguage.adapter = adapter
    }


    private fun changeNoticeCheckView(isChecked: Boolean) = binding.run {
        if (isChecked) {
            ivNoticeCheck.setImageResource(R.drawable.n_ic_enabled_check)
        } else {
            ivNoticeCheck.setImageResource(R.drawable.n_ic_language_disabled_check)
        }
    }

    private fun enableChangeButton(enabled: Boolean) = binding.run {
        mcvChangeLanguage.isEnabled = enabled

        if (enabled) {
            mcvChangeLanguage.strokeWidth = requireContext().dpToPx(1f).toInt()
            mcvChangeLanguage.setCardBackgroundColor(Color.WHITE)
            tvChangeLanguage.setTextColor(Color.BLACK)
        } else {
            mcvChangeLanguage.strokeWidth = 0
            mcvChangeLanguage.setCardBackgroundColor(requireContext().getColor(R.color.gray_400))
            tvChangeLanguage.setTextColor(Color.WHITE)
        }
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isNoticeChecked.collectLatest(::changeNoticeCheckView) }
            launch { viewModel.isChangeEnabled.collectLatest(::enableChangeButton) }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}