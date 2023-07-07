package com.clonect.feeltalk.new_presentation.ui.signUpNavigation

import android.app.Dialog
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
import com.clonect.feeltalk.presentation.utils.makeLoadingDialog
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
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpNavigationBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fcv_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val startPage = arguments?.getString("startPage", "agreement")
        val startDestination = if (startPage == "coupleCode") {
            R.id.coupleCodeFragment
        } else {
            R.id.agreementFragment
        }
        val navGraph = navController.navInflater.inflate(R.navigation.sign_up_nav_graph)
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph

        viewModel.clear()
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }
        }
    }


    private fun navigateToNickname() {
        val navigateFragmentId = R.id.nicknameFragment
        if (navController.currentDestination?.id == navigateFragmentId) return
        navController.navigate(R.id.action_agreementFragment_to_nicknameFragment)
    }

    private fun navigateToCoupleCode() {
        val navigateFragmentId = R.id.coupleCodeFragment
        if (navController.currentDestination?.id == navigateFragmentId) return
        navController.navigate(R.id.action_nicknameFragment_to_coupleCodeFragment)
    }

    private fun navigateToMain() = runCatching {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_signUpNavigationFragment_to_mainNavigationFragment)
    }.onFailure {
        it.printStackTrace()
    }


    private fun showCoupleConnectedSnackBar() {
        val decorView = activity?.window?.decorView ?: return
        Snackbar.make(
            decorView,
            requireContext().getString(R.string.sign_up_succeed),
            Snackbar.LENGTH_SHORT
        ).also {
            val view = it.view
            view.setOnClickListener { _ -> it.dismiss() }
            val layoutParams = view.layoutParams as FrameLayout.LayoutParams
            layoutParams.bottomMargin = getNavigationBarHeight() + activity.dpToPx(56f).toInt()
            view.layoutParams = layoutParams
            it.show()
        }
    }

    private fun showErrorSnackBar(message: String) {
        val decorView = activity?.window?.decorView ?: return
        Snackbar.make(
            decorView,
            message,
            Snackbar.LENGTH_LONG
        ).also {
            val view = it.view
            view.setOnClickListener { _ -> it.dismiss() }
            val layoutParams = view.layoutParams as FrameLayout.LayoutParams
            layoutParams.bottomMargin = getNavigationBarHeight()
            view.layoutParams = layoutParams
            it.show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun changeActionBar(currentPage: String) = binding.run {
        when (currentPage) {
            "agreement" -> {
                ivExit.visibility = View.VISIBLE
                ivBack.visibility = View.GONE
                lpiProcess.visibility = View.VISIBLE
                tvTitle.setText(R.string.sign_up_title_sign_up)
            }
            "nickname" -> {
                ivExit.visibility = View.GONE
                ivBack.visibility = View.VISIBLE
                lpiProcess.visibility = View.VISIBLE
                tvTitle.setText(R.string.sign_up_title_sign_up)
            }
            "coupleCode" -> {
                ivExit.visibility = View.GONE
                ivBack.visibility = View.GONE
                lpiProcess.visibility = View.GONE
                tvTitle.setText(R.string.sign_up_title_couple_code)
            }
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.errorMessage.collectLatest(::showErrorSnackBar) }
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.currentPage.collectLatest(::changeActionBar) }
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
                        showCoupleConnectedSnackBar()
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