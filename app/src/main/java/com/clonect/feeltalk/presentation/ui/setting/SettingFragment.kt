package com.clonect.feeltalk.presentation.ui.setting

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSettingBinding
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

        collectNotificationSettings()
        initSwitch()

        binding.apply {
            textMyName.text = "jenny"

            textMyName.setOnClickListener { navigateToCoupleSettingPage() }
            ivHalfArrowRight.setOnClickListener { navigateToCoupleSettingPage() }
            flProfile.setOnClickListener { navigateToCoupleSettingPage() }
            llDDay.setOnClickListener { navigateToCoupleSettingPage() }
        }

    }

    private fun collectNotificationSettings() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.isPushNotificationEnabled.collectLatest {
                    binding.switchPushNotification.setCheckedImmediately(it)
                }
            }

            launch {
                viewModel.isUsageInfoNotificationEnabled.collectLatest {
                    binding.switchUsageInfoNotification.setCheckedImmediately(it)
                }
            }
        }
    }

    private fun navigateToCoupleSettingPage() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_coupleSettingFragment)
    }

    private fun initSwitch() = binding.apply {
        layoutPushNotification.setOnClickListener {
            switchPushNotification.toggle()
        }
        layoutUsageInfoNotification.setOnClickListener {
            switchUsageInfoNotification.toggle()
        }

        switchPushNotification.apply {
            setOnCheckedChangeListener { _, isChecked ->
                val drawableRes =
                    if (isChecked) R.drawable.ic_switch_thumb_on
                    else R.drawable.ic_switch_thumb_off
                setThumbDrawableRes(drawableRes)
                viewModel.enablePushNotification(isChecked)
            }
        }

        switchUsageInfoNotification.apply {
            setOnCheckedChangeListener { _, isChecked ->
                val drawableRes =
                    if (isChecked) R.drawable.ic_switch_thumb_on
                    else R.drawable.ic_switch_thumb_off
                setThumbDrawableRes(drawableRes)
                viewModel.enableUsageInfoNotification(isChecked)
            }
        }
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