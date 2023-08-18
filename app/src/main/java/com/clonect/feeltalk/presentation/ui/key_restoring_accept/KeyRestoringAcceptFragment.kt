package com.clonect.feeltalk.presentation.ui.key_restoring_accept

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentKeyRestoringAcceptBinding
import com.clonect.feeltalk.domain.model.data.user.Emotion
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KeyRestoringAcceptFragment : Fragment() {

    private lateinit var binding: FragmentKeyRestoringAcceptBinding
    private val viewModel: KeyRestoringAcceptViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentKeyRestoringAcceptBinding.inflate(inflater, container, false)
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectState()

        binding.btnAccept.setOnClickListener {
            viewModel.acceptRestoreKeys()
        }
        binding.btnBack.setOnClickListener {
            onBackCallback.handleOnBackPressed()
        }
    }


    private fun collectState() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectPartnerState()
            collectKeyPairsStateMessage()
            collectRequestButtonEnabled()
            collectIsLoading()
            collectToast()
        }
    }

    private fun CoroutineScope.collectPartnerState() = launch {
        viewModel.partnerState.collectLatest {
            binding.tvPartnerState.text = it
        }
    }

    private fun CoroutineScope.collectKeyPairsStateMessage() = launch {
        viewModel.keyPairsStateMessage.collectLatest {
            binding.ivStateIcon.setState(it.state)
            binding.tvStateMessage.text = it.message
        }
    }

    private fun CoroutineScope.collectRequestButtonEnabled() = launch {
        viewModel.acceptButtonEnabled.collectLatest { enabled ->
            binding.btnAccept.apply {
                isEnabled = enabled
                setCardBackgroundColor(
                    if (enabled) requireContext().getColor(R.color.guide_indicator_enabled_button_color)
                    else requireContext().getColor(R.color.guide_indicator_disabled_button_color)
                )
            }
        }
    }

    private fun CoroutineScope.collectIsLoading() = launch {
        viewModel.isLoading.collectLatest {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
    }

    private fun CoroutineScope.collectToast() = launch {
        viewModel.toast.collect {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }



    private fun ImageView.setState(emotion: Emotion) {
        val emotionId = when (emotion) {
            is Emotion.Happy -> R.drawable.ic_emotion_happy
            is Emotion.Puzzling -> R.drawable.ic_emotion_puzzling
            is Emotion.Bad -> R.drawable.ic_emotion_bad
            is Emotion.Angry -> R.drawable.ic_emotion_angry
        }
        setImageResource(emotionId)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
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