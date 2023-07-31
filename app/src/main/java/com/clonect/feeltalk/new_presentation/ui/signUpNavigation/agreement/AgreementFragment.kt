package com.clonect.feeltalk.new_presentation.ui.signUpNavigation.agreement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentAgreementBinding
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.user.SocialType
import com.clonect.feeltalk.new_presentation.ui.signUpNavigation.SignUpNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
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
        enableNextButton(isRequirementAllAgreed())
        viewModel.setSignUpProcess(33)
        viewModel.getSocialToken()
        viewModel.setCurrentPage("agreement")

        binding.run {
            root.layoutTransition.apply {
                setDuration(600)
            }
            mcvCertifyAdult.setOnClickListener { certifyAdult() }
            clAgreeAll.setOnClickListener { toggleAgreeAll() }
            llAgreement1.setOnClickListener { toggleAgreeService() }
            llAgreement2.setOnClickListener { toggleAgreePrivacy() }
            llAgreement3.setOnClickListener { toggleAgreeSensitive() }
            llAgreement4.setOnClickListener { toggleAgreeMarketing() }
            mcvNext.setOnClickListener { navigateToNickname() }
        }
    }

    private fun certifyAdult() {
        viewModel.certifyAdult()
    }

    private fun navigateToNickname() {
        viewModel.setAgreementProcessed(true)
    }

    private fun toggleAgreeAll() = viewModel.run {
        // 이미 모두 체크된 상태에서 클릭
        if (isAgreeAll.value) {
            setAgreeAll(false)
            setServiceAgreed(false)
            setPrivacyAgreed(false)
            setSensitiveAgreed(false)
            setMarketingAgreed(false)
            enableNextButton(false)
        }
        // 모두 체크되지 않은 상태에서 클릭
        else {
            setAgreeAll(true)
            setServiceAgreed(true)
            setPrivacyAgreed(true)
            setSensitiveAgreed(true)
            setMarketingAgreed(true)
            enableNextButton(true)
        }
    }

    private fun toggleAgreeService() = viewModel.run {
        setServiceAgreed(!isServiceAgreed.value)
        enableNextButton(isRequirementAllAgreed())
        setAgreeAll(isAllAgreed())
    }

    private fun toggleAgreePrivacy() = viewModel.run {
        setPrivacyAgreed(!isPrivacyAgreed.value)
        enableNextButton(isRequirementAllAgreed())
        setAgreeAll(isAllAgreed())
    }

    private fun toggleAgreeSensitive() = viewModel.run {
        setSensitiveAgreed(!isSensitiveAgreed.value)
        enableNextButton(isRequirementAllAgreed())
        setAgreeAll(isAllAgreed())
    }

    private fun toggleAgreeMarketing() = viewModel.run {
        setMarketingAgreed(!isMarketingAgreed.value)
        enableNextButton(isRequirementAllAgreed())
        setAgreeAll(isAllAgreed())
    }


    private fun isAllAgreed() = viewModel.run {
        isServiceAgreed.value && isPrivacyAgreed.value && isSensitiveAgreed.value && isMarketingAgreed.value
    }

    private fun isRequirementAllAgreed() = viewModel.run {
        isServiceAgreed.value && isPrivacyAgreed.value && isSensitiveAgreed.value
    }

    private fun enableNextButton(enabled: Boolean) = binding.mcvNext.run {
        if (enabled) {
            setCardBackgroundColor(resources.getColor(R.color.main_500, null))
            isClickable = true
            isFocusable = true
            isEnabled = true
        } else {
            setCardBackgroundColor(resources.getColor(R.color.main_400, null))
            isClickable = false
            isFocusable = false
            isEnabled = false
        }
    }

    private fun changeAdultView(isAdult: Boolean) = binding.run {
        if (isAdult) {
            spacerAdult.visibility = View.GONE
            tvAdultAnnounce.visibility = View.GONE
            mcvCertifyAdult.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
            mcvCertifyAdult.strokeWidth = 0
            tvCertifyAdult.setText(R.string.agreement_certify_adult_done)
            tvCertifyAdult.setTextColor(requireContext().getColor(R.color.gray_400))
            ivCertifyAdultDone.visibility = View.VISIBLE

            mcvNext.visibility = View.VISIBLE
            spacerAgreement.visibility = View.VISIBLE
            clAgreeAll.visibility = View.VISIBLE
            llSubAgreements.visibility = View.VISIBLE
        } else {
            spacerAdult.visibility = View.VISIBLE
            tvAdultAnnounce.visibility = View.VISIBLE
            mcvCertifyAdult.setCardBackgroundColor(requireContext().getColor(R.color.white))
            mcvCertifyAdult.strokeWidth = requireContext().dpToPx(1f).toInt()
            tvCertifyAdult.setText(R.string.agreement_certify_adult)
            tvCertifyAdult.setTextColor(requireContext().getColor(R.color.black))
            ivCertifyAdultDone.visibility = View.GONE

            spacerAgreement.visibility = View.GONE
            clAgreeAll.visibility = View.GONE
            mcvNext.visibility = View.GONE
            llSubAgreements.visibility = View.GONE
        }
    }

    private fun changeAgreeAllView(agreeAll: Boolean) = binding.run {
        ivAgreeAll.setImageResource(
            if (agreeAll) R.drawable.n_ic_agree_all
            else R.drawable.n_ic_disagree_all
        )
    }

    private fun changeAgreementView(isEnabled: Boolean, check: ImageView, text: TextView) {
        check.setImageResource(
            if (isEnabled) R.drawable.n_ic_enabled_check
            else R.drawable.n_ic_disabled_check
        )
        text.setTextColor(requireContext().getColor(
            if (isEnabled) R.color.black
            else R.color.gray_600
        ))
    }

    private fun changeSocialEmailView(socialToken: SocialToken?) = binding.run {
        if (socialToken?.email != null) {
            mcvSnsEmailContainer.visibility = View.VISIBLE
            tvNicknameTitle.visibility = View.VISIBLE

            tvEmail.text = socialToken.email
            tvNicknameTitle.text = "${socialToken.name ?: socialToken.email} ${requireContext().getString(R.string.agreement_sub_title)}"
            ivSnsIcon.setImageResource(
                when (socialToken.type) {
                    SocialType.Kakao -> R.drawable.n_image_kakao
                    SocialType.Naver -> R.drawable.n_image_naver
                    SocialType.Google -> R.drawable.n_image_google
                    SocialType.Apple -> R.drawable.n_image_apple
                }
            )
        } else {
            mcvSnsEmailContainer.visibility = View.GONE
            tvNicknameTitle.visibility = View.GONE
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.socialToken.collectLatest(::changeSocialEmailView) }
            launch { viewModel.isAdult.collectLatest(::changeAdultView) }
            launch { viewModel.isAgreeAll.collectLatest(::changeAgreeAllView) }
            launch {
                viewModel.isServiceAgreed.collectLatest {
                    binding.run {
                        changeAgreementView(it, ivAgreementCheck1, tvAgreement1)
                    }
                }
            }
            launch {
                viewModel.isPrivacyAgreed.collectLatest {
                    binding.run {
                        changeAgreementView(it, ivAgreementCheck2, tvAgreement2)
                    }
                }
            }
            launch {
                viewModel.isSensitiveAgreed.collectLatest {
                    binding.run {
                        changeAgreementView(it, ivAgreementCheck3, tvAgreement3)
                    }
                }
            }
            launch {
                viewModel.isMarketingAgreed.collectLatest {
                    binding.run {
                        changeAgreementView(it, ivAgreementCheck4, tvAgreement4)
                    }
                }
            }
        }
    }
}