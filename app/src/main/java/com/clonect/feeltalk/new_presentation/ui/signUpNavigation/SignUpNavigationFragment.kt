package com.clonect.feeltalk.new_presentation.ui.signUpNavigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
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
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpNavigationFragment : Fragment() {

    private lateinit var binding: FragmentSignUpNavigationBinding
    private val viewModel: SignUpNavigationViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpNavigationBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fcv_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val startPage = requireArguments().getString("startPage", "agreement")
        if (startPage == "coupleCode") {
            navigateToCoupleCode()
            navController.backQueue.clear()
        }
        viewModel.clear()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
        }
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


    private fun showSnackBar() {
        val decorView = activity?.window?.decorView ?: return
        Snackbar.make(
            decorView,
            requireContext().getString(R.string.sign_up_succeed),
            Snackbar.LENGTH_SHORT
        ).also {
            val view = it.view
            view.setOnClickListener { _ -> it.dismiss() }
            val layoutParams = view.layoutParams as FrameLayout.LayoutParams
            layoutParams.bottomMargin = getNavigationBarHeight() + requireActivity().dpToPx(56f).toInt()
            view.layoutParams = layoutParams
            it.show()
        }

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
                        showSnackBar()
                        navigateToMain()
                        viewModel.clear()
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.popBackStack()) {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}