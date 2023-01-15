package com.clonect.feeltalk.presentation.ui.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.data.util.Resource
import com.clonect.feeltalk.databinding.FragmentSignUpBinding
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private fun signUp() = this.lifecycleScope.launch {
        val request = binding.run {
            SignUpEmailRequest(
                email = etEmail.text.toString(),
                password = etPassword.text.toString(),
                name = etName.text.toString(),
                nickname = etNickname.text.toString(),
                age = etAge.text.toString().toIntOrNull() ?: 0,
                phone = etPhoneNum.text.toString().toIntOrNull() ?: 0
            )
        }

        val response = viewModel.signUp(request)
        if (response is Resource.Success<SignUpEmailResponse>) {
            // TODO 이메일 회원가입 성공
            Log.i("SignUpFragment", "sign up server response: ${response.data}")
            navigateToHomePage()
            return@launch
        }

        if (response is Resource.Error) {
            val exception = response.throwable
            Log.i("SignUpFragment", "Fail to sign up with email: ${exception.message}")
        }
        Log.i("SignUpFragment", "Fail to sign up with email: $response")
        Toast.makeText(requireContext(), "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show()
    }

}