package com.clonect.feeltalk.release_presentation.ui.agreementSensitiveDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.databinding.FragmentAgreementSensitiveDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgreementSensitiveDetailFragment : Fragment() {

    private lateinit var binding: FragmentAgreementSensitiveDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAgreementSensitiveDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

}