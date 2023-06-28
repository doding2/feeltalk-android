package com.clonect.feeltalk.new_presentation.ui.mainNavigation.question

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentQuestionBinding
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.answer.AnswerBottomSheetFragment
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.new_presentation.ui.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuestionFragment : Fragment() {

    private lateinit var binding: FragmentQuestionBinding
    private val viewModel: QuestionViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()

    @Inject
    lateinit var adapter: QuestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentQuestionBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, 0)
            setLightStatusBars(true, activity, binding.root)
        } else {
            activity.setStatusBarColor(binding.root, requireContext().getColor(R.color.gray_100), true)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
        initRecyclerView()

        binding.apply {
            ivScrollTop.setOnClickListener { scrollToTop() }
        }
    }


    private fun navigateToChat() {
//        requireParentFragment()
//            .requireParentFragment()
//            .findNavController()
//            .navigate(R.id.action_mainNavigationFragment_to_chatFragment)
    }

    private fun showAnswerBottomSheet(question: Question) {
        val bottomSheet = AnswerBottomSheetFragment(::onAnswerQuestion)

        val bundle = Bundle()
        bundle.putSerializable("question", question)
        bottomSheet.arguments = bundle

        bottomSheet.show(requireActivity().supportFragmentManager, AnswerBottomSheetFragment.TAG)
    }

    private fun onAnswerQuestion(question: Question) {
        val answeredItemPosition = adapter.differ.currentList.indexOf(question)
        adapter.notifyItemChanged(answeredItemPosition)

        if (question.index == viewModel.todayQuestion.value.index) {
            changeTodayQuestionView(question)
        }
    }

    private fun onQuestionClick(question: Question) {
        showAnswerBottomSheet(question)
    }


    private fun initRecyclerView() {
        binding.rvQuestion.adapter = adapter
        adapter.setOnItemClickListener(::onQuestionClick)
    }

    private fun scrollToTop() {
        binding.rvQuestion.smoothScrollToPosition(0)
    }


    private fun changeTodayQuestionView(question: Question) = binding.run {
        tvTodayQuestionHeader.text = question.header
        tvTodayQuestionBody.text = question.body
        tvTodayQuestionDate.text = question.date

        val isUserAnswered = question.myAnswer != null
        if (isUserAnswered) {
            mcvAnswerOrChat.setOnClickListener { navigateToChat() }
            mcvAnswerOrChat.setCardBackgroundColor(Color.WHITE)
            tvAnswerOrChat.setText(R.string.question_today_button_chat)
            tvAnswerOrChat.setTextColor(requireContext().getColor(R.color.main_500))
        } else {
            mcvAnswerOrChat.setOnClickListener { showAnswerBottomSheet(question) }
            mcvAnswerOrChat.setCardBackgroundColor(Color.BLACK)
            tvAnswerOrChat.setText(R.string.question_today_button_answer)
            tvAnswerOrChat.setTextColor(Color.WHITE)
        }
    }

    private fun changeRecyclerViewItems(items: List<Question>) {
        val todayQuestion = viewModel.todayQuestion.value
        val copyList = items.toMutableList()
        copyList.removeAll { it.index == todayQuestion.index }
        copyList.remove(todayQuestion)  // TODO
        adapter.differ.submitList(copyList)
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.todayQuestion.collectLatest(::changeTodayQuestionView) }
            launch { viewModel.questions.collectLatest(::changeRecyclerViewItems) }
        }
    }


    override fun onDetach() {
        super.onDetach()
    }
}