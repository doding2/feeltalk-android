package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.partnerSetting.breakUpCouple

import android.app.Dialog
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
import com.clonect.feeltalk.databinding.FragmentBreakUpCoupleBinding
import com.clonect.feeltalk.new_domain.model.account.ServiceDataCountDto
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
 * Created by doding2 on 2023/09/21.
 */
@AndroidEntryPoint
class BreakUpCoupleFragment : Fragment() {

    private lateinit var binding: FragmentBreakUpCoupleBinding
    private val viewModel: BreakUpCoupleViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBreakUpCoupleBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }
            llAgreeAll.setOnClickListener { viewModel.toggleIsAllAgreed() }
            mcvConfirm.setOnClickListener { showBreakUpConfirmDialog() }
        }
    }

    private fun showBreakUpConfirmDialog() {
        showConfirmDialog(
            title = requireContext().getString(R.string.break_up_couple_dialog_title),
            body = requireContext().getString(R.string.break_up_couple_dialog_body),
            cancelButton = requireContext().getString(R.string.break_up_couple_dialog_cancel),
            confirmButton = requireContext().getString(R.string.break_up_couple_dialog_confirm),
            onConfirm = {
                breakUpCouple()
            }
        )
    }

    private fun breakUpCouple() {
        viewModel.breakUpCouple {
            navigateToSignUp()
        }
    }

    private fun navigateToSignUp() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_breakUpCoupleFragment_to_signUpFragment,)
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

    private fun changeServiceDataCount(dataCount: ServiceDataCountDto?) = binding.run {
        if (dataCount == null) {
            tvQuestionRecord.text = "0"
            tvChallengeRecord.text = "0"
            return@run
        }
        tvQuestionRecord.text = dataCount.questionTotalCount.toString()
        tvChallengeRecord.text = dataCount.challengeTotalCount.toString()
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
            launch { viewModel.isAllAgreed.collectLatest(::changeAgreeAllView) }
            launch { viewModel.serviceDataCount.collectLatest(::changeServiceDataCount) }
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