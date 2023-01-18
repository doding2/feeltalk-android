package com.clonect.feeltalk.presentation.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentChatBinding
import com.clonect.feeltalk.domain.model.chat.Chat
import com.clonect.feeltalk.domain.model.question.Question
import com.clonect.feeltalk.presentation.util.addTextGradient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    @Inject
    lateinit var  adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        initRecyclerView()

        collectQuestion()
        collectChatList()

        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvChat.adapter = adapter
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
                adapter.differ.submitList(it)
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