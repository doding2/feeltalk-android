package com.clonect.feeltalk.new_presentation.ui.animatedSignUpNavigation

import android.annotation.SuppressLint
import android.app.Dialog
import com.clonect.feeltalk.R
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet.Motion
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.databinding.FragmentAnimatedSignUpBinding
import com.clonect.feeltalk.new_presentation.ui.animatedSignUpNavigation.authAgreement.AuthAgreementBottomSheetFragment
import com.clonect.feeltalk.new_presentation.ui.animatedSignUpNavigation.mobileCarrier.MobileCarrierBottomSheetFragment
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import com.clonect.feeltalk.new_presentation.ui.util.showOneButtonDialog
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * Created by doding2 on 2023/11/18.
 */
@AndroidEntryPoint
class AnimatedSignUpFragment : Fragment() {

    private lateinit var binding: FragmentAnimatedSignUpBinding
    private val viewModel: AnimatedSignUpViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog
    private lateinit var mobileCarrierBottomSheet: MobileCarrierBottomSheetFragment
    private lateinit var authAgreementBottomSheet: AuthAgreementBottomSheetFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAnimatedSignUpBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        val startPage = arguments?.getString("startPage", "coupleCode") ?: "coupleCode"
        viewModel.setStartPage(startPage)
        setFocusListener()
        setKeyboardInsets()
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }

            mcvStartButton.setOnClickListener { navigateFocus() }

            etName.addTextChangedListener { viewModel.setName(it?.toString() ?: "") }
            ivNameClear.setOnClickListener { etName.text = null }

            etBirth.addTextChangedListener { setBirth(it) }
            llBirthGender.setOnClickListener { setStateWithProgressed(AnimatedSignUpState.Gender) }
            etGender.addTextChangedListener { viewModel.setGender(it?.toString() ?: "") }

            mcvMobileCarrier.setOnClickListener { setStateWithProgressed(AnimatedSignUpState.MobileCarrier) }

            etPhoneNumber.addTextChangedListener { viewModel.setPhoneNumber(it?.toString() ?: "") }
            ivPhoneNumberClear.setOnClickListener { etPhoneNumber.text = null }

            mcvAgreement.setOnClickListener { clickAgreementButton() }
            ivAgreementDetail.setOnClickListener { setStateWithProgressed(AnimatedSignUpState.Agreement) }

            etAuthCode.addTextChangedListener { viewModel.setAuthCode(it?.toString() ?: "") }
            mcvAuthCodeButton.setOnClickListener { requestAuthCode() }

            tvNext.setOnClickListener { navigateFocus() }

            mcvDoneButton.setOnClickListener {
//                viewModel.matchAuthCode()
                navigateToSignUpNavigation()
            }
        }
    }

    private fun navigateToSignUpNavigation() {
        requireParentFragment()
            .findNavController()
            .navigate(
                R.id.action_animatedSignUpFragment_to_signUpNavigationFragment,
                bundleOf("startPage" to viewModel.startPage.value)
            )
    }


    private fun navigateFocus() {
        val nextFocus = when (viewModel.state.value) {
            AnimatedSignUpState.Start -> AnimatedSignUpState.Name
            AnimatedSignUpState.Default -> AnimatedSignUpState.Default
            AnimatedSignUpState.Name -> AnimatedSignUpState.Birth
            AnimatedSignUpState.Birth -> AnimatedSignUpState.MobileCarrier
            AnimatedSignUpState.Gender -> AnimatedSignUpState.MobileCarrier
            AnimatedSignUpState.MobileCarrier -> AnimatedSignUpState.PhoneNumber
            AnimatedSignUpState.PhoneNumber -> AnimatedSignUpState.Agreement
            AnimatedSignUpState.Agreement -> AnimatedSignUpState.AuthCodeReady
            AnimatedSignUpState.AuthCodeReady -> AnimatedSignUpState.AuthCodeReady
            AnimatedSignUpState.AuthCode -> AnimatedSignUpState.Default
            AnimatedSignUpState.AuthCodeError -> AnimatedSignUpState.Default
        }
        setStateWithProgressed(nextFocus)
    }

    private fun setBirth(birth: Editable?) {
        val birthString = birth?.toString()
            ?: return
        viewModel.setBirth(birthString)
        if (birthString.length >= 6) {
            setStateWithProgressed(AnimatedSignUpState.Gender)
        }
    }


    private fun showMobileCarrierBottomSheet() {
        if (::mobileCarrierBottomSheet.isInitialized && mobileCarrierBottomSheet.isAdded) {
            mobileCarrierBottomSheet.dismiss()
        }
        mobileCarrierBottomSheet = MobileCarrierBottomSheetFragment(
            onSelected = {
                viewModel.setMobileCarrier(it)
                setStateWithProgressed(AnimatedSignUpState.PhoneNumber)
            },
            onCancel = {
                setStateWithProgressed(AnimatedSignUpState.Default)
            }
        )
        mobileCarrierBottomSheet.show(requireActivity().supportFragmentManager, MobileCarrierBottomSheetFragment.TAG)
    }

    private fun showAuthAgreementBottomSheet() {
        if (::authAgreementBottomSheet.isInitialized && authAgreementBottomSheet.isAdded) {
            authAgreementBottomSheet.dismiss()
        }
        authAgreementBottomSheet = AuthAgreementBottomSheetFragment(
            isAllAccepted = viewModel.isAgreementAccepted.value,
            onDone = {
                viewModel.setAgreementAccepted(true)
                setStateWithProgressed(AnimatedSignUpState.AuthCodeReady)
            },
            onCancel = {
                setStateWithProgressed(AnimatedSignUpState.Default)
            }
        )
        authAgreementBottomSheet.show(requireActivity().supportFragmentManager, AuthAgreementBottomSheetFragment.TAG)
    }

    private fun clickAgreementButton() {
        if (viewModel.isAgreementAccepted.value) {
            viewModel.setAgreementAccepted(false)
        } else {
            viewModel.setAuthCodeState(viewModel.authCodeState.value.copy(isAgreementDisagreed = false))
            viewModel.setAgreementAccepted(true)
            setStateWithProgressed(AnimatedSignUpState.AuthCodeReady)
        }
    }

    private fun requestAuthCode() {
        viewModel.requestAuthCode()
    }


    private fun setStateWithProgressed(state: AnimatedSignUpState) = binding.run {
        if (mlMotion.progress == 0f || mlMotion.progress == 1f) {
            viewModel.setState(state)
            return@run
        }
        // when keyboard is down while animation is in progress.
        // wait until previous animation is ended
        val listener = getOneTimeTransitionCompletedListener {
            viewModel.setState(state)
        }
        mlMotion.addTransitionListener(listener)
    }

    private fun getOneTimeTransitionCompletedListener(onCompleted: () -> Unit): MotionLayout.TransitionListener {
        return object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}
            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
            override fun onTransitionCompleted(
                motionLayout: MotionLayout?,
                currentId: Int
            ) {
                onCompleted()
                motionLayout?.removeTransitionListener(this)
            }
        }
    }

    private fun setFocusListener() = binding.run {
        etName.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) setStateWithProgressed(AnimatedSignUpState.Name)
        }
        etName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                setStateWithProgressed(AnimatedSignUpState.Birth)
                return@setOnEditorActionListener true
            }
            false
        }
        etBirth.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) setStateWithProgressed(AnimatedSignUpState.Birth)
        }
        etBirth.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                setStateWithProgressed(AnimatedSignUpState.MobileCarrier)
                return@setOnEditorActionListener true
            }
            false
        }
        etGender.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) setStateWithProgressed(AnimatedSignUpState.Gender)
        }
        etGender.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                setStateWithProgressed(AnimatedSignUpState.MobileCarrier)
                return@setOnEditorActionListener true
            }
            false
        }

        etPhoneNumber.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) setStateWithProgressed(AnimatedSignUpState.PhoneNumber)
        }
        etPhoneNumber.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                setStateWithProgressed(AnimatedSignUpState.Agreement)
                return@setOnEditorActionListener true
            }
            false
        }

        etAuthCode.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                val isAuthCodeError = viewModel.authCodeState.value.run {
                    isTimeOut || isAuthCodeInvalid
                }
                if (isAuthCodeError) {
                    setStateWithProgressed(AnimatedSignUpState.AuthCodeError)
                } else {
                    setStateWithProgressed(AnimatedSignUpState.AuthCode)
                }
            }
        }
        etAuthCode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setStateWithProgressed(AnimatedSignUpState.Default)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setKeyboardInsets() = binding.run {
        root.setOnApplyWindowInsetsListener { _, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.stableInsetBottom
            }

            val isKeyboardUp = imeHeight != 0
            viewModel.setKeyboardUp(isKeyboardUp)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return@setOnApplyWindowInsetsListener insets
            }

            if (imeHeight == 0) {
                binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
            } else {
                binding.root.setPadding(0, getStatusBarHeight(), 0, imeHeight)
            }

            insets
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.etName.clearFocus()
        binding.etBirth.clearFocus()
        binding.etGender.clearFocus()
        binding.etPhoneNumber.clearFocus()
        binding.etAuthCode.clearFocus()
    }

    private fun showKeyboard(target: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(target, 0)
    }


    private fun applyGenderChanges(gender: String) = binding.run {
        if (gender.isBlank()) {
            ivBirthGender.setImageResource(R.drawable.n_ic_sign_up_birth_gender_empty)
        } else {
            ivBirthGender.setImageResource(R.drawable.n_ic_sign_up_birth_gender_filled)
        }
    }

    private fun applyMobileCarrierChanges(mobileCarrier: Int) = binding.run {
        tvMobileCarrier.setText(
            when (mobileCarrier) {
                1 -> R.string.sign_up_mobile_carrier_1
                2 -> R.string.sign_up_mobile_carrier_2
                3 -> R.string.sign_up_mobile_carrier_3
                4 -> R.string.sign_up_mobile_carrier_4
                5 -> R.string.sign_up_mobile_carrier_5
                6 -> R.string.sign_up_mobile_carrier_6
                else -> R.string.sign_up_mobile_carrier_1
            }
        )
    }

    private fun applyAgreementAcceptedChanges(isAccepted: Boolean) = binding.run {
        ivAgreementCheck.setImageResource(
            if (isAccepted) R.drawable.n_ic_round_agree
            else R.drawable.n_ic_round_disagree
        )
    }


    private fun applyStateChanges(state: AnimatedSignUpState) = binding.run {
        infoLog("state: $state")
        ivNameClear.visibility = View.GONE
        ivPhoneNumberClear.visibility = View.GONE
        tvNext.setText(R.string.add_challenge_next)

        when (state) {
            AnimatedSignUpState.Start -> {
                mlMotion.transitionToState(R.id.sign_up_start)
            }
            AnimatedSignUpState.Default -> {
                mlMotion.transitionToState(R.id.sign_up_default)
                hideKeyboard()
            }
            AnimatedSignUpState.Name -> {
                val listener = getOneTimeTransitionCompletedListener {
                    if (mlMotion.currentState == R.id.sign_up_name) {
                        etName.requestFocus()
                        ivNameClear.visibility = View.VISIBLE
                        showKeyboard(etName)
                    }
                }
                mlMotion.addTransitionListener(listener)
                mlMotion.transitionToState(R.id.sign_up_name)
            }
            AnimatedSignUpState.Birth -> {
                mlMotion.transitionToState(R.id.sign_up_birth)
                etBirth.requestFocus()
                showKeyboard(etBirth)
            }
            AnimatedSignUpState.Gender -> {
                if (mlMotion.currentState != R.id.sign_up_birth) {
                    mlMotion.transitionToState(R.id.sign_up_birth)
                }
                etGender.requestFocus()
                showKeyboard(etGender)
            }
            AnimatedSignUpState.MobileCarrier -> {
                mlMotion.transitionToState(R.id.sign_up_mobile_carrier)
                hideKeyboard()
                showMobileCarrierBottomSheet()
            }
            AnimatedSignUpState.PhoneNumber -> {
                val listener = getOneTimeTransitionCompletedListener {
                    if (mlMotion.currentState == R.id.sign_up_phone_number) {
                        etPhoneNumber.requestFocus()
                        ivPhoneNumberClear.visibility = View.VISIBLE
                        showKeyboard(etPhoneNumber)
                    }
                }
                mlMotion.addTransitionListener(listener)
                mlMotion.transitionToState(R.id.sign_up_phone_number)
            }
            AnimatedSignUpState.Agreement -> {
                mlMotion.transitionToState(R.id.sign_up_agreement)
                hideKeyboard()
                showAuthAgreementBottomSheet()
            }
            AnimatedSignUpState.AuthCodeReady -> {
                mlMotion.transitionToState(R.id.sign_up_auth_code_ready)
                hideKeyboard()
            }
            AnimatedSignUpState.AuthCode -> {
                val listener = getOneTimeTransitionCompletedListener {
                    if (mlMotion.currentState == R.id.sign_up_auth_code) {
                        etAuthCode.requestFocus()
                        showKeyboard(etName)
                    }
                }
                mlMotion.addTransitionListener(listener)
                mlMotion.transitionToState(R.id.sign_up_auth_code)
                tvNext.setText(R.string.add_challenge_done)
            }
            AnimatedSignUpState.AuthCodeError -> {
                val listener = getOneTimeTransitionCompletedListener {
                    if (mlMotion.currentState == R.id.sign_up_auth_code_error) {
                        etAuthCode.requestFocus()
                        showKeyboard(etName)
                    }
                }
                mlMotion.addTransitionListener(listener)
                mlMotion.transitionToState(R.id.sign_up_auth_code_error)
                tvNext.setText(R.string.add_challenge_done)
            }
        }
    }


    private fun applyKeyboardUpChanges(isKeyboardUp: Boolean) = binding.run {
        if (viewModel.state.value == AnimatedSignUpState.Start) {
            return@run
        }

        if (isKeyboardUp) {
            mcvNextBar.visibility = View.VISIBLE
            return@run
        }
        if (mlMotion.currentState == R.id.sign_up_mobile_carrier
            || mlMotion.currentState == R.id.sign_up_agreement
            || mlMotion.currentState == R.id.sign_up_auth_code_ready) {
            mcvNextBar.visibility = View.GONE
            return@run
        }

        setStateWithProgressed(AnimatedSignUpState.Default)
        mcvNextBar.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun applyAuthCodeRemainingTimeChanges(remainingTime: Long) = binding.run {
        if (remainingTime == 0L) {
            tvAuthCodeTimer.setTextColor(requireContext().getColor(R.color.gray_500))
        } else {
            tvAuthCodeTimer.setTextColor(Color.BLACK)
        }

        if (remainingTime == 180000L) {
            tvAuthCodeTimer.visibility = View.GONE
            return@run
        }
        tvAuthCodeTimer.visibility = View.VISIBLE
        val totalSeconds = ceil(remainingTime / 1000f).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        val minutesStr = if (minutes < 10) "0$minutes" else minutes.toString()
        val secondsStr = if (seconds < 10) "0$seconds" else seconds.toString()
        tvAuthCodeTimer.text = "$minutesStr:$secondsStr"
    }

    private fun applyAuthCodeStateChanges(state: AuthCodeState) = binding.run {
        if (state.isAgreementDisagreed) {
            tvAgreementWarning.visibility = View.VISIBLE
        } else {
            tvAgreementWarning.visibility = View.GONE
        }

        if (state.isRequested) {
            tvAuthCodeButton.setText(R.string.sign_up_auth_code_retry_button)
            tvAuthCodeButton.setTextColor(Color.BLACK)
            mcvAuthCodeButton.setCardBackgroundColor(Color.WHITE)
            mcvAuthCodeButton.strokeWidth = requireContext().dpToPx(1f)
        } else {
            tvAuthCodeButton.setText(R.string.sign_up_auth_code_request_button)
            tvAuthCodeButton.setTextColor(Color.WHITE)
            mcvAuthCodeButton.setCardBackgroundColor(requireContext().getColor(R.color.main_500))
            mcvAuthCodeButton.strokeWidth = 0
        }

        if (state.isAuthCodeInvalid) {
            if (mlMotion.currentState == R.id.sign_up_auth_code)
                viewModel.setState(AnimatedSignUpState.AuthCodeError)
            mcvAuthCodeErrorBorder.setCardBackgroundColor(Color.WHITE)
            mcvAuthCodeErrorBorder.strokeWidth = requireContext().dpToPx(1f)
            tvAuthCodeWarning.setText(R.string.sign_up_auth_code_warning_error_2)
            tvAuthCodeWarning.setTextColor(requireContext().getColor(R.color.system_error))
            return@run
        }
        if (state.isTimeOut) {
            if (mlMotion.currentState == R.id.sign_up_auth_code)
                viewModel.setState(AnimatedSignUpState.AuthCodeError)
            mcvAuthCodeErrorBorder.setCardBackgroundColor(Color.WHITE)
            mcvAuthCodeErrorBorder.strokeColor = requireContext().getColor(R.color.system_error)
            mcvAuthCodeErrorBorder.strokeWidth = requireContext().dpToPx(1f)
            tvAuthCodeWarning.setText(R.string.sign_up_auth_code_warning_error_1)
            tvAuthCodeWarning.setTextColor(requireContext().getColor(R.color.system_error))
            return@run
        }

        if (mlMotion.currentState == R.id.sign_up_auth_code_error)
            viewModel.setState(AnimatedSignUpState.AuthCode)
        mcvAuthCodeErrorBorder.setCardBackgroundColor(Color.TRANSPARENT)
        mcvAuthCodeErrorBorder.strokeWidth = 0
        tvAuthCodeWarning.setText(R.string.sign_up_auth_code_warning)
        tvAuthCodeWarning.setTextColor(requireContext().getColor(R.color.gray_500))
    }

    private fun applyIsPersonInfoInvalidChanges(isParsonInfoInvalid: Boolean) {
        if (isParsonInfoInvalid) {
            showOneButtonDialog(
                title = requireContext().getString(R.string.sign_up_person_info_invalid_dialog_title),
                body = requireContext().getString(R.string.sign_up_person_info_invalid_dialog_body),
                onConfirm = {}
            )
        }
    }

    private fun applyIsDoneEnabledChanges(isDone: Boolean) = binding.run {
        mcvDoneButton.isEnabled = isDone
        if (isDone) {
            tvDoneButton.setBackgroundResource(R.drawable.n_background_button_main)
        } else {
            tvDoneButton.setBackgroundColor(requireContext().getColor(R.color.main_400))
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun showSnackBar(message: String) {
        val decorView = activity?.window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = message,
            duration = Snackbar.LENGTH_SHORT,
            onClick = {
                it.dismiss()
            }
        ).show()
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.gender.collectLatest(::applyGenderChanges) }
            launch { viewModel.mobileCarrier.collectLatest(::applyMobileCarrierChanges) }
            launch { viewModel.isAgreementAccepted.collectLatest(::applyAgreementAcceptedChanges) }
            launch { viewModel.state.collectLatest(::applyStateChanges) }
            launch { viewModel.isKeyboardUp.collectLatest(::applyKeyboardUpChanges) }
            launch { viewModel.authCodeRemainingTime.collectLatest(::applyAuthCodeRemainingTimeChanges) }
            launch { viewModel.authCodeState.collectLatest(::applyAuthCodeStateChanges) }
            launch { viewModel.isPersonInfoInvalid.collectLatest(::applyIsPersonInfoInvalidChanges) }
            launch { viewModel.isDoneEnabled.collectLatest(::applyIsDoneEnabledChanges) }
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isEdited()) {
                    showConfirmDialog(
                        title = requireContext().getString(R.string.sign_up_auth_pop_back_dialog_title),
                        body = requireContext().getString(R.string.sign_up_auth_pop_back_dialog_body),
                        onConfirm = {
                            findNavController().popBackStack()
                        }
                    )
                    return
                }
                findNavController().navigate(R.id.action_animatedSignUpFragment_pop_back)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}