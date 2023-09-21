package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSettingBinding
import com.clonect.feeltalk.new_domain.model.appSettings.Language
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        setFragmentResultListener("lockSettingFragment") { requestKey, bundle ->
            val lockEnabled = bundle.getBoolean("lockEnabled", viewModel.lockEnabled.value ?: false)
            viewModel.setLockEnabled(lockEnabled)
        }
        setFragmentResultListener("languageSettingFragment") { requestKey, bundle ->
            val selectedLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable("selectedLanguage", Language::class.java)
            } else {
                bundle.getSerializable("selectedLanguage") as? Language
            }
            selectedLanguage?.let {
                viewModel.setLanguage(it)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            tvVersionInfo.text = BuildConfig.VERSION_NAME
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            llLockSetting.setOnClickListener { navigateToLockSetting() }
            llBreakUpCouple.setOnClickListener { navigateToBreakUpCouple() }
            llLanguageSetting.setOnClickListener { navigateToLanguageSetting() }
            llPrivacyPolicy.setOnClickListener { navigateToPrivacyPolicyDetail() }
            llServiceAgreement.setOnClickListener { navigateToServiceAgreementDetail() }
        }
    }

    private fun navigateToLockSetting() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_settingFragment_to_lockSettingFragment)
    }

    private fun navigateToLanguageSetting() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_settingFragment_to_languageSettingFragment)
    }

    private fun navigateToBreakUpCouple() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_settingFragment_to_breakUpCoupleFragment)
    }

    private fun navigateToPrivacyPolicyDetail() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_settingFragment_to_privacyPolicyDetailFragment)
    }

    private fun navigateToServiceAgreementDetail() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_settingFragment_to_serviceAgreementDetailFragment)
    }





    private fun changeLockSettingView(lockEnabled: Boolean?) = binding.run {
        if (lockEnabled == null) {
            tvLockEnabled.text = null
        } else if (lockEnabled) {
            tvLockEnabled.text = requireContext().getString(R.string.setting_lock_on)
        } else {
            tvLockEnabled.text = requireContext().getString(R.string.setting_lock_off)
        }
    }

    private fun changeLanguageSettingView(language: Language) = binding.run {
        tvLanguage.text = language.nativeName
    }

    private fun showSnackBar(message: String) {
        val decorView = activity?.window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = message,
            duration = Snackbar.LENGTH_SHORT,
            onClick = {
                it.dismiss()
            }
        ).show()
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch { viewModel.lockEnabled.collectLatest(::changeLockSettingView) }
            launch { viewModel.language.collectLatest(::changeLanguageSettingView) }
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