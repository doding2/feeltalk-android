package com.clonect.feeltalk.presentation.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentChatBinding
import com.clonect.feeltalk.domain.model.question.Question
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
    lateinit var  adapter: ChatAdapter

    private var scrollRemainHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        initRecyclerView()

        collectQuestion()
        collectChatList()
        collectScrollPosition()

        binding.btnSendChat.setOnClickListener { sendChat() }

        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvChat.adapter = adapter
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

    private fun sendChat() {
        val format = SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss", Locale.getDefault())
        val date = format.format(Date())

        binding.etChat.text.toString()
            .takeIf { it.isNotEmpty() }
            ?.let {
                binding.etChat.setText("")
                viewModel.sendChat(
                    content = it,
                    date = date
                )
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
}