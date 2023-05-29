package com.clonect.feeltalk.new_presentation.ui.sign_up_navigation.agreement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentAgreementBinding
import com.clonect.feeltalk.new_presentation.ui.sign_up_navigation.SignUpNavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AgreementFragment : Fragment() {

    private lateinit var binding: FragmentAgreementBinding
    private val viewModel: SignUpNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            clAgreeSensitiveInfo.setOnClickListener { agreeSensitiveInfo() }
            ivMoreSensitiveInfo.setOnClickListener { navigateToSensitiveDetail() }

            clAgreePrivacyInfo.setOnClickListener { agreePrivacyInfo() }
            ivMorePrivacyInfo.setOnClickListener { navigateToPrivacyDetail() }

            mcvNext.setOnClickListener { navigateToNickname() }
        }
    }


    private fun navigateToSensitiveDetail() {
        requireParentFragment()
            .requireParentFragment()
                .findNavController()
                .navigate(R.id.action_signUpNavigationFragment_to_agreementSensitiveDetailFragment)
    }

    private fun navigateToPrivacyDetail() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_signUpNavigationFragment_to_agreementPrivacyDetailFragment)
    }


    private fun navigateToNickname() {
        viewModel.setAgreementProcessed(true)
    }

    private fun agreeSensitiveInfo() {
        if (viewModel.isPrivacyInfoAgreed.value) return
        viewModel.setSensitiveInfoAgreed(viewModel.isSensitiveInfoAgreed.value.not())
    }

    private fun agreePrivacyInfo() {
        viewModel.setPrivacyInfoAgreed(viewModel.isPrivacyInfoAgreed.value.not())
    }


    private fun setPrivacyInfoVisible(visible: Boolean) = binding.run {
        llPrivacyInfo.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun enableNextButton(enabled: Boolean) = binding.mcvNext.run {
        if (enabled) {
            setCardBackgroundColor(resources.getColor(R.color.main_500, null))
            isClickable = true
            isFocusable = true
        } else {
            setCardBackgroundColor(resources.getColor(R.color.main_400, null))
            isClickable = false
            isFocusable = false
        }
    }

    private fun changeSensitiveInfoView(enabled: Boolean) = binding.run {
        if (enabled) {
            ivSensitiveCheck.setImageResource(R.drawable.n_ic_processed_check)
            tvSensitiveAgree.setTextColor(resources.getColor(R.color.gray_500, null))
            setPrivacyInfoVisible(true)
            viewModel.setSignUpProcess(20)
        } else {
            ivSensitiveCheck.setImageResource(R.drawable.n_ic_disabled_check)
            tvSensitiveAgree.setTextColor(resources.getColor(R.color.system_black, null))
            setPrivacyInfoVisible(false)
            viewModel.setSignUpProcess(0)
        }
    }

    private fun changePrivacyInfoView(enabled: Boolean) = binding.run {
        if (enabled) {
            ivPrivacyCheck.setImageResource(R.drawable.n_ic_processed_check)
            tvPrivacyAgree.setTextColor(resources.getColor(R.color.gray_500, null))
            viewModel.setSignUpProcess(40)
        } else {
            ivPrivacyCheck.setImageResource(R.drawable.n_ic_disabled_check)
            tvPrivacyAgree.setTextColor(resources.getColor(R.color.system_black, null))

            if (viewModel.isSensitiveInfoAgreed.value) {
                viewModel.setSignUpProcess(20)
            }
        }
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isSensitiveInfoAgreed.collectLatest(::changeSensitiveInfoView) }
            launch {
                viewModel.isPrivacyInfoAgreed.collectLatest {
                    changePrivacyInfoView(it)
                    enableNextButton(it)
                }
            }
        }
    }
}