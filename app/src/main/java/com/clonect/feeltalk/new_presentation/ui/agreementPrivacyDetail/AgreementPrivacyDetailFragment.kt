package com.clonect.feeltalk.new_presentation.ui.agreementPrivacyDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.databinding.FragmentAgreementPrivacyDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgreementPrivacyDetailFragment : Fragment() {

    private lateinit var binding: FragmentAgreementPrivacyDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAgreementPrivacyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
}