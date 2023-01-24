package com.clonect.feeltalk.presentation.ui.setting

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSettingBinding
import com.clonect.feeltalk.presentation.util.addTextGradient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
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

        binding.apply {

            textLogo.addTextGradient()

            btnAnotherSetting.setOnClickListener { }

            textMyName.text = "jenny"

            textMyName.setOnClickListener { navigateToCoupleSettingPage() }
            ivHalfArrowRight.setOnClickListener { navigateToCoupleSettingPage() }
            flProfile.setOnClickListener { navigateToCoupleSettingPage() }
            llDDay.setOnClickListener { navigateToCoupleSettingPage() }
        }

        initSwitch()
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
                if (isChecked)
                    setThumbDrawableRes(R.drawable.ic_switch_thumb_on)
                else
                    setThumbDrawableRes(R.drawable.ic_switch_thumb_off)
            }
        }

        switchUsageInfoNotification.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    setThumbDrawableRes(R.drawable.ic_switch_thumb_on)
                else
                    setThumbDrawableRes(R.drawable.ic_switch_thumb_off)
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