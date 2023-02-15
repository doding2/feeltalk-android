package com.clonect.feeltalk.presentation.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
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

        initActionBar()
        initRecyclerView()

        collectPartnerInfo()
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
        if (!viewModel.isPartnerAnswered()) {
            Toast.makeText(requireContext(), "상대가 질문에 답하기 전에는 채팅을 이용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.etChat.text.toString()
            .takeIf { it.isNotEmpty() }
            ?.let {
                viewModel.sendChat(it)
                binding.etChat.setText("")
            }
    }


    private fun collectPartnerInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.partnerInfo.collectLatest {
                adapter.setPartnerNickname(it.nickname)
            }
        }
    }

    private fun collectQuestion() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.question.collectLatest {
                reassembleQuestionTitle(it)
            }
        }
    }

    private fun collectChatList() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.chatList.collectLatest {
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


    private fun initActionBar() = binding.apply {
        tvTitle.text = viewModel.partnerInfo.value.nickname
        // TODO 파트너 프로필 사진
//        ivPartnerProfile.setImageBitmap()
    }

    private fun initRecyclerView() {
        binding.rvChat.adapter = adapter
        binding.rvChat.itemAnimator = null
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

    private fun computeRemainScrollHeight(): Int {
        return binding.rvChat.run {
            computeVerticalScrollRange() - computeVerticalScrollOffset() - computeVerticalScrollExtent()
        }
    }

    private fun reassembleQuestionTitle(question: Question) {
        binding.layoutQuestionContent.removeAllViewsInLayout()

//        question.contentPrefix.reassembleTextView(R.layout.text_view_question_content_prefix)
        question.question.reassembleTextView(R.layout.text_view_question_content)
//        question.contentSuffix.reassembleTextView(R.layout.text_view_question_content_suffix)
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