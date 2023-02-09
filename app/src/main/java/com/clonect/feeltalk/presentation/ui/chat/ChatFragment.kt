package com.clonect.feeltalk.presentation.ui.chat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentChatBinding
import com.clonect.feeltalk.domain.model.data.question.Question
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    @Inject
    lateinit var adapter: ChatAdapter
    private lateinit var onBackCallback: OnBackPressedCallback

    private var scrollRemainHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        preventScreenCapture()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        collectQuestion()
        collectChatList()
        collectScrollPosition()

        binding.btnSendChat.setOnClickListener { sendChat() }

        binding.btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
    }

    private fun preventScreenCapture() {
        requireActivity().window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }

    private fun sendChat() {
        binding.etChat.text.toString()
            .takeIf { it.isNotEmpty() }
            ?.let {
                binding.etChat.setText("")
                viewModel.sendChat(it)
            }
    }

    private fun initRecyclerView() {
        binding.rvChat.adapter = adapter
        // adjust scroll y when keboard up/down
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

    private fun collectQuestion() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.questionState.collectLatest {
                reassembleQuestionTitle(it)
            }
        }
    }

    private fun collectChatList() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.chatListState.collectLatest {
                adapter.differ.submitList(it) {
                    scrollRemainHeight -= computeRemainScrollHeight()
                    val position = adapter.itemCount - 1
                    viewModel.updateScrollPosition(position)
                }
            }
        }
    }

    private fun collectScrollPosition() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.scrollPositionState.collectLatest {
                binding.rvChat.scrollToPosition(it)
            }
        }
    }


    private fun computeRemainScrollHeight(): Int {
        return binding.rvChat.run {
            computeVerticalScrollRange() - computeVerticalScrollOffset() - computeVerticalScrollExtent()
        }
    }

    private fun reassembleQuestionTitle(question: Question) {
        binding.layoutQuestionContent.removeAllViewsInLayout()

        question.contentPrefix.reassembleTextView(R.layout.text_view_question_content_prefix)
        question.content.reassembleTextView(R.layout.text_view_question_content)
        question.contentSuffix.reassembleTextView(R.layout.text_view_question_content_suffix)
    }

    private fun String.reassembleTextView(@LayoutRes resource: Int) {
        val wordList = this.split(" ")

        wordList.forEachIndexed { index, word ->
            val textView = LayoutInflater.from(requireContext()).inflate(resource, null) as TextView

            textView.text =
                if (index == wordList.lastIndex && word != " ") word else "$word "

            binding.layoutQuestionContent.addView(textView)
        }
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