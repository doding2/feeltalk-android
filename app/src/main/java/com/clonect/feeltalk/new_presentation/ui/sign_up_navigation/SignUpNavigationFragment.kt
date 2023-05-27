package com.clonect.feeltalk.new_presentation.ui.sign_up_navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSignUpNavigationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpNavigationFragment : Fragment() {

    private lateinit var binding: FragmentSignUpNavigationBinding
    private val viewModel: SignUpNavigationViewModel by activityViewModels()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpNavigationBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fcv_fragment) as NavHostFragment
        navController = navHostFragment.navController
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { navigateBack() }
        }
    }


    private fun navigateBack() {
        navController.popBackStack()
    }

    private fun navigateToNickname() {
        val navigateFragmentId = R.id.nicknameFragment
        if (navController.currentDestination?.id == navigateFragmentId) return
        navController.navigate(navigateFragmentId)
    }

    private fun navigateToCoupleCode() {
        val navigateFragmentId = R.id.coupleCodeFragment
        if (navController.currentDestination?.id == navigateFragmentId) return
        navController.navigate(navigateFragmentId)
    }

    private fun navigateToMain() = runCatching {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_signUpNavigationFragment_to_mainNavigationFragment)
    }.onFailure {
        it.printStackTrace()
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.signUpProcess.collectLatest {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        binding.lpiProcess.setProgress(it, true)
                    } else {
                        binding.lpiProcess.progress = it
                    }
                }
            }

            launch {
                viewModel.isAgreementProcessed.collectLatest { processed ->
                    if (processed) navigateToNickname()
                }
            }

            launch {
                viewModel.isNicknameProcessed.collectLatest { processed ->
                    if (processed) navigateToCoupleCode()
                }
            }

            launch {
                viewModel.isCoupleConnected.collectLatest { connected ->
                    if (connected) {
//                        Snackbar.make(binding.root, "연인과 연결되었어요 !", Snackbar.LENGTH_LONG).show()
                        navigateToMain()
                    }
                }
            }
        }
    }
}