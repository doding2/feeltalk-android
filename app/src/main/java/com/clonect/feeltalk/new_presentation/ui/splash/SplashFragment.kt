package com.clonect.feeltalk.new_presentation.ui.splash

import com.clonect.feeltalk.R
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.databinding.FragmentSplashBinding
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2024/01/08.
 */
@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
//    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
//        loadingDialog = makeLoadingDialog()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        tryAutoLogIn()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {

        }
    }

    private fun tryAutoLogIn() = lifecycleScope.launch {
//        viewModel.autoLogIn()
        viewModel.getUserStatus()
        viewModel.setReady()
    }


    private fun setNextNavigation(isReady: Boolean) {
        if (!isReady) return

        val nextDestination = viewModel.run {
            if (isNetworkErrorOccurred.value)
                R.id.action_splashFragment_to_networkErrorFragment
            else if (isServerDown.value)
                R.id.action_splashFragment_to_serverDownFragment
            else if (!isLoggedIn.value)
                R.id.action_splashFragment_to_onboardingFragment
            else if (!isUser.value)
                R.id.action_splashFragment_to_onboardingFragment
            else if (!isUserCouple.value)
                R.id.action_splashFragment_to_signUpNavigationFragment
            else if (isAccountLocked.value)
                R.id.action_splashFragment_to_passwordFragment
            else
                R.id.action_splashFragment_to_mainNavigationFragment
        }

        requireParentFragment()
            .findNavController()
            .navigate(nextDestination)
    }


    private fun showLoading(isLoading: Boolean) {
//        if (isLoading) {
//            loadingDialog.show()
//        } else {
//            loadingDialog.dismiss()
//        }
    }

    private fun showSnackBar(message: String) {
//        val decorView = activity?.window?.decorView ?: return
//        TextSnackbar.make(
//            view = decorView,
//            message = message,
//            duration = Snackbar.LENGTH_SHORT,
//            onClick = {
//                it.dismiss()
//            }
//        ).show()
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch { viewModel.isReady.collectLatest(::setNextNavigation) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}