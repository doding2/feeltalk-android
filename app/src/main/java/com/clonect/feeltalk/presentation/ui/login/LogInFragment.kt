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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.R
import com.clonect.feeltalk.domain.model.user.LogInGoogleResponse
import com.clonect.feeltalk.data.util.Resource
import com.clonect.feeltalk.databinding.FragmentLogInBinding
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val viewModel: LogInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)

        binding.btnLogIn.setOnClickListener {
            navigateToHomePage()
        }

        binding.btnGoogleLogIn.setOnClickListener {
            launchGoogleLogIn()
        }

        binding.btnSignUp.setOnClickListener {
            goToSignUpPage()
        }

        return binding.root
    }

    private fun navigateToHomePage() {
        findNavController().navigate(R.id.action_logInFragment_to_bottomNavigationFragment)
    }

    private fun goToSignUpPage() {
        findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
    }


    // Check already logged in with Google Auth or not
    override fun onStart() {
        super.onStart()
        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        googleAccount?.let {
            navigateToHomePage()
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

            // TODO 클로넥트 서버에 이 계정이 이미 회원가입 되어있나 체크 후 안 되어있으면 아래 코드 실행

            Log.i("LogInFragment", "email: ${email}, idToken: ${idToken}, authCode: $authCode")
            authCode?.let {
                getAccessToken(it, email, displayName)
            }
        } catch (e: ApiException) {
            Log.e("LogInFragment", "Fail to Google Sign In: ${e.message}")
        }
    }

    private fun getAccessToken(authCode: String, email: String, displayName: String) = this.lifecycleScope.launch {
        val result = viewModel.fetchGoogleAuthInfo(authCode)
        if (result is Resource.Success<LogInGoogleResponse>) {
            // TODO send accessToken to backend server
            val request = SignUpEmailRequest(
                email = email,
                password = "",
                name = displayName,
                nickname = displayName,
                age = 0,
                phone = 0,
                accessToken = result.data.access_token,
                refreshToken = result.data.refresh_token,
                coupleid = 0
            )

            val response = viewModel.signUp(request)
            if (response is Resource.Success<SignUpEmailResponse>) {
                val data = response.data
                
                // TODO 이 서버에서 받은 데이터 가지고 뭘 어떻게 할건지 정해야됨

                navigateToHomePage()
                return@launch
            }
        }

        Toast.makeText(requireContext(), "구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
    }

}