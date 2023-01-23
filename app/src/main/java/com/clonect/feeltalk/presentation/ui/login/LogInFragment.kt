package com.clonect.feeltalk.presentation.ui.login

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.R
import com.clonect.feeltalk.domain.model.user.GoogleTokens
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.databinding.FragmentLogInBinding
import com.clonect.feeltalk.domain.model.user.LogInEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val viewModel: LogInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        binding.apply {

            etEmail.addTextChangedListener { viewModel.setEmail(it.toString().trim()) }
            etPassword.addTextChangedListener { viewModel.setPassword(it.toString().trim()) }

            btnLogIn.setOnClickListener {
                logInWithEmail()
            }

            btnGoogleLogIn.setOnClickListener {
                launchGoogleLogIn()
            }

            btnSignUp.setOnClickListener {
                navigateToSignUpPage()
            }

            btnSkip.setOnClickListener {
                navigateToHomePage()
            }
        }

        initEditTexts()

        return binding.root
    }

    private fun initEditTexts() = lifecycleScope.launchWhenStarted {
        binding.etEmail.setText(viewModel.emailStateFlow.value)
        binding.etPassword.setText(viewModel.passwordStateFlow.value)
    }

    private fun navigateToHomePage() {
        findNavController().navigate(R.id.action_logInFragment_to_bottomNavigationFragment)
    }

    private fun navigateToSignUpPage() {
        findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
    }


    // Check already logged in with Google Auth or not
    override fun onStart() {
        super.onStart()
        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        googleAccount?.let {
            // TODO 구글 계정으로 서버 로그인 성공 할 시
            navigateToHomePage()
        }
    }


    private fun logInWithEmail() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {

            Log.i("LogInFragment", "email: ${viewModel.emailStateFlow.value}, password: ${viewModel.passwordStateFlow.value}")

            val request = LogInEmailRequest(
                email = viewModel.emailStateFlow.value,
                password = viewModel.passwordStateFlow.value
            )
            val result = viewModel.logInWithEmail(request)

            if (result is Resource.Success) {
                navigateToHomePage()
                return@repeatOnLifecycle
            }
            if (result is Resource.Error) {
                Log.i("LogInFragment", "로그인 에러: ${result.throwable.message}")
            }
            Toast.makeText(requireContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }



    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleLogInResult(task)
        }
    }

    private fun launchGoogleLogIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
            .requestServerAuthCode(BuildConfig.GOOGLE_AUTH_CLIENT_ID, true)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(intent)
    }

    private fun handleGoogleLogInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val displayName = account?.displayName.toString()
            val idToken = account?.idToken.toString()
            val authCode = account.serverAuthCode

            // TODO 클로넥트 서버에 이 계정이 이미 회원가입 되어있나 체크 후, 안 되어있으면 아래 코드 실행

            Log.i("LogInFragment", "email: ${email}, idToken: ${idToken}, authCode: $authCode")
            authCode?.let {
                signUpUsingClonectServerWithGoogle(it, email, displayName)
            }
        } catch (e: ApiException) {
            Log.e("LogInFragment", "Fail to Google Sign In: ${e.message}")
        }
    }

    private fun signUpUsingClonectServerWithGoogle(authCode: String, email: String, displayName: String) = this.lifecycleScope.launch {
        val result = viewModel.fetchGoogleAuthInfo(authCode)
        if (result is Resource.Success<GoogleTokens>) {
            // TODO send accessToken to backend server

            val profileFile = withContext(Dispatchers.IO) {
                File(requireContext().cacheDir, "default_profile_female.png").apply {
                    createNewFile()
                    outputStream().use {
                        requireContext().assets.open("default_profile_female.png").copyTo(it)
                    }
                }
            }

            val request = SignUpEmailRequest(
                email = email,
                password = "",
                name = displayName,
                nickname = displayName,
                age = "0",
                phone = "0",
//                accessToken = result.data.access_token,
//                refreshToken = result.data.refresh_token,
                profile = profileFile
            )

            val response = viewModel.signUp(request)
            if (response is Resource.Success) {
                val data = response.data
                
                // TODO 이 서버에서 받은 데이터 가지고 뭘 어떻게 할건지 정해야됨

                navigateToHomePage()
                return@launch
            }
        }

        Toast.makeText(requireContext(), "구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
    }

}