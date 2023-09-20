package com.clonect.feeltalk.new_presentation.ui.mainNavigation.home.signal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentSignalBottomSheetBinding
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignalBottomSheetFragment(
    val onSendSignal: (Signal) -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "SignalBottomSheetFragment"
    }

    private lateinit var binding: FragmentSignalBottomSheetBinding
    private val viewModel: SignalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignalBottomSheetBinding.inflate(inflater, container, false)
        val behavior = (dialog as? BottomSheetDialog)?.behavior
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        behavior?.skipCollapsed = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCurrentSignal()
        collectViewModel()

        binding.run {
            layoutSeduce.root.setOnClickListener { selectSignal(Signal.Seduce) }
            layoutPassion.root.setOnClickListener { selectSignal(Signal.Passion) }
            layoutSkinship.root.setOnClickListener { selectSignal(Signal.Skinship) }
            layoutPuzzling.root.setOnClickListener { selectSignal(Signal.Puzzling) }
            layoutNope.root.setOnClickListener { selectSignal(Signal.Nope) }
            layoutTired.root.setOnClickListener { selectSignal(Signal.Tired) }

            mcvSendSignal.setOnClickListener { sendSignal() }
        }
    }

    private fun initCurrentSignal() {
        arguments?.getString("currentSignal")?.let {
            val currentSignal = Signal.valueOf(it)
            viewModel.setCurrentSignal(currentSignal)
            binding.ivSelectedSignal.setBackgroundColor(currentSignal.getColorResource())
        }
    }

    private fun selectSignal(signal: Signal) {
        viewModel.setSelectedSignal(signal)
    }

    // TODO 나중에 서버로 시그널 결과 보내게 수정
    private fun sendSignal() {
        val selectedSignal = viewModel.selectedSignal.value ?: return
        viewModel.setCurrentSignal(selectedSignal)
        onSendSignal(selectedSignal)
        dismiss()
    }


    // TODO 나중에 이미지로 바꾸기
    private fun changeSelectedSignalView(selectedSignal: Signal?) = binding.run {
        if (selectedSignal == null) return@run
        ivSelectedSignal.setBackgroundColor(selectedSignal.getColorResource())
    }

    // TODO 나중에 이미지로 바꾸기
    private fun Signal.getColorResource(): Int = when (this) {
        Signal.Seduce -> ContextCompat.getColor(requireContext(), R.color.signal_seduce)
        Signal.Passion -> ContextCompat.getColor(requireContext(), R.color.signal_passion)
        Signal.Skinship -> ContextCompat.getColor(requireContext(), R.color.signal_skinship)
        Signal.Puzzling -> ContextCompat.getColor(requireContext(), R.color.signal_puzzling)
        Signal.Nope -> ContextCompat.getColor(requireContext(), R.color.signal_nope)
        Signal.Tired -> ContextCompat.getColor(requireContext(), R.color.signal_tired)
    }

    private fun enableSendButton(enabled: Boolean) = binding.mcvSendSignal.run {
        if (enabled) {
            setCardBackgroundColor(resources.getColor(R.color.main_500, null))
            isClickable = true
            isFocusable = true
            isEnabled = true
        } else {
            setCardBackgroundColor(resources.getColor(R.color.main_400, null))
            isClickable = false
            isFocusable = false
            isEnabled = false
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.selectedSignal.collectLatest {
                    changeSelectedSignalView(it)
                    enableSendButton(it != null)
                }
            }
        }
    }

}