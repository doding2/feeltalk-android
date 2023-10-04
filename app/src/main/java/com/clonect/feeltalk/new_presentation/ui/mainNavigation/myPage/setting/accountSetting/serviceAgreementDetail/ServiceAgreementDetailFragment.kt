package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.accountSetting.serviceAgreementDetail

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
import com.clonect.feeltalk.databinding.FragmentServiceAgreementDetailBinding
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/09/20.
 */
@AndroidEntryPoint
class ServiceAgreementDetailFragment : Fragment() {

    private lateinit var binding: FragmentServiceAgreementDetailBinding
    private val viewModel: ServiceAgreementDetailViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
//    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentServiceAgreementDetailBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
//        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }
        }
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
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}