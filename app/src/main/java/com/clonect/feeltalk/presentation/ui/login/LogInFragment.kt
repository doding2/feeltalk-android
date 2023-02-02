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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.databinding.FragmentLogInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnGoogleLogIn.setOnClickListener {
                launchGoogleSignUp()
            }
            btnSkip.setOnClickListener {
                navigateToHomePage()
            }
        }

        initFcm()

    }

    private fun initFcm() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { }
    }

    private fun navigateToHomePage() {
        findNavController().navigate(R.id.action_logInFragment_to_bottomNavigationFragment)
    }

    private fun navigateToCoupleRegistrationPage() {
        findNavController().navigate(R.id.action_logInFragment_to_coupleRegistrationFragment)
    }

    // Check already logged in with Google Auth or not
    override fun onStart() {
        super.onStart()
//        googleLogOut()
        googleAutoLogIn()
    }

    private fun googleLogOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInClient.signOut().addOnSuccessListener {
            Log.i("LogInFragment", "구글 로그 아웃")
        }
    }

    private fun googleAutoLogIn() = lifecycleScope.launch {
        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        googleAccount?.let {
            val idToken = it.idToken.toString()
            val isLogInSuccessful = viewModel.autoLogInWithGoogle(idToken)
            if (!isLogInSuccessful) {
                Toast.makeText(requireContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val isUserCouple = viewModel.checkUserIsCouple()
            if (isUserCouple) {
                navigateToHomePage()
                return@launch
            }

            navigateToCoupleRegistrationPage()
        }
    }


    private fun signInWithKakao() {
        UserApiClient.instance.run {
            if (!isKakaoTalkLoginAvailable(requireContext())) return

            loginWithKakaoTalk(requireContext()) { token, error ->
                error?.let {
                    Log.i("LogInFragment", "카카오 로그인 실패: ${error}")
                    return@loginWithKakaoTalk
                }

                token?.let {
                    Log.i("LogInFragment", "카카오 로그인 성공: ${token.accessToken}")
                    return@loginWithKakaoTalk
                }
            }
        }
    }


    private var googleSignUpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignUpResult(task)
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

    private fun handleGoogleSignUpResult(completedTask: Task<GoogleSignInAccount>) = lifecycleScope.launch {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken.toString()
            val serverAuthCode = account?.serverAuthCode.toString()
            Log.i("LogInFragment", "아이디 토큰: $idToken")
            Log.i("LogInFragment", "서버오스코드: $serverAuthCode")

            val isSignUpSuccessful = viewModel.signUpWithGoogle(idToken, serverAuthCode)
            if (isSignUpSuccessful) {
                navigateToCoupleRegistrationPage()
            } else {
                Toast.makeText(requireContext(), "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e("LogInFragment", "Fail to Google Sign In: ${e.message}")
        }
    }


}