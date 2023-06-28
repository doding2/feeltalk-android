package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

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
import com.clonect.feeltalk.databinding.FragmentChatBinding
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var viewUpper: AndroidBug5497Workaround

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        viewUpper = AndroidBug5497Workaround(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNavViewModel()
    }

    private fun setBackCallback(isChatShown: Boolean) {
        if (isChatShown) {
            onBackCallback = object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (navViewModel.showChatNavigation.value) {
                        navViewModel.toggleShowChatNavigation()
                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
        } else if (::onBackCallback.isInitialized) {
            onBackCallback.remove()
        }
    }

    private fun collectNavViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                navViewModel.showChatNavigation.collectLatest {
                    setBackCallback(it)
                }
            }
        }
    }
}