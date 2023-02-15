package com.clonect.feeltalk.presentation.ui.question_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentQuestionListBinding
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.presentation.ui.bottom_navigation.BottomNavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuestionListFragment : Fragment() {

    private lateinit var binding: FragmentQuestionListBinding
    private val viewModel: QuestionListViewModel by viewModels()
    private val navViewModel: BottomNavigationViewModel by activityViewModels()
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

        collectQuestionList()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        navViewModel.questionListScrollState.value?.let {
            binding.rvQuestionList.layoutManager?.onRestoreInstanceState(it)
            navViewModel.setQuestionListScrollState(null)
        }
        binding.rvQuestionList.adapter = adapter
        adapter.setOnItemClickListener {
            clickQuestionItem(it)
        }
    }


    private fun collectQuestionList() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.questionListState.collectLatest {
                adapter.differ.submitList(it)
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

        if (!::binding.isInitialized) return
        val scrollState = binding.rvQuestionList.layoutManager?.onSaveInstanceState()
        scrollState?.let {
            navViewModel.setQuestionListScrollState(it)
        }
    }

}