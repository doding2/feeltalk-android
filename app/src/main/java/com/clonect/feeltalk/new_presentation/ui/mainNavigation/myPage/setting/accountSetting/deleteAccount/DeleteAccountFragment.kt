package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.accountSetting.deleteAccount

import android.content.Context
import android.graphics.Color
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
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentDeleteAccountBinding
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/09/23.
 */
@AndroidEntryPoint
class DeleteAccountFragment : Fragment() {

    private lateinit var binding: FragmentDeleteAccountBinding
    private val viewModel: DeleteAccountViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
//    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
//        loadingDialog = makeLoadingDialog()
        viewModel.navigatePage()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }
            llAgreeAll.setOnClickListener { viewModel.toggleIsAllAgreed() }
            mcvConfirm.setOnClickListener { navigateToDeleteAccountDetail() }
        }
    }


    private fun navigateToDeleteAccountDetail() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_deleteAccountFragment_to_deleteAccountDetailFragment)
    }


    private fun changeAgreeAllView(isAllAgreed: Boolean) = binding.run {
        mcvConfirm.isEnabled = isAllAgreed
        if (isAllAgreed) {
            ivAgreeAllCheck.setImageResource(R.drawable.n_ic_round_agree)
            mcvConfirm.strokeWidth = requireContext().dpToPx(1f)
            tvConfirm.setBackgroundResource(R.drawable.n_background_button_outline)
            tvConfirm.setTextColor(Color.BLACK)
        } else {
            ivAgreeAllCheck.setImageResource(R.drawable.n_ic_round_disagree)
            mcvConfirm.strokeWidth = 0
            tvConfirm.setBackgroundColor(requireContext().getColor(R.color.gray_400))
            tvConfirm.setTextColor(Color.WHITE)
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
            launch { viewModel.isAllAgreed.collectLatest(::changeAgreeAllView) }
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