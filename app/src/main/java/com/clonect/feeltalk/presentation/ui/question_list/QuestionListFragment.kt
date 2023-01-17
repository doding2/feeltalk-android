package com.clonect.feeltalk.presentation.ui.question_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentQuestionListBinding
import com.clonect.feeltalk.domain.model.question.Question
import com.clonect.feeltalk.presentation.util.addTextGradient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuestionListFragment : Fragment() {

    private lateinit var binding: FragmentQuestionListBinding
    @Inject
    lateinit var adapter: QuestionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentQuestionListBinding.inflate(inflater, container, false)

        binding.textLogo.addTextGradient()

        binding.btnMoreSelection.setOnClickListener {

        }

        // update new list
        binding.rvQuestionList.adapter = adapter
        adapter.differ.submitList(mutableListOf(
            Question(-50505L, "", "", "", "", "", "", ""),
            Question(0, "", "질문 1","", "내 답변 1", "상대방 답변 1", "", ""),
            Question(1, "", "길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 2", "", "", "", "", ""),
            Question(2, "", "질문 3", "", "내 답변 3", "", "", ""),
            Question(3, "", "질문 4","", "", "상대방 답변 4", "", "")
        ))
        adapter.setOnItemClickListener {
            clickQuestionItem(it)
        }


        return binding.root
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

}