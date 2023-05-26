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
            ivMoreSensitiveInfo.setOnClickListener {
                // navigate to sensitive info fragment
            }

            clAgreePrivacyInfo.setOnClickListener { agreePrivacyInfo() }
            ivMorePrivacyInfo.setOnClickListener {
                // navigate to privacy info fragment
            }

            mcvNext.setOnClickListener { navigateToNickname() }
        }
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


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.isSensitiveInfoAgreed.collectLatest { agreed ->
                    binding.run {
                        if (agreed) {
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
                }
            }

            launch {
                viewModel.isPrivacyInfoAgreed.collectLatest { agreed ->
                    binding.run {
                        enableNextButton(agreed)

                        if (agreed) {
                            ivPrivacyCheck.setImageResource(R.drawable.n_ic_processed_check)
                            ivPrivacySubCheck.setImageResource(R.drawable.n_ic_processed_check)
                            tvPrivacyAgree.setTextColor(resources.getColor(R.color.gray_500, null))
                            viewModel.setSignUpProcess(40)
                        } else {
                            ivPrivacyCheck.setImageResource(R.drawable.n_ic_disabled_check)
                            ivPrivacySubCheck.setImageResource(R.drawable.n_ic_disabled_check)
                            tvPrivacyAgree.setTextColor(resources.getColor(R.color.system_black, null))
                        }
                    }
                }
            }
        }
    }
}