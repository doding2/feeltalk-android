package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSettingBinding
import com.clonect.feeltalk.new_domain.model.appSettings.Language
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

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
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }

            llLockSetting.setOnClickListener { navigateToLockSetting() }
            llAccountSetting.setOnClickListener { navigateToAccountSetting() }
            llLanguageSetting.setOnClickListener { navigateToLanguageSetting() }

            tvLogOut.setOnClickListener { showLogOutDialog() }
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

    private fun navigateToAccountSetting() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_settingFragment_to_accountSettingFragment)
    }


    private fun showLogOutDialog() {
        showConfirmDialog(
            title = requireContext().getString(R.string.log_out_dialog_title),
            body = requireContext().getString(R.string.log_out_dialog_body),
            cancelButton = requireContext().getString(R.string.log_out_dialog_cancel),
            confirmButton = requireContext().getString(R.string.log_out_dialog_confirm),
            onConfirm = {
                viewModel.logOut {
                    restartApplication()
                }
            }
        )
    }

    private fun restartApplication() {
        val packageManager: PackageManager = requireContext().packageManager
        val intent = packageManager.getLaunchIntentForPackage(requireContext().packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        startActivity(mainIntent)
        exitProcess(0)
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
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
            launch { viewModel.isLoading.collectLatest(::showLoading) }
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