package com.clonect.feeltalk.presentation.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.databinding.FragmentSignUpBinding
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.btnSignUp.setOnClickListener {
            signUp()
        }

        return binding.root
    }

    private fun navigateToHomePage() {
        findNavController().navigate(R.id.action_signUpFragment_to_bottomNavigationFragment)
    }

    private fun signUp() = lifecycleScope.launch {

        val profileFile = withContext(Dispatchers.IO) {
            File(requireContext().cacheDir, "default_profile_female.png").apply {
                createNewFile()
                outputStream().use {
                    requireContext().assets.open("default_profile_female.png").copyTo(it)
                }
            }
        }

        val request = binding.run {
            SignUpEmailRequest(
                email = etEmail.text.toString(),
                password = etPassword.text.toString(),
                name = etName.text.toString(),
                nickname = etNickname.text.toString(),
                age = etAge.text.toString(),
                phone = etPhoneNum.text.toString(),
                profile = profileFile
            )
        }

        val userInfo = viewModel.signUp(request)
        if (userInfo is Resource.Success) {
            Log.i("SignUpFragment", "sign up server response: ${userInfo.data}")
            navigateToHomePage()
            return@launch
        }

        if (userInfo is Resource.Error) {
            val exception = userInfo.throwable
            Log.i("SignUpFragment", "Fail to sign up with email: ${exception.message}")
        }
        Log.i("SignUpFragment", "Fail to sign up with email: $userInfo")
        Toast.makeText(requireContext(), "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show()
    }

}