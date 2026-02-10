package com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.contentsShare.questionShare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.databinding.FragmentQuestionShareBinding
import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.contentsShare.ContentsShareViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock


@AndroidEntryPoint
class QuestionShareFragment : Fragment() {
    
    private lateinit var binding: FragmentQuestionShareBinding
    private val shareViewModel: ContentsShareViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private val viewModel: QuestionShareViewModel by viewModels()
    lateinit var adapter: QuestionShareAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentQuestionShareBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
    }

    private fun initRecyclerView() = binding.run {
        adapter = QuestionShareAdapter().apply {
            setOnItemSelectListener(::onQuestionSelect)
            addLoadStateListener {
                if (it.prepend is LoadState.Error && !viewModel.questionSharePagingRetryLock.isLocked) {
                    lifecycleScope.launch {
                        viewModel.questionSharePagingRetryLock.withLock {
                            delay(10000)
                            retry()
                        }
                    }
                }
            }
        }
        rvQuestionShare.adapter = adapter
        rvQuestionShare.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isInTop = !recyclerView.canScrollVertically(-10)
                viewModel.setInTop(isInTop)
            }
        })
    }

    private fun onQuestionSelect(question: Question?) {
        shareViewModel.selectQuestion(question)
    }


    private fun scrollToTop() {
        binding.rvQuestionShare.smoothScrollToPosition(0)
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.pagingQuestion.collectLatest {
                    adapter.submitData(lifecycle, it)
                }
            }
            launch {
                viewModel.scrollToTop.collectLatest {
                    if (it) {
                        delay(50)
                        scrollToTop()
                        viewModel.setScrollToTop(false)
                    }
                }
            }
        }
    }
}