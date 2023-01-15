package com.clonect.feeltalk.presentation.ui.question_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            Question(-50505L, null, null, null),
            Question(0, "질문 1", "내 답변 1", "상대방 답변 1"),
            Question(1, "길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 길쭉한 질문 2", "내 답변 2", "상대방 답변 2"),
            Question(2, "질문 3", "내 답변 3", "상대방 답변 3"),
            Question(3, "질문 4", "내 답변 4", "상대방 답변 4")
        ))


        return binding.root
    }

}