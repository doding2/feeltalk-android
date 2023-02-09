package com.clonect.feeltalk.presentation.ui.setting

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSettingBinding
import com.clonect.feeltalk.presentation.utils.showPermissionRequestDialog
import com.kyleduo.switchbutton.SwitchButton
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectUserInfo()
        initSwitch()

        binding.apply {
            textMyName.text = "jenny"

            textMyName.setOnClickListener { navigateToCoupleSettingPage() }
            ivHalfArrowRight.setOnClickListener { navigateToCoupleSettingPage() }
            flProfile.setOnClickListener { navigateToCoupleSettingPage() }
            llDDay.setOnClickListener { navigateToCoupleSettingPage() }
        }
    }

    private fun navigateToCoupleSettingPage() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_coupleSettingFragment)
    }


    private fun collectUserInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.userInfo.collectLatest {
                binding.textMyName.text = it.nickname
            }
        }
    }



    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            binding.switchPushNotification.toggle()
            return@registerForActivityResult
        }
        showPermissionRequestDialog(
            title = "알림 권한 설정",
            message = "푸쉬 알림을 활성화 하려면 알림 권한을 설정해주셔야 합니다."
        )
    }

    private val usageInfoNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            binding.switchUsageInfoNotification.toggle()
            return@registerForActivityResult
        }
        showPermissionRequestDialog(
            title = "알림 권한 설정",
            message = "이용 정보 알림을 활성화 하려면 알림 권한을 설정해주셔야 합니다."
        )
    }

    private fun initSwitch() = binding.apply {
        layoutPushNotification.setOnClickListener {
            val isPushNotificationEnabled = viewModel.isPushNotificationEnabled.value
            if (isPushNotificationEnabled) {
                switchPushNotification.toggle()
                return@setOnClickListener
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                switchPushNotification.toggle()
            }
        }

        layoutUsageInfoNotification.setOnClickListener {
            val isUsageInfoNotificationEnabled = viewModel.isUsageInfoNotificationEnabled.value
            if (isUsageInfoNotificationEnabled) {
                switchUsageInfoNotification.toggle()
                return@setOnClickListener
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                usageInfoNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                switchUsageInfoNotification.toggle()
            }
        }

        switchPushNotification.apply {
            val initialChecked = viewModel.isPushNotificationEnabled.value
            setCheckedImmediatelyNoEvent(initialChecked)
            setSwitchChecked(initialChecked)

            setOnCheckedChangeListener { _, isChecked ->
                setSwitchChecked(isChecked)
                viewModel.enablePushNotification(isChecked)
            }
        }

        switchUsageInfoNotification.apply {
            val initialChecked = viewModel.isUsageInfoNotificationEnabled.value
            setCheckedImmediatelyNoEvent(initialChecked)
            setSwitchChecked(initialChecked)

            setOnCheckedChangeListener { _, isChecked ->
                setSwitchChecked(isChecked)
                viewModel.enableUsageInfoNotification(isChecked)
            }
        }
    }

    private fun SwitchButton.setSwitchChecked(isChecked: Boolean) {
        val drawableRes =
            if (isChecked) R.drawable.ic_switch_thumb_on
            else R.drawable.ic_switch_thumb_off
        setThumbDrawableRes(drawableRes)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this@SettingFragment.requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}