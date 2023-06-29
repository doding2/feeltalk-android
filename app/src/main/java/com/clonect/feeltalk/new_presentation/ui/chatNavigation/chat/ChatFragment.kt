package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
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
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback
    @Inject
    lateinit var adapter: ChatAdapter

    private var scrollRemainHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        setKeyboardInsets()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNavViewModel()
        setRecyclerView()

        binding.run {
            etTextMessage.addTextChangedListener { viewModel.setTextChat(it?.toString() ?: "") }
            ivSendTextChat.setOnClickListener { sendTextChat() }
            ivExpansion.setOnClickListener { viewModel.toggleExpandChatMedia() }
            ivCancel.setOnClickListener { cancel() }
        }
    }


    private fun sendTextChat() {
        viewModel.sendTextChat(
            onComplete =  {
                binding.etTextMessage.setText("")
            }
        )
    }

    private fun cancel() {
        viewModel.setExpandChatMedia(false)

        binding.run {
            ivCancel.visibility = View.GONE
            ivExpansion.visibility = View.VISIBLE
        }
    }


    private fun setRecyclerView() = binding.run {
        rvChat.adapter = adapter.apply {
            setMyNickname("me")
            setPartnerNickname("partner")
        }
    }

    private fun setKeyboardInsets() {
        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

                if (imeHeight == 0) {
                    binding.root.setPadding(0, 0, 0, getNavigationBarHeight())
                } else {
                    binding.root.setPadding(0, 0, 0, imeHeight)
                }
            }
            insets
        }

        binding.rvChat.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom == oldBottom) return@addOnLayoutChangeListener

            val rangeMoved = oldBottom - bottom
            if (bottom < oldBottom) {
                binding.rvChat.scrollBy(0, rangeMoved)
                scrollRemainHeight = computeRemainScrollHeight()
                return@addOnLayoutChangeListener
            }

            if (scrollRemainHeight < -rangeMoved) {
                binding.rvChat.scrollBy(0, -scrollRemainHeight)
                return@addOnLayoutChangeListener
            }

            binding.rvChat.scrollBy(0, rangeMoved)
        }
    }

    private fun computeRemainScrollHeight(): Int {
        return binding.rvChat.run {
            computeVerticalScrollRange() - computeVerticalScrollOffset() - computeVerticalScrollExtent()
        }
    }

    private fun scrollToBottom() {
        scrollRemainHeight -= computeRemainScrollHeight()
        val position = adapter.itemCount - 1
        binding.rvChat.scrollToPosition(position)
    }



    private fun setBackCallback(isChatShown: Boolean) {
        if (isChatShown) {
            onBackCallback = object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.expandChat.value) {
                        viewModel.toggleExpandChatMedia()
                        return
                    }
                    if (navViewModel.showChatNavigation.value) {
                        navViewModel.toggleShowChatNavigation()
                        return
                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(this.viewLifecycleOwner, onBackCallback)
        } else if (::onBackCallback.isInitialized) {
            onBackCallback.remove()
        }
    }

    private fun prepareTextChat(message: String) = binding.run {
        val isTextChatMode = message.isNotEmpty()
        if (isTextChatMode) {
            ivActivateVoiceChat.visibility = View.GONE
            ivSendTextChat.visibility = View.VISIBLE
        } else {
            ivActivateVoiceChat.visibility = View.VISIBLE
            ivSendTextChat.visibility = View.GONE
        }
    }

    private fun changeChatMediaView(isExpanded: Boolean) = binding.run {
        llMediaContainer.visibility =
            if (isExpanded) View.VISIBLE
            else View.GONE

        ivExpansion.visibility =
            if (isExpanded) View.GONE
            else View.VISIBLE

        ivCancel.visibility =
            if (isExpanded) View.VISIBLE
            else View.GONE
    }

    private fun collectNavViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.textChat.collectLatest(::prepareTextChat) }
            launch { viewModel.expandChat.collectLatest(::changeChatMediaView) }

            launch {
                viewModel.chatList.collectLatest {
                    adapter.differ.submitList(it)
                }
            }
            launch {
                viewModel.scrollToBottom.collectLatest {
                    if (it) scrollToBottom()
                }
            }
            launch {
                navViewModel.showChatNavigation.collectLatest {
                    setBackCallback(it)
                }
            }
        }
    }
}