package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.accountSetting.deleteAccountDetail

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentDeleteAccountDetailBinding
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/09/23.
 */
@AndroidEntryPoint
class DeleteAccountDetailFragment : Fragment() {

    private lateinit var binding: FragmentDeleteAccountDetailBinding
    private val viewModel: DeleteAccountDetailViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDeleteAccountDetailBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
        setKeyboardInsets()

        binding.run {
            etEtcReason.addTextChangedListener { viewModel.setEtcReason(it?.toString() ?: "") }
            etEtcReason.setOnFocusChangeListener { view, isFocused -> if (isFocused) viewModel.setEtcReasonFocused(true) }
            etDeleteReason.addTextChangedListener { viewModel.setDeleteReason(it?.toString() ?: "") }
            etDeleteReason.setOnFocusChangeListener { view, isFocused -> if (isFocused) viewModel.setDeleteReasonFocused(true) }

            mcvReason1.setOnClickListener { viewModel.setDeleteReasonType(DeleteReasonType.BreakUp) }
            mcvReason2.setOnClickListener { viewModel.setDeleteReasonType(DeleteReasonType.NoFunctionality) }
            mcvReason3.setOnClickListener { viewModel.setDeleteReasonType(DeleteReasonType.BugOrError) }
            mcvReason4.setOnClickListener { viewModel.setDeleteReasonType(DeleteReasonType.Etc) }

            mcvConfirm.setOnClickListener { showDeleteAccountConfirmDialog() }
        }
    }

    private fun showDeleteAccountConfirmDialog() {
        showConfirmDialog(
            title = requireContext().getString(R.string.delete_account_detail_dialog_title),
            body = requireContext().getString(R.string.delete_account_detail_dialog_body),
            cancelButton = requireContext().getString(R.string.delete_account_detail_dialog_cancel),
            confirmButton = requireContext().getString(R.string.delete_account_detail_dialog_confirm),
            onConfirm = {
                deleteAccount()
            }
        )
    }

    private fun deleteAccount() {
        viewModel.deleteAccount {
            navigateToDeleteAccountDone()
        }
    }

    private fun navigateToDeleteAccountDone() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_deleteAccountDetailFragment_to_deleteAccountDoneFragment)
    }


    private fun setKeyboardInsets() = binding.run {
        root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            } else {
                insets.systemWindowInsetBottom
            }

            viewModel.setKeyboardUp(imeHeight != 0)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                return@setOnApplyWindowInsetsListener insets
            }
            if (imeHeight == 0) {
                root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
            } else {
                root.setPadding(0, getStatusBarHeight(), 0, imeHeight)
                svScroll.smoothScrollBy(0, getNavigationBarHeight())
            }

            insets
        }
    }



    private fun changeKeyboardUpView(isUp: Boolean) = binding.run {
        if (isUp) {
            llConfirm.visibility = View.GONE
        } else {
            llConfirm.visibility = View.VISIBLE
            etEtcReason.clearFocus()
            etDeleteReason.clearFocus()
            viewModel.setEtcReasonFocused(false)
            viewModel.setDeleteReasonFocused(false)
        }
    }

    private fun changeDeleteReasonView(deleteReasonType: DeleteReasonType?) = binding.run {
        ivReason1Check.setImageResource(R.drawable.n_ic_round_disagree)
        ivReason2Check.setImageResource(R.drawable.n_ic_round_disagree)
        ivReason3Check.setImageResource(R.drawable.n_ic_round_disagree)
        ivReason4Check.setImageResource(R.drawable.n_ic_round_disagree)
        mcvEtcReason.visibility = View.GONE

        when (deleteReasonType) {
            DeleteReasonType.BreakUp ->
                ivReason1Check.setImageResource(R.drawable.n_ic_round_agree)
            DeleteReasonType.NoFunctionality ->
                ivReason2Check.setImageResource(R.drawable.n_ic_round_agree)
            DeleteReasonType.BugOrError ->
                ivReason3Check.setImageResource(R.drawable.n_ic_round_agree)
            DeleteReasonType.Etc -> {
                ivReason4Check.setImageResource(R.drawable.n_ic_round_agree)
                mcvEtcReason.visibility = View.VISIBLE
            }
            null -> return@run
        }
    }

    private fun changeEtcReasonFocusedView(isFocused: Boolean) = binding.run {
        if (isFocused) {
            mcvEtcReason.strokeWidth = requireContext().dpToPx(2f).toInt()
            mcvEtcReason.setCardBackgroundColor(Color.WHITE)
        } else {
            mcvEtcReason.strokeWidth = 0
            mcvEtcReason.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
        }
    }

    private fun changeDeleteReasonFocusedView(isFocused: Boolean) = binding.run {
        if (isFocused) {
            mcvDeleteReason.strokeWidth = requireContext().dpToPx(2f).toInt()
            mcvDeleteReason.setCardBackgroundColor(Color.WHITE)
        } else {
            mcvDeleteReason.strokeWidth = 0
            mcvDeleteReason.setCardBackgroundColor(requireContext().getColor(R.color.gray_200))
        }
    }

    private fun changeConfirmEnabledView(enabled: Boolean) = binding.run {
        mcvConfirm.isEnabled = enabled
        if (enabled) {
            mcvConfirm.strokeWidth = requireContext().dpToPx(1f).toInt()
            mcvConfirm.setCardBackgroundColor(Color.WHITE)
            tvConfirm.setTextColor(Color.BLACK)
        } else {
            mcvConfirm.strokeWidth = 0
            mcvConfirm.setCardBackgroundColor(requireContext().getColor(R.color.gray_400))
            tvConfirm.setTextColor(Color.WHITE)
        }
    }

    private fun changeEtcReasonNumView(reason: String) = binding.run {
        tvNumEtcReason.text = reason.length.toString()
    }

    private fun changeDeleteReasonNumView(reason: String) = binding.run {
        tvNumDeleteReason.text = reason.length.toString()
    }



    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun showSnackBar(message: String) {
        val decorView = activity?.window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = message,
            duration = Snackbar.LENGTH_SHORT,
            onClick = {
                it.dismiss()
            }
        ).show()
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch { viewModel.isKeyboardUp.collectLatest(::changeKeyboardUpView) }
            launch { viewModel.deleteReasonType.collectLatest(::changeDeleteReasonView) }
            launch { viewModel.isEtcReasonFocused.collectLatest(::changeEtcReasonFocusedView) }
            launch { viewModel.isDeleteReasonFocused.collectLatest(::changeDeleteReasonFocusedView) }
            launch { viewModel.isConfirmEnabled.collectLatest(::changeConfirmEnabledView) }
            launch { viewModel.etcReason.collectLatest(::changeEtcReasonNumView) }
            launch { viewModel.deleteReason.collectLatest(::changeDeleteReasonNumView) }
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