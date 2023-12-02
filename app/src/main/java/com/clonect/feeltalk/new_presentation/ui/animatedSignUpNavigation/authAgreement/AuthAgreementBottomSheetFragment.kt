package com.clonect.feeltalk.new_presentation.ui.animatedSignUpNavigation.authAgreement

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentAuthAgreementBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.state
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/12/02.
 */
@AndroidEntryPoint
class AuthAgreementBottomSheetFragment(
    private val isAllAccepted: Boolean,
    private val onDone: () -> Unit,
    private val onCancel: () -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "AuthAgreementBottomSheetFragment"
    }

    private lateinit var binding: FragmentAuthAgreementBinding
    private val viewModel: AuthAgreementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAuthAgreementBinding.inflate(inflater, container, false)
        val behavior = (dialog as? BottomSheetDialog)?.behavior
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        behavior?.skipCollapsed = true
        if (viewModel.state.value == null && isAllAccepted) {
            viewModel.setState(
                AuthAgreementState(
                    isPrivacyPolicyAgreed = true,
                    isIdentificationInfoAgreed = true,
                    isServiceUsageAgreed = true,
                    isMobileCarrierUsageAgreed = true
                )
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            mcvAgreeAll.setOnClickListener {
                val isAgreeAll = viewModel.state.value?.run {
                    isPrivacyPolicyAgreed
                            && isIdentificationInfoAgreed
                            && isServiceUsageAgreed
                            && isMobileCarrierUsageAgreed
                } ?: false

                viewModel.setState(
                    if (isAgreeAll)
                        AuthAgreementState()
                    else
                        AuthAgreementState(
                            isPrivacyPolicyAgreed = true,
                            isIdentificationInfoAgreed = true,
                            isServiceUsageAgreed = true,
                            isMobileCarrierUsageAgreed = true
                        )
                )
            }
            llAgree1.setOnClickListener {
                val state = viewModel.state.value ?: AuthAgreementState()
                viewModel.setState(state.copy(isPrivacyPolicyAgreed = state.isPrivacyPolicyAgreed.not()))
            }
            llAgree2.setOnClickListener {
                val state = viewModel.state.value ?: AuthAgreementState()
                viewModel.setState(state.copy(isIdentificationInfoAgreed = state.isIdentificationInfoAgreed.not()))
            }
            llAgree3.setOnClickListener {
                val state = viewModel.state.value ?: AuthAgreementState()
                viewModel.setState(state.copy(isServiceUsageAgreed = state.isServiceUsageAgreed.not()))
            }
            llAgree4.setOnClickListener {
                val state = viewModel.state.value ?: AuthAgreementState()
                viewModel.setState(state.copy(isMobileCarrierUsageAgreed = state.isMobileCarrierUsageAgreed.not()))
            }

            mcvDone.setOnClickListener {
                onDone()
                dismiss()
            }
        }
    }

    private fun applyStateChanges(state: AuthAgreementState?) = binding.run {
        val isAgreeAll = state?.run {
            isPrivacyPolicyAgreed
                    && isIdentificationInfoAgreed
                    && isServiceUsageAgreed
                    && isMobileCarrierUsageAgreed
        } ?: false

        enableDoneButton(isAgreeAll)
        if (isAgreeAll) {
            ivAgreeAll.setImageResource(R.drawable.n_ic_round_agree)
            ivAgreeCheck1.setImageResource(R.drawable.n_ic_enabled_check)
            ivAgreeCheck2.setImageResource(R.drawable.n_ic_enabled_check)
            ivAgreeCheck3.setImageResource(R.drawable.n_ic_enabled_check)
            ivAgreeCheck4.setImageResource(R.drawable.n_ic_enabled_check)
            return@run
        }

        ivAgreeAll.setImageResource(R.drawable.n_ic_round_disagree)

        if (state?.isPrivacyPolicyAgreed == true) {
            ivAgreeCheck1.setImageResource(R.drawable.n_ic_enabled_check)
        } else {
            ivAgreeCheck1.setImageResource(R.drawable.n_ic_disabled_check)
        }

        if (state?.isIdentificationInfoAgreed == true) {
            ivAgreeCheck2.setImageResource(R.drawable.n_ic_enabled_check)
        } else {
            ivAgreeCheck2.setImageResource(R.drawable.n_ic_disabled_check)
        }

        if (state?.isServiceUsageAgreed == true) {
            ivAgreeCheck3.setImageResource(R.drawable.n_ic_enabled_check)
        } else {
            ivAgreeCheck3.setImageResource(R.drawable.n_ic_disabled_check)
        }

        if (state?.isMobileCarrierUsageAgreed == true) {
            ivAgreeCheck4.setImageResource(R.drawable.n_ic_enabled_check)
        } else {
            ivAgreeCheck4.setImageResource(R.drawable.n_ic_disabled_check)
        }
    }

    private fun enableDoneButton(enabled: Boolean) = binding.run {
        mcvDone.isEnabled = enabled
        if (enabled) {
            tvDone.setBackgroundResource(R.drawable.n_background_button_main)
        } else {
            tvDone.setBackgroundColor(resources.getColor(R.color.main_400, null))
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.state.collectLatest(::applyStateChanges) }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
//        val isAgreeAll = viewModel.state.value?.run {
//            isPrivacyPolicyAgreed
//                    && isIdentificationInfoAgreed
//                    && isServiceUsageAgreed
//                    && isMobileCarrierUsageAgreed
//        } ?: false
        onCancel()
    }
}