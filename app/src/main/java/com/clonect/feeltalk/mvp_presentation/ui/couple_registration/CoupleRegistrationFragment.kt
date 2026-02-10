package com.clonect.feeltalk.mvp_presentation.ui.couple_registration

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCoupleRegistrationBinding
import com.clonect.feeltalk.release_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.mvp_presentation.utils.addTextGradient
import com.clonect.feeltalk.mvp_presentation.utils.showAlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class CoupleRegistrationFragment : Fragment() {

    private lateinit var binding: FragmentCoupleRegistrationBinding
    private val viewModel: CoupleRegistrationViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleRegistrationBinding.inflate(inflater, container, false)
        setStatusBarColor(Color.WHITE, true)
        setNavigationBarColor(Color.WHITE, true)
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectIsLoading()
        collectMyCoupleCode()
        initPartnerCoupleCodeValue()

        collectIsKeyPairExchangingCompleted()

        binding.apply {
            textMessageHighlight.addTextGradient()
            textMyCoupleCode.addTextGradient()
            btnBack.setOnClickListener {
                onBackCallback.handleOnBackPressed()
            }
            btnNext.setOnClickListener {
                sendPartnerCode()
            }
            cvCopyCode.setOnClickListener {
                copyCodeToClipBoard()
            }

            llLeaveFeeltalk.setOnClickListener { leaveFeeltalk() }
        }

        initPartnerCodeEditText()
        collectToast()
        enableNextButton(false)
    }

    private fun sendPartnerCode() = lifecycleScope.launch {
        viewModel.sendPartnerCode()
    }


    private fun navigateCoupleRegistrationDonePage() {
        findNavController().navigate(R.id.action_coupleRegistrationFragment_to_coupleRegistrationDoneFragment)
    }

    private fun copyCodeToClipBoard() {
        val text = viewModel.myCoupleCode.value
        if (text.isBlank()) return

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("초대코드", text)
        clipboard.setPrimaryClip(clip)
    }


    private fun collectIsLoading() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.isLoading.collectLatest {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun collectMyCoupleCode() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.myCoupleCode.collectLatest {
                binding.textMyCoupleCode.text = it
            }
        }
    }

    private fun collectIsKeyPairExchangingCompleted() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.isKeyPairExchangingCompleted.collectLatest { isCompleted ->
                if (isCompleted) {
                    navigateCoupleRegistrationDonePage()
                }
            }
        }
    }

    private fun collectToast() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.toastMessage.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initPartnerCoupleCodeValue() {
        binding.etPartnerCode.setText(viewModel.partnerCoupleCodeInput.value)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPartnerCodeEditText() = binding.etPartnerCode.apply {
        addTextChangedListener {
            val input = it?.toString() ?: ""
            viewModel.setPartnerCodeInput(input)

            val clearIcon = if (input.isBlank()) {
                enableNextButton(false)
                0
            } else {
                if (input == viewModel.myCoupleCode.value) {
                    enableNextButton(false)
                } else {
                    enableNextButton(true)
                }
                R.drawable.ic_clear
            }
            setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
        }

        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                    binding.etPartnerCode.setText("")
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun enableNextButton(enabled: Boolean) = binding.btnNext.apply {
        val colorId =
            if (enabled) R.color.today_question_enter_answer_button_enabled_color
            else R.color.today_question_enter_answer_button_disabled_color

        setCardBackgroundColor(ResourcesCompat.getColor(resources, colorId, null))
        isClickable = enabled
        isFocusable = enabled
        isEnabled = enabled
    }



    private fun leaveFeeltalk() {
        showAlertDialog(
            title = "필로우톡 회원 탈퇴",
            message = "정말 탈퇴하시겠습니까?",
            confirmButtonText = "확 인",
            onConfirmClick = {
                lifecycleScope.launch {
                    val succeed = viewModel.leaveFeeltalk()
                    if (succeed) {
                        logOut()
                    } else {
                        Toast.makeText(requireContext(), "탈퇴에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    private fun logOut() = lifecycleScope.launch {
        tryGoogleLogOut()
        tryKakaoLogOut()
        tryNaverLogOut()
        restartApplication()
    }

    private suspend fun tryKakaoLogOut() = suspendCoroutine { continuation ->
        UserApiClient.instance.logout { error ->
            if (error == null) {
                continuation.resume(true)
            } else {
                continuation.resume(false)
            }
        }
    }

    private fun tryNaverLogOut(): Boolean {
        NaverIdLoginSDK.logout()
        return true
    }

    private suspend fun tryGoogleLogOut() = suspendCoroutine { continuation ->
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInClient.signOut()
            .addOnSuccessListener {
                continuation.resume(true)
            }
            .addOnFailureListener {
                continuation.resume(false)
            }
    }

    private fun restartApplication() {
        val pm = requireContext().packageManager
        val intent = pm.getLaunchIntentForPackage(requireContext().packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        requireContext().startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }



    private fun setStatusBarColor(
        @ColorInt color: Int,
        isLight: Boolean
    ) {
        activity?.window?.apply {
            statusBarColor = color
            WindowInsetsControllerCompat(this, binding.root).apply {
                isAppearanceLightStatusBars = isLight
            }
        }
    }

    private fun setNavigationBarColor(
        @ColorInt color: Int,
        isLight: Boolean
    ) {
        activity?.window?.apply {
            navigationBarColor = color
            WindowInsetsControllerCompat(this, binding.root).apply {
                isAppearanceLightNavigationBars = isLight
            }
        }
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
        loadingDialog.dismiss()
    }

}