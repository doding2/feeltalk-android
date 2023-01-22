package com.clonect.feeltalk.presentation.ui.couple_setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCoupleSettingBinding
import com.clonect.feeltalk.presentation.util.addTextGradient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoupleSettingFragment : Fragment() {

    private lateinit var binding: FragmentCoupleSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleSettingBinding.inflate(inflater, container, false)

        binding.textLogo.addTextGradient()

        return binding.root
    }
}