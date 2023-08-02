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
                viewModel.setInQuestionTop(isInTop)
                navViewModel.setInQuestionTop(isInTop)
            }
        })
    }

    private fun scrollToTop() {
        binding.rvQuestion.smoothScrollToPosition(0)
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.scrollToTop.collectLatest {
                    if (it) scrollToTop()
                }
            }
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