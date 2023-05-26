package com.clonect.feeltalk.new_presentation.ui.sign_up_navigation.couple_code

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.databinding.FragmentCoupleCodeBinding
import com.clonect.feeltalk.new_presentation.ui.sign_up_navigation.SignUpNavigationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CoupleCodeFragment : Fragment() {

    private lateinit var binding: FragmentCoupleCodeBinding
    private val viewModel: SignUpNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
        viewModel.setSignUpProcess(80)

        binding.run {
            mcvShareCoupleCode.setOnClickListener { copyCoupleCode() }
            mcvShowSheet.setOnClickListener { showCoupleConnectSheet() }
        }
    }


    private fun showCoupleConnectSheet() {

    }

    private fun copyCoupleCode() {
        val text = viewModel.coupleCode.value
        if (text.isNullOrBlank()) return

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("커플코드", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.coupleCode.collectLatest {
                    binding.tvCoupleCode.text = it
                }
            }

            launch {
                viewModel.nickname.collectLatest {
                    binding.tvNickname.text = it
                }
            }
        }
    }
}