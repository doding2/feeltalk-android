package com.clonect.feeltalk.presentation.ui.input

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentUserNicknameInputBinding
import com.clonect.feeltalk.presentation.utils.addTextGradient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserNicknameInputFragment : Fragment() {

    private lateinit var binding: FragmentUserNicknameInputBinding
    private val viewModel: UserInputViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserNicknameInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNickname()
        collectInvalidWarning()

        enableNextButton(false)
        initNicknameEditText()

        binding.apply {
            textMessageHighlight.addTextGradient()
            btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            mcvNext.setOnClickListener { navigateToUserBirthPage() }
        }
    }


    private fun navigateToUserBirthPage() {
        findNavController().navigate(R.id.action_userNicknameInputFragment_to_userBirthInputFragment)
    }


    private fun collectNickname() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.nickname.collectLatest { nickname ->
                if (nickname.isNullOrBlank()) {
                    viewModel.setInvalidNicknameWarning(null)
                    binding.etUserNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    enableNextButton(false)
                    return@collectLatest
                }

                if (nickname.length >= 20) {
                    viewModel.setInvalidNicknameWarning(getString(R.string.warning_long_nickname))
                    binding.etUserNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
                    enableNextButton(false)
                    return@collectLatest
                }

                if (!viewModel.checkValidNickname(nickname)) {
                    viewModel.setInvalidNicknameWarning(getString(R.string.warning_special_character_nickname))
                    binding.etUserNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
                    enableNextButton(false)
                    return@collectLatest
                }

                viewModel.setInvalidNicknameWarning(null)
                binding.etUserNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0)
                enableNextButton(true)

            }
        }
    }

    private fun collectInvalidWarning() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.invalidNicknameWarning.collectLatest {
                binding.tvInvalidWarning.text = it
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initNicknameEditText() = binding.etUserNickname.apply {
        setText(viewModel.nickname.value)
        addTextChangedListener {
            val input = it?.toString()?.trim()
            viewModel.setNickname(input)
        }
        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                    binding.etUserNickname.text.clear()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
        setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                && getNextButtonEnabled()
            ) {
                navigateToUserBirthPage()
                return@setOnEditorActionListener true
            }
            false
        }
        requestFocus()
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

    private fun getNextButtonEnabled() = binding.mcvNext.isEnabled


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