package com.clonect.feeltalk.new_presentation.ui.signUp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSignUpBinding
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.user.SocialType
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.presentation.utils.infoLog
import com.clonect.feeltalk.presentation.utils.makeLoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        collectViewModel()
        initCustomerServiceText()

        binding.apply {
            mcvSignUpKakao.setOnClickListener { clickKakaoButton() }
            mcvSignUpNaver.setOnClickListener { clickNaverButton() }
            mcvSignUpGoogle.setOnClickListener { clickGoogleButton() }
            mcvSignUpApple.setOnClickListener { clickAppleButton() }
        }
    }

    private fun navigateToAgreement() = runCatching {
        val bundle = bundleOf(
            "startPage" to "agreement"
        )
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_signUpFragment_to_signUpNavigationFragment, bundle)
    }.onFailure {
        it.printStackTrace()
        infoLog("navigate agreement error: ${it.localizedMessage}")
    }

    private fun navigateToCoupleCode() = runCatching {
        val bundle = bundleOf(
            "startPage" to "coupleCode"
        )
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_signUpFragment_to_signUpNavigationFragment, bundle)
    }.onFailure {
        it.printStackTrace()
        infoLog("navigate couple code error: ${it.localizedMessage}")
    }

    private fun navigateToMain() = runCatching {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_signUpFragment_to_mainNavigationFragment)
    }.onFailure {
        it.printStackTrace()
        infoLog("navigate main error: ${it.localizedMessage}")
    }


    private fun clickKakaoButton() = lifecycleScope.launch {
        try {
            val (accessToken, refreshToken) = KakaoAuthHelper.signIn(requireContext())
            val socialToken = SocialToken(
                type = SocialType.Kakao,
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            viewModel.checkMemberType(socialToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            viewModel.sendErrorMessage("카카오 로그인에 실패했습니다.")
            infoLog("카카오 로그인 실패: ${e.localizedMessage} \n ${e.stackTrace.joinToString(separator = "\n")}")
        }
    }


    private fun clickNaverButton() = lifecycleScope.launch {
        try {
            val (accessToken, refreshToken) = NaverAuthHelper.signIn(requireContext())
            val socialToken = SocialToken(
                type = SocialType.Naver,
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            viewModel.checkMemberType(socialToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            viewModel.sendErrorMessage("네이버 로그인에 실패했습니다.")
            infoLog("네이버 로그인 실패: ${e.stackTrace.joinToString(separator = "\n")}")
        }
    }

    private fun clickAppleButton() = lifecycleScope.launch {
        try {
            val state = AppleAuthHelper.signIn(requireContext())
            val socialToken = SocialToken(
                type = SocialType.Apple,
                state = state
            )

            viewModel.checkMemberType(socialToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            viewModel.sendErrorMessage("애플 로그인에 실패했습니다.")
            infoLog("애플 로그인 실패: ${e.localizedMessage} \n ${e.stackTrace.joinToString(separator = "\n")}")
        }
    }

    private fun clickGoogleButton() = lifecycleScope.launch {
        GoogleAuthHelper.signIn(requireContext(), googleSignUpLauncher)
    }

    private val googleSignUpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val (idToken, serverAuthCode) = GoogleAuthHelper.handleSignInData(task)
            proceedGoogleSignIn(idToken, serverAuthCode)
        } else {
            viewModel.sendErrorMessage("구글 로그인에 실패했습니다.")
            infoLog("구글 로그인 실패")
        }
    }

    private fun proceedGoogleSignIn(idToken: String, serverAuthCode: String) = lifecycleScope.launch {
        try {
            val socialToken = SocialToken(
                type = SocialType.Google,
                idToken = idToken,
                serverAuthCode = serverAuthCode
            )

            viewModel.checkMemberType(socialToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            viewModel.sendErrorMessage("구글 로그인에 실패했습니다.")
            infoLog("구글 로그인 실패: ${e.localizedMessage} \n ${e.stackTrace.joinToString(separator = "\n")}")
        }
    }



    private fun sendFeedbackEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.FEEDBACK_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, "[필로우톡] 이런 점이 아쉬워요 !")
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun initCustomerServiceText() = binding.tvCustomerService.apply {
        paintFlags = Paint.UNDERLINE_TEXT_FLAG
        setOnClickListener {
            sendFeedbackEmail()
        }
    }


    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun showSnackBar(message: String) {
        val decorView = activity?.window?.decorView ?: return
        Snackbar.make(
            decorView,
            message,
            Snackbar.LENGTH_SHORT
        ).also {
            val view = it.view
            view.setOnClickListener { _ -> it.dismiss() }
            val layoutParams = view.layoutParams as FrameLayout.LayoutParams
            layoutParams.bottomMargin = getNavigationBarHeight()
            view.layoutParams = layoutParams
            it.show()
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isLoading.collectLatest(::setLoading) }
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch {
                viewModel.navigateToAgreement.collectLatest {
                    if (it) navigateToAgreement()
                }
            }
            launch {
                viewModel.navigateToCoupleCode.collectLatest {
                    if (it) navigateToCoupleCode()
                }
            }
            launch {
                viewModel.navigateToMain.collectLatest {
                    if (it) navigateToMain()
                }
            }
        }
    }

}