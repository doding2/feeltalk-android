package com.clonect.feeltalk.presentation.ui.input

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentUserGenderInputBinding
import com.clonect.feeltalk.presentation.utils.addTextGradient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserGenderInputFragment : Fragment() {

    private lateinit var binding: FragmentUserGenderInputBinding
    private val viewModel: UserInputViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserGenderInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectGender()

        binding.apply {
            textMessageHighlight.addTextGradient()

            btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            mcvNext.setOnClickListener { navigateToUserNicknamePage() }

            ivFemale.setOnClickListener {
                viewModel.setGender("female")
            }
            ivMale.setOnClickListener {
                viewModel.setGender("male")
            }
        }
    }


    private fun navigateToUserNicknamePage() {
        findNavController().navigate(R.id.userNicknameInputFragment)
    }


    private fun collectGender() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.gender.collectLatest {
                if (it == "male") {
                    binding.ivFemale.setImageResource(R.drawable.ic_female_unselected)
                    binding.ivMale.setImageResource(R.drawable.ic_male_selected)
                    enableNextButton(true)
                    return@collectLatest
                }
                if (it == "female") {
                    binding.ivFemale.setImageResource(R.drawable.ic_female_selected)
                    binding.ivMale.setImageResource(R.drawable.ic_male_unselected)
                    enableNextButton(true)
                    return@collectLatest
                }

                binding.ivFemale.setImageResource(R.drawable.ic_female_unselected)
                binding.ivMale.setImageResource(R.drawable.ic_male_unselected)
                enableNextButton(false)
            }
        }
    }


    private fun enableNextButton(enabled: Boolean) {
        binding.mcvNext.apply {
            val colorId =
                if (enabled) R.color.today_question_enter_answer_button_enabled_color
                else R.color.today_question_enter_answer_button_disabled_color

            setCardBackgroundColor(ResourcesCompat.getColor(resources, colorId, null))
            isClickable = enabled
            isFocusable = enabled
            isEnabled = enabled
        }
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