package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.passwordSetting

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentPasswordSettingBinding
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordSettingFragment : Fragment() {

    private lateinit var binding: FragmentPasswordSettingBinding
    private val viewModel: PasswordSettingViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPasswordSettingBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        val isLockEnabled = arguments?.getBoolean("isLockEnabled") ?: false
        viewModel.setLockEnabled(isLockEnabled)
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            tvKeypad1.setOnClickListener { viewModel.addPasswordNum(1) }
            tvKeypad2.setOnClickListener { viewModel.addPasswordNum(2) }
            tvKeypad3.setOnClickListener { viewModel.addPasswordNum(3) }
            tvKeypad4.setOnClickListener { viewModel.addPasswordNum(4) }
            tvKeypad5.setOnClickListener { viewModel.addPasswordNum(5) }
            tvKeypad6.setOnClickListener { viewModel.addPasswordNum(6) }
            tvKeypad7.setOnClickListener { viewModel.addPasswordNum(7) }
            tvKeypad8.setOnClickListener { viewModel.addPasswordNum(8) }
            tvKeypad9.setOnClickListener { viewModel.addPasswordNum(9) }
            tvKeypad0.setOnClickListener { viewModel.addPasswordNum(0) }
            tvKeypadCancel.setOnClickListener { viewModel.clearPassword() }
            tvKeypadConfirm.setOnClickListener { confirmPassword() }
        }
    }

    private fun confirmPassword() {
        val navigate = viewModel.navigateOrConfirmPassword()
        if (!navigate) return

        if (!viewModel.lockEnabled.value) {
            navigateToLockQuestionSetting()
            return
        }

        viewModel.updatePassword {
            showSnackBar(requireContext().getString(R.string.password_setting_success_snack_bar))
            navigateBack()
        }
    }

    private fun navigateToLockQuestionSetting() {
        val bundle = bundleOf(
            "password" to viewModel.password.value
        )
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_passwordSettingFragment_to_lockQuestionSettingFragment, bundle)
    }

    private fun navigateBack() {
        setFragmentResult(
            requestKey = "passwordSettingFragment",
            result = bundleOf("lockEnabled" to true)
        )
        findNavController().popBackStack()
    }


    private fun vibrate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)
        }
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val effect = VibrationEffect.createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrationEffect  = VibrationEffect.createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE)
            val combinedVibration = CombinedVibration.createParallel(vibrationEffect)
            vibrator.vibrate(combinedVibration)
        }
    }


    private fun changeConfirmModeView(isConfirmMode: Boolean) = binding.run {
        val guideResId =  if (isConfirmMode) {
            if (viewModel.lockEnabled.value) R.string.password_setting_confirm_guide_enabled
            else R.string.password_setting_confirm_guide_disabled
        } else {
            if (viewModel.lockEnabled.value) R.string.password_setting_default_guide_enabled
            else R.string.password_setting_default_guide_disabled
        }
        tvGuide.setText(guideResId)
        changePasswordBarView()
    }

    private fun changePasswordBarView() = binding.run {
        val length = if (viewModel.isConfirmMode.value) {
            viewModel.confirmPassword.value.length
        } else {
            viewModel.password.value.length
        }
        ivPasswordBar1.setPasswordEnabled(length >= 1)
        ivPasswordBar2.setPasswordEnabled(length >= 2)
        ivPasswordBar3.setPasswordEnabled(length >= 3)
        ivPasswordBar4.setPasswordEnabled(length >= 4)
    }

    private fun ImageView.setPasswordEnabled(enabled: Boolean) {
        setImageResource(
            if (enabled) R.drawable.n_ic_password_bar_enabled
            else R.drawable.n_ic_password_bar_disabled
        )
    }

    private fun changeConfirmInvalidView(isInvalid: Boolean) = binding.run {
        if (isInvalid) {
            tvGuide.setText(R.string.password_setting_confirm_invalid)
            tvGuide.setTextColor(requireContext().getColor(R.color.system_error))
            vibrate()
        } else {
            val guideResId =  if (viewModel.isConfirmMode.value) {
                if (viewModel.lockEnabled.value) R.string.password_setting_confirm_guide_enabled
                else R.string.password_setting_confirm_guide_disabled
            } else {
                if (viewModel.lockEnabled.value) R.string.password_setting_default_guide_enabled
                else R.string.password_setting_default_guide_disabled
            }
            tvGuide.setText(guideResId)
            tvGuide.setTextColor(requireContext().getColor(R.color.gray_600))
        }
    }


    private fun changeTitleView(lockEnabled: Boolean) = binding.run {
        tvTitle.setText(
            if (lockEnabled) R.string.password_setting_title_enabled
            else R.string.password_setting_title_disabled
        )
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
            launch { viewModel.isConfirmMode.collectLatest(::changeConfirmModeView) }
            launch { viewModel.password.collectLatest { changePasswordBarView() } }
            launch { viewModel.confirmPassword.collectLatest { changePasswordBarView() } }
            launch { viewModel.isConfirmInvalid.collectLatest(::changeConfirmInvalidView) }
            launch { viewModel.lockEnabled.collectLatest(::changeTitleView) }
        }
    }
}