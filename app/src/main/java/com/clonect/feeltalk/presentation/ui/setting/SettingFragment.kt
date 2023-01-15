package com.clonect.feeltalk.presentation.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSettingBinding
import com.clonect.feeltalk.presentation.util.addTextGradient

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.textLogo.addTextGradient()

        binding.btnAnotherSetting.setOnClickListener {

        }

        binding.textMyName.text = "jenny"

        binding.textMyName.setOnClickListener { navigateToLogPage() }
        binding.ivHalfArrowRight.setOnClickListener { navigateToLogPage() }


        return binding.root
    }

    fun navigateToLogPage() {

    }


}