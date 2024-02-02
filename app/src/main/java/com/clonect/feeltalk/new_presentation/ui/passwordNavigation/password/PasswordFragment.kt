package com.clonect.feeltalk.new_presentation.ui.passwordNavigation.password

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentPasswordBinding
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/09/19.
 */
@AndroidEntryPoint
class PasswordFragment : Fragment() {

    private lateinit var binding: FragmentPasswordBinding
    private val viewModel: PasswordViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPasswordBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
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
            tvKeypadConfirm.setOnClickListener { matchPassword() }
            clForgetPassword.setOnClickListener { navigateToResetPassword() }
        }
    }


    private fun matchPassword() = lifecycleScope.launch {
        val isLessEntered = viewModel.password.value.length < 4
        if (isLessEntered) return@launch

        val isValid = viewModel.matchPassword()
        if (isValid) {
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        val bundle = bundleOf(
            "showChat" to arguments?.getBoolean("showChat", false),
            "questionIndex" to arguments?.getLong("questionIndex", -1),
            "isTodayQuestion" to arguments?.getBoolean("isTodayQuestion", false),
            "challengeIndex" to arguments?.getLong("challengeIndex", -1),
        )

        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_passwordFragment_to_mainNavigationFragment, bundle)
    }

    private fun navigateToResetPassword() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_passwordFragment_to_resetPasswordFragment)
        viewModel.setValidPassword(true)
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

    private fun changePasswordBarView(password: String) = binding.run {
        val length = password.length
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

    private fun changeValidPasswordView(isValid: Boolean) = binding.run {
        if (isValid) {
            tvGuide.setText(R.string.password_guide)
            tvGuide.setTextColor(requireContext().getColor(R.color.gray_600))
        } else {
            tvGuide.setText(R.string.password_confirm_invalid)
            tvGuide.setTextColor(requireContext().getColor(R.color.system_error))
            vibrate()
        }
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
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.password.collectLatest(::changePasswordBarView) }
            launch { viewModel.isValidPassword.collectLatest(::changeValidPasswordView) }
        }
    }
}