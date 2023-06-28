package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.databinding.FragmentChatBinding
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setKeyboardInsets()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNavViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setKeyboardInsets() {
        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            if (imeHeight == 0) {
                binding.root.setPadding(0, 0, 0, getNavigationBarHeight())
            } else {
                binding.root.setPadding(0, 0, 0, imeHeight)
            }
            insets
        }
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
            requireActivity().onBackPressedDispatcher.addCallback(this.viewLifecycleOwner, onBackCallback)
        } else if (::onBackCallback.isInitialized) {
            onBackCallback.remove()
        }
    }

    private fun collectNavViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                navViewModel.showChatNavigation.collectLatest {
//                    if (it) {
//                        chatUpper.addListener()
//                    } else {
//                        chatUpper.removeListener()
//                    }
                    setBackCallback(it)
                }
            }
        }
    }
}