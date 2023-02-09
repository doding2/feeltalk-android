package com.clonect.feeltalk.presentation.ui.couple_registration_done

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCoupleRegistrationDoneBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoupleRegistrationDoneFragment : Fragment() {

    private lateinit var binding: FragmentCoupleRegistrationDoneBinding
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleRegistrationDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnNext.setOnClickListener { navigateToHomePage() }
            btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
        }
    }


    private fun navigateToHomePage() {
        findNavController().navigate(R.id.action_coupleRegistrationDoneFragment_to_bottomNavigationFragment)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}