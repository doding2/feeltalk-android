package com.clonect.feeltalk.presentation.ui.user_nickname_input

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentUserNicknameInputBinding
import com.clonect.feeltalk.presentation.utils.addTextGradient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserNicknameInputFragment : Fragment() {

    private lateinit var binding: FragmentUserNicknameInputBinding
    private val viewModel: UserNicknameInputViewModel by viewModels()
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
            ivUserNicknameClear.setOnClickListener { etUserNickname.text.clear() }
            mcvNext.setOnClickListener { navigateToUserBirthPage() }
        }
    }


    private fun navigateToUserBirthPage() {
        // TODO
        Toast.makeText(requireContext(), "성공", Toast.LENGTH_SHORT).show()
    }


    private fun collectNickname() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.nickname.collectLatest { nickname ->
                if (nickname.isNullOrBlank()) {
                    viewModel.setInvalidWarning(null)
                    binding.ivUserNicknameClear.visibility = View.GONE
                    enableNextButton(false)
                    return@collectLatest
                }

                if (nickname.length >= 20) {
                    viewModel.setInvalidWarning("닉네임은 최대 20글자까지 가능합니다.")
                    binding.ivUserNicknameClear.visibility = View.VISIBLE
                    enableNextButton(false)
                    return@collectLatest
                }

                if (!viewModel.checkValidNickname(nickname)) {
                    viewModel.setInvalidWarning("닉네임에는 공백과 특수문자를 쓸 수 없습니다.")
                    binding.ivUserNicknameClear.visibility = View.VISIBLE
                    enableNextButton(false)
                    return@collectLatest
                }

                viewModel.setInvalidWarning(null)
                binding.ivUserNicknameClear.visibility = View.VISIBLE
                enableNextButton(true)

            }
        }
    }

    private fun collectInvalidWarning() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.invalidWarning.collectLatest {
                binding.tvInvalidWarning.text = it
            }
        }
    }


    private fun initNicknameEditText() = binding.etUserNickname.apply {
        setText(viewModel.nickname.value)
        addTextChangedListener {
            val input = it?.toString()
            viewModel.setNickname(input)
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