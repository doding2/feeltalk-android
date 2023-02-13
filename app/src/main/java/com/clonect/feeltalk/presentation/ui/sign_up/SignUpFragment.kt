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
            mcvSignUpGoogle.setOnClickListener {
                launchGoogleSignUp()
            }
            mcvSignUpKakao.setOnClickListener {
                signInWithKakao()
            }
        }
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



    private fun signInWithKakao() {
        UserApiClient.instance.run {
            if (!isKakaoTalkLoginAvailable(requireContext())) return

            loginWithKakaoTalk(requireContext()) { token, error ->
                error?.let {
                    infoLog("카카오 로그인 실패: ${error}")
                    Toast.makeText(requireContext(), "카카오로 가입하는데 실패했습니다", Toast.LENGTH_SHORT).show()
                    return@loginWithKakaoTalk
                }

                token?.let {
                    infoLog( "카카오 로그인 성공\n -> idToken: ${token.idToken}\n accessToken: ${token.accessToken}")
                    return@loginWithKakaoTalk
                }
            }
        }
    }

    private suspend fun kakaoLogOut(): Boolean = suspendCoroutine {
        UserApiClient.instance.unlink { error ->
            if (error == null) {
                it.resume(true)
            } else {
                it.resume(false)
            }
        }
    }


    private fun signInWithNaver() {

    }



    private fun googleLogOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInClient.signOut().addOnSuccessListener {
            infoLog("구글 로그 아웃")
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

            viewModel.signUpWithGoogle(idToken, serverAuthCode)
        } catch (e: ApiException) {
            googleLogOut()
            Toast.makeText(requireContext(), "구글로 가입하는데 실패했습니다", Toast.LENGTH_SHORT).show()
        }
    }



}