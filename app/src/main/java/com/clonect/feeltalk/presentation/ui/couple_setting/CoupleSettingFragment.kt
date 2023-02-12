package com.clonect.feeltalk.presentation.ui.couple_setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.FragmentCoupleSettingBinding
import com.clonect.feeltalk.presentation.utils.showBreakUpCoupleDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@AndroidEntryPoint
class CoupleSettingFragment : Fragment() {

    private lateinit var binding: FragmentCoupleSettingBinding
    private lateinit var onBackCallback: OnBackPressedCallback
    private val viewModel: CoupleSettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectUserInfo()
        collectPartnerInfo()
        collectCoupleAnniversary()

        binding.apply {
            btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            
            clPartnerProfile.setOnClickListener { 
                showBreakUpCoupleDialog(
                    partnerNickname = viewModel.partnerInfo.value.nickname,
                    onConfirm = {
                        lifecycleScope.launch {
                            val isSuccessful = viewModel.breakUpCouple()
                            if (isSuccessful) {
                                Toast.makeText(requireContext(), "커플이 해제되었습니다", Toast.LENGTH_SHORT).show()
                                navigateToSignUpPage()
                            } else {
                                Toast.makeText(requireContext(), "실패했습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
        
    }

    private fun collectUserInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.userInfo.collectLatest {
                binding.textMyName.text = it.nickname
                binding.textMyBirthDate.text = it.birth
            }
        }
    }

    private fun collectPartnerInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.partnerInfo.collectLatest {
                binding.textPartnerName.text = it.nickname
                binding.textPartnerBirthDate.text = it.birth
            }
        }
    }

    private fun collectCoupleAnniversary() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.coupleAnniversary.collectLatest {
                binding.textCoupleAnniversary.text = it?.replace("/", ". ")
                it?.let {
                    binding.textDDayValue.text = calculateDDay(it)
                }
            }
        }
    }

    private fun calculateDDay(date: String): String {
        try {
            val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val anniversaryDate = format.parse(date) ?: return "0"
            val ddayPoint = (Date().time - anniversaryDate.time).toDouble() / Constants.ONE_DAY
            return ceil(ddayPoint).toLong().toString()
        } catch (e: Exception) {
            return "0"
        }
    }


    private fun navigateToSignUpPage() {
        findNavController()
            .navigate(R.id.action_coupleSettingFragment_to_signUpFragment)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}