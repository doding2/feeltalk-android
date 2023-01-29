package com.clonect.feeltalk.presentation.ui.login

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentLogInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

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
                launchGoogleSignIn()
            }
            btnSkip.setOnClickListener {
                navigateToHomePage()
            }
        }

    }

    private fun navigateToHomePage() {
        findNavController().navigate(R.id.action_logInFragment_to_bottomNavigationFragment)
    }


    // Check already logged in with Google Auth or not
    override fun onStart() {
        super.onStart()
        val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
        googleAccount?.let {
            Log.i("LogInFragment", "idToken: ${it.idToken.toString()}")
            // TODO 구글 계정으로 서버 로그인 성공 할 시
            navigateToHomePage()
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


    private var googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
            .requestServerAuthCode(BuildConfig.GOOGLE_AUTH_CLIENT_ID, true)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val intent = mGoogleSignInClient.signInIntent
        googleSignInLauncher.launch(intent)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken.toString()

            Log.i("LogInFragment", "idToken: $idToken")
            viewModel.signInWithGoogle(idToken)
            navigateToHomePage()
        } catch (e: ApiException) {
            Log.e("LogInFragment", "Fail to Google Sign In: ${e.message}")
        }
    }

}