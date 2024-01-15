package com.clonect.feeltalk.new_presentation.ui.signUpNavigation.couple_code

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.databinding.FragmentCoupleCodeBinding
import com.clonect.feeltalk.new_presentation.ui.signUpNavigation.SignUpNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.signUpNavigation.couple_connect.CoupleConnectBottomSheetFragment
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getScreenHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoupleCodeFragment : Fragment() {

    private lateinit var binding: FragmentCoupleCodeBinding
    private val viewModel: SignUpNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleCodeBinding.inflate(inflater, container, false)
        scaleCodeLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
        getCoupleCode()
        viewModel.registerService()
        viewModel.setCurrentPage("coupleCode")
        viewModel.setNicknameFocused(false)

        binding.run {
            mcvShareCoupleCode.setOnClickListener { copyCoupleCode() }
            mcvShowSheet.setOnClickListener { showCoupleConnectSheet() }
            llReload.setOnClickListener { getCoupleCode() }
        }
    }

    private fun getCoupleCode() = lifecycleScope.launch {
        viewModel.getCoupleCode()
    }

    private fun showCoupleConnectSheet() {
        val bottomSheet = CoupleConnectBottomSheetFragment(
            onKeyboardUp = { binding.clCodeLayout.visibility = View.GONE },
            onKeyboardDown = { binding.clCodeLayout.visibility = View.VISIBLE }
        )
        bottomSheet.show(requireActivity().supportFragmentManager, CoupleConnectBottomSheetFragment.TAG)
    }

    private fun copyCoupleCode() {
        val text = viewModel.coupleCode.value
        if (text.isNullOrBlank()) return

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("커플코드", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun scaleCodeLayout() = binding.run {
        val screenHeight = requireContext().getScreenHeight()
        val originalHeight = activity.dpToPx(812f)
        val ratio = screenHeight / originalHeight
        val reverseRatio = originalHeight / screenHeight
        clCodeLayout.apply {
            scaleX = ratio
            scaleY = ratio
        }

        tvCodeInvitation.apply {
            scaleX = reverseRatio
            scaleY = reverseRatio
        }
        tvCoupleCode.apply {
            scaleX = reverseRatio
            scaleY = reverseRatio
        }
        mcvShareCoupleCode.apply {
            scaleX = reverseRatio
            scaleY = reverseRatio
        }
        llReload.apply {
            scaleX = reverseRatio
            scaleY = reverseRatio
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.coupleCode.collectLatest(binding.tvCoupleCode::setText)
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
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