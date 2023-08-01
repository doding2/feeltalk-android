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
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentQuestionBinding
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
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

        initRecyclerView()
        collectViewModel()
        navViewModel.setShowQuestionPage(true)
        navViewModel.setPartnerLastChatColor(requireContext().getColor(R.color.gray_200))

        binding.apply {
            ivScrollTop.setOnClickListener { scrollToTop() }
        }
    }

    override fun onResume() {
        super.onResume()
        setLightStatusBars(true, activity, binding.root)
    }

    private fun showAnswerBottomSheet(question: Question) {
        navViewModel.setAnswerTargetQuestion(question)
        navViewModel.setShowAnswerSheet(true)
    }

    // TODO
    private fun onAnswerQuestion(question: Question) {
//        val answeredItemPosition = adapter.differ.currentList.indexOf(question)
//        adapter.notifyItemChanged(answeredItemPosition)
//
//        if (question.index == viewModel.todayQuestion.value.index) {
//            changeTodayQuestionView(question)
//        }
    }

    private fun onQuestionClick(question: Question) {
        showAnswerBottomSheet(question)
        navViewModel.setShowChatNavigation(false)
    }


    private fun initRecyclerView() = binding.run {
        rvQuestion.adapter = adapter
        adapter.setOnItemClickListener(::onQuestionClick)

        rvQuestion.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isInTop = !recyclerView.canScrollVertically(-10)
                navViewModel.setInQuestionTop(isInTop)
            }
        })
    }

    private fun scrollToTop() {
        binding.rvQuestion.smoothScrollToPosition(0)
    }


    private fun changeTodayQuestionView(question: Question) = binding.run {
//         TODO 나중에 어답터로 이 코드들 옮기기
//        tvTodayQuestionHeader.text = question.header
//        tvTodayQuestionBody.text = question.body
//        tvTodayQuestionDate.text = question.date
//
//        val isUserAnswered = question.myAnswer != null
//        if (isUserAnswered) {
//            mcvAnswerOrChat.setOnClickListener { navigateToChat() }
//            mcvAnswerOrChat.setCardBackgroundColor(Color.WHITE)
//            tvAnswerOrChat.setText(R.string.question_today_button_chat)
//            tvAnswerOrChat.setTextColor(requireContext().getColor(R.color.main_500))
//        } else {
//            mcvAnswerOrChat.setOnClickListener { showAnswerBottomSheet(question) }
//            mcvAnswerOrChat.setCardBackgroundColor(Color.BLACK)
//            tvAnswerOrChat.setText(R.string.question_today_button_answer)
//            tvAnswerOrChat.setTextColor(Color.WHITE)
//        }
    }

    private fun changeRecyclerViewItems(items: List<Question>) {
//        val todayQuestion = viewModel.todayQuestion.value
//        val copyList = items.toMutableList()
//        copyList.removeAll { it.index == todayQuestion.index }
//        copyList.remove(todayQuestion)  // TODO
//        adapter.differ.submitList(copyList)
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.pagingQuestion.collectLatest {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        navViewModel.setShowQuestionPage(false)
        navViewModel.setPartnerLastChatColor(Color.WHITE)
    }
}