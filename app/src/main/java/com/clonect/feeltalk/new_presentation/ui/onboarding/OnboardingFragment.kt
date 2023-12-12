package com.clonect.feeltalk.new_presentation.ui.onboarding

import com.clonect.feeltalk.R
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.viewpager2.widget.ViewPager2
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.FragmentOnboardingBinding
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by doding2 on 2023/12/12.
 */
@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
//    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
//        loadingDialog = makeLoadingDialog()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        setRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            mcvDone.setOnClickListener { navigateToSignUp() }
            tvInquire.setOnClickListener { sendFeedbackEmail() }
        }
    }

    private fun navigateToSignUp() {
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_onboardingFragment_to_signUpFragment)
    }

    private fun sendFeedbackEmail() {
        try {
            sendEmailWithGmail()
        } catch (e: Exception) {
            sendEmailWithOtherApp()
        }
    }

    private fun sendEmailWithGmail() {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "plain/text"
            setPackage("com.google.android.gm")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.PILLOWTALK_FEEDBACK))
            putExtra(Intent.EXTRA_SUBJECT, requireContext().getString(R.string.pillowtalk_feedback_inquire))
        }
        if (emailIntent.resolveActivity(requireActivity().packageManager) != null)
            startActivity(emailIntent)
        startActivity(emailIntent)
    }

    private fun sendEmailWithOtherApp() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.PILLOWTALK_FEEDBACK))
            putExtra(Intent.EXTRA_SUBJECT, requireContext().getString(R.string.pillowtalk_feedback_inquire))
        }
        startActivity(Intent.createChooser(intent, null))
    }


    private fun setRecyclerView() = binding.run {
        val positions = (0..2).toList()
        setOnboardingText(0)
        vp2Onboarding.adapter = OnboardingAdapter(positions)
        vp2Onboarding.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setOnboardingText(position)
            }
        })
        diIndicator.attachTo(vp2Onboarding)
    }

    private fun setOnboardingText(position: Int) = binding.run {
        when (position) {
            0 -> {
                tvTitle.setText(R.string.onboarding_title_1)
                tvBody.setText(R.string.onboarding_body_1)
            }
            1 -> {
                tvTitle.setText(R.string.onboarding_title_2)
                tvBody.setText(R.string.onboarding_body_2)
            }
            2 -> {
                tvTitle.setText(R.string.onboarding_title_3)
                tvBody.setText(R.string.onboarding_body_3)
            }
            else -> {
                tvTitle.setText(R.string.onboarding_title_1)
                tvBody.setText(R.string.onboarding_body_1)
            }
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