package com.clonect.feeltalk.new_presentation.ui.signUpNavigation.nickname

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentNicknameBinding
import com.clonect.feeltalk.new_presentation.ui.signUpNavigation.SignUpNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class NicknameFragment : Fragment() {

    private lateinit var binding: FragmentNicknameBinding
    private val viewModel: SignUpNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNicknameBinding.inflate(inflater, container, false)
        setKeyboardInsets()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
        viewModel.setSignUpProcess(66)
        viewModel.setCurrentPage("nickname")

        binding.run {
            etNickname.setText(viewModel.nickname.value)
            etNickname.addTextChangedListener {
                viewModel.setNickname(it?.toString() ?: "")
            }

            mcvNext.setOnClickListener { signUp() }
        }
    }


    private fun signUp() {
        hideKeyboard()
        viewModel.signUpNew()
//        viewModel.signUp()
    }


    private fun setKeyboardInsets() = binding.run {
        etNickname.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                etNickname.clearFocus()
            }
            false
        }

        llClearFocusArea.setOnClickListener { hideKeyboard() }
        viewClearFocusArea.setOnClickListener { hideKeyboard() }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            setNextButtonMargin(0)
            mcvNext.radius = 0f
        }

        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.stableInsetBottom
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                setNextButtonMargin(0)
                mcvNext.radius = 0f
                return@setOnApplyWindowInsetsListener insets
            }

            val isKeyboardUp = imeHeight != 0
            if (isKeyboardUp) {
                setNextButtonMargin(0)
                mcvNext.radius = 0f
            } else {
                etNickname.clearFocus()
                setNextButtonMargin((activity.dpToPx(20f).toInt()))
                mcvNext.radius = activity.dpToPx(30f)
            }

            if (imeHeight == 0) {
                binding.root.setPadding(0, 0, 0, 0)
            } else {
                binding.root.setPadding(0, 0, 0, imeHeight - getNavigationBarHeight())
            }

            insets
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etNickname.windowToken, 0)
    }

    private fun setNextButtonMargin(margin: Int) = binding.run {
        val params = mcvNext.layoutParams as LinearLayout.LayoutParams
        params.setMargins(margin, 0, margin, 0)
        mcvNext.layoutParams = params
    }

    private fun enableNextButton(enabled: Boolean) = binding.mcvNext.run {
        if (enabled) {
            isClickable = true
            isFocusable = true
            isEnabled = true
            binding.tvNext.setBackgroundResource(R.drawable.n_background_button_main)
        } else {
            isClickable = false
            isFocusable = false
            isEnabled = false
            binding.tvNext.setBackgroundColor(resources.getColor(R.color.main_400, null))
        }
    }


    private fun validateNickname(nickname: String): Boolean {
        if (nickname.isBlank()) return false
        if (nickname.length >= 10) return false

        val nicknamePattern = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9\\s]*$")
        val isNoSpecialCharacter = nicknamePattern.matcher(nickname).matches()
        if (!isNoSpecialCharacter) return false

        return true
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.nickname.collectLatest {
                    if (validateNickname(it)) {
                        enableNextButton(true)
                    } else {
                        enableNextButton(false)
                    }
                }
            }
        }
    }
}