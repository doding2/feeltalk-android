package com.clonect.feeltalk.presentation.ui.signup

import android.app.Activity.RESULT_OK
import android.graphics.Paint
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
import com.clonect.feeltalk.databinding.FragmentSignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCustomerServiceText()

        binding.apply {
            mcvSignUpGoogle.setOnClickListener {
                launchGoogleSignUp()
            }
        }
    }

    private fun initCustomerServiceText() = binding.tvCustomerService.apply {
        paintFlags = Paint.UNDERLINE_TEXT_FLAG
        setOnClickListener { 
            // TODO 문의하기로 넘어가기
            // 지금은 임시로 스킵 역할을 하는중
            navigateToHomePage()
        }
    }

    private fun navigateToHomePage() {
        findNavController().navigate(R.id.action_signUpFragment_to_bottomNavigationFragment)
    }

    private fun navigateToCoupleRegistrationPage() {
        findNavController().navigate(R.id.action_signUpFragment_to_coupleRegistrationFragment)
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



    private fun googleLogOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInClient.signOut().addOnSuccessListener {
            Log.i("LogInFragment", "구글 로그 아웃")
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

    private var googleSignUpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignUpResult(task)
        }
    }

    private fun handleGoogleSignUpResult(completedTask: Task<GoogleSignInAccount>) = lifecycleScope.launch {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken.toString()
            val serverAuthCode = account?.serverAuthCode.toString()

            val isSignUpSuccessful = viewModel.signUpWithGoogle(idToken, serverAuthCode)
            if (isSignUpSuccessful) {
                navigateToCoupleRegistrationPage()
            } else {
                googleLogOut()
                Toast.makeText(requireContext(), "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            googleLogOut()
            Toast.makeText(requireContext(), "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }


}