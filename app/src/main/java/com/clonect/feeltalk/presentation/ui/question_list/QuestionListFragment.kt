package com.clonect.feeltalk.presentation.ui.question_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentQuestionListBinding
import com.clonect.feeltalk.domain.model.question.Question
import com.clonect.feeltalk.presentation.util.addTextGradient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuestionListFragment : Fragment() {

    private lateinit var binding: FragmentQuestionListBinding
    private val viewModel: QuestionListViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    @Inject
    lateinit var adapter: QuestionListAdapter
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentQuestionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textLogo.addTextGradient()

        binding.btnMoreSelection.setOnClickListener {

        }

        binding.rvQuestionList.adapter = adapter
        adapter.differ.submitList(mutableListOf(
            Question(0, "", "질문 1","", "내 답변 1", "상대방 답변 1", "", ""),
            Question(1, "", "길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 2", "", "", "", "", ""),
            Question(2, "", "질문 3", "", "내 답변 3", "", "", ""),
            Question(3, "", "질문 4","", "", "상대방 답변 4", "", ""),
            Question(4, "", "질문 1","", "내 답변 1", "상대방 답변 1", "", ""),
            Question(5, "", "길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 2", "", "", "", "", ""),
            Question(6, "", "질문 3", "", "내 답변 3", "", "", ""),
            Question(7, "", "질문 4","", "", "상대방 답변 4", "", ""),
            Question(8, "", "질문 1","", "내 답변 1", "상대방 답변 1", "", ""),
            Question(9, "", "길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 2", "", "", "", "", ""),
            Question(10, "", "질문 3", "", "내 답변 3", "", "", ""),
            Question(11, "", "질문 4","", "", "상대방 답변 4", "", ""),
            Question(12, "", "질문 1","", "내 답변 1", "상대방 답변 1", "", ""),
            Question(13, "", "길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 2", "", "", "", "", ""),
            Question(14, "", "질문 3", "", "내 답변 3", "", "", ""),
            Question(15, "", "질문 4","", "", "상대방 답변 4", "", ""),
            Question(-50505L, "", "", "", "", "", "", ""),
        ))
        adapter.setOnItemClickListener {
            clickQuestionItem(it)
        }

        collectScrollPosition()
    }

    private fun collectScrollPosition() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.scrollPositionState.collectLatest {
                binding.rvQuestionList.apply {
                    post {
                        scrollBy(0, it)
                    }
                }
            }
        }
    }

    private fun clickQuestionItem(question: Question) {
        if (question.myAnswer.isNullOrEmpty()) {
            navigateToTodayQuestionPage(question)
            return
        }
        navigateToChatPage(question)
    }

    private fun navigateToTodayQuestionPage(question: Question) {
        val bundle = bundleOf("selectedQuestion" to question)

        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(
                R.id.action_bottomNavigationFragment_to_todayQuestionFragment,
                bundle
            )
    }

    private fun navigateToChatPage(question: Question) {
        val bundle = bundleOf("selectedQuestion" to question)

        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(
                R.id.action_bottomNavigationFragment_to_chatFragment,
                bundle
            )
    }


    private fun saveScrollPositionState() {
        binding.rvQuestionList.apply {
            val position = computeVerticalScrollOffset()
            viewModel.saveScrollPosition(position)
            Log.i("QuestinListFragment", "scrollPosition: $position")
        }
    }

    override fun onPause() {
        super.onPause()
        saveScrollPositionState()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this@QuestionListFragment.requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}