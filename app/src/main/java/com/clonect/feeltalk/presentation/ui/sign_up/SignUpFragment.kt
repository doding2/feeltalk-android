package com.clonect.feeltalk.presentation.ui.sign_up

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSignUpBinding
import com.clonect.feeltalk.presentation.utils.infoLog
import com.clonect.feeltalk.presentation.utils.makeLoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectState()
        collectToast()
        collectIsLoading()
        initCustomerServiceText()

        binding.apply {
            mcvSignUpKakao.setOnClickListener { clickKakaoButton() }
            mcvSignUpNaver.setOnClickListener { clickNaverButton() }
            mcvSignUpGoogle.setOnClickListener { clickGoogleButton() }
        }
    }

    private fun clickGoogleButton() = lifecycleScope.launch {
        setSignUpButtonsEnabled(false)
        logOut()
        launchGoogleSignUp()
    }

    private fun clickKakaoButton() = lifecycleScope.launch {
        setSignUpButtonsEnabled(false)
        logOut()
        signInWithKakao()
    }

    private fun clickNaverButton() = lifecycleScope.launch {
        setSignUpButtonsEnabled(false)
        logOut()
        signInWithNaver()
    }



    private fun initCustomerServiceText() = binding.tvCustomerService.apply {
        paintFlags = Paint.UNDERLINE_TEXT_FLAG
        setOnClickListener {
            // TODO 문의하기로 넘어가기
            navigateToHomePage()
        }
    }

    private fun navigateToHomePage() {
        viewModel.setLoading(false)
        findNavController().navigate(R.id.action_signUpFragment_to_bottomNavigationFragment)
    }

    private fun navigateToUserNicknameInputPage() {
        viewModel.setLoading(false)
        findNavController().navigate(R.id.action_signUpFragment_to_userNicknameInputFragment)
    }

    private fun navigateToCoupleRegistrationPage() {
        viewModel.setLoading(false)
        findNavController().navigate(R.id.action_signUpFragment_to_coupleRegistrationFragment)
    }


    private fun collectToast() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.toast.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun collectState() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.isSignUpSuccessful.collectLatest {
                    if (it) {
                        navigateToUserNicknameInputPage()
                        return@collectLatest
                    }
                }
            }

            launch {
                viewModel.run {
                    isLogInSuccessful.collectLatest {
                        if (!it) { return@collectLatest }

                        if (!isUserInfoEntered.value) {
                            navigateToUserNicknameInputPage()
                            return@collectLatest
                        }

                        if (!isUserCouple.value) {
                            navigateToCoupleRegistrationPage()
                            return@collectLatest
                        }

                        navigateToHomePage()
                    }
                }
            }
        }
    }

    
    private suspend fun logOut() {
        tryGoogleLogOut()
        tryKakaoLogOut()
        tryNaverLogOut()
        viewModel.clearAllTokens()
    }


    private fun signInWithKakao() = lifecycleScope.launch func@{
        UserApiClient.instance.run {
            if (!isKakaoTalkLoginAvailable(requireContext())) {
                tryKakaoLogOut()
            }

            loginWithKakaoTalk(requireContext()) { token, error ->
                error?.let {
                    lifecycleScope.launch {
                        infoLog("카카오 로그인 실패: $error")
                        tryKakaoLogOut()
                    }
                    setSignUpButtonsEnabled(true)
                    return@loginWithKakaoTalk
                }

                token?.let {
                    lifecycleScope.launch {
                        val accessToken = token.accessToken

                        val isSuccessful = viewModel.signUpWithKakao(accessToken)
                        if (isSuccessful) {
                            infoLog( "카카오 로그인 성공")
                        } else {
                            infoLog("카카오 로그인 실패")
                            tryKakaoLogOut()
                        }
                        setSignUpButtonsEnabled(true)
                    }
                }
            }
        }
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


    private val naverSignUpCallback = object: OAuthLoginCallback {
        override fun onError(errorCode: Int, message: String) {
            lifecycleScope.launch {
                infoLog("Fail to sign up with naver: ${message}")
                tryNaverLogOut()
                setSignUpButtonsEnabled(true)
            }
        }

        override fun onFailure(httpStatus: Int, message: String) {
            lifecycleScope.launch {
                infoLog("Fail to sign up with naver -> status: ${httpStatus}, message: ${message}")
                tryNaverLogOut()
                setSignUpButtonsEnabled(true)
            }
        }

        override fun onSuccess() {
            lifecycleScope.launch {
                val accessToken = NaverIdLoginSDK.getAccessToken()
                if (accessToken == null) {
                    tryNaverLogOut()
                    infoLog("Fail to sign up with naver: Access Token is null")
                    Toast.makeText(requireContext(), "네이버로 가입하는데 실패했습니다", Toast.LENGTH_SHORT).show()
                    setSignUpButtonsEnabled(true)
                    return@launch
                }
                
                val isSuccessful = viewModel.signUpWithNaver(accessToken)
                if (!isSuccessful) {
                    tryNaverLogOut()
                }
                infoLog("네이버 로그인 액세스 토큰: $accessToken")
                setSignUpButtonsEnabled(true)
            }
        }
    }

    private fun signInWithNaver() {
        NaverIdLoginSDK.authenticate(requireContext(), naverSignUpCallback)
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

    private fun launchGoogleSignUp() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
            .requestServerAuthCode(BuildConfig.GOOGLE_AUTH_CLIENT_ID, true)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val intent = mGoogleSignInClient.signInIntent
        googleSignUpLauncher.launch(intent)
    }

    private val googleSignUpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignUpResult(task)
        }
        setSignUpButtonsEnabled(true)
    }

    private fun handleGoogleSignUpResult(completedTask: Task<GoogleSignInAccount>) = lifecycleScope.launch {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken.toString()
            val serverAuthCode = account?.serverAuthCode.toString()

            val isSuccessful = viewModel.signUpWithGoogle(idToken, serverAuthCode)
            if (!isSuccessful) {
                tryGoogleLogOut()
            }
        } catch (e: ApiException) {
            tryGoogleLogOut()
            Toast.makeText(requireContext(), "구글로 가입하는데 실패했습니다", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setSignUpButtonsEnabled(enabled: Boolean) = binding.apply {
        mcvSignUpKakao.isEnabled = enabled
        mcvSignUpNaver.isEnabled = enabled
        mcvSignUpGoogle.isEnabled = enabled
        mcvSignUpApple.isEnabled = enabled
    }

}