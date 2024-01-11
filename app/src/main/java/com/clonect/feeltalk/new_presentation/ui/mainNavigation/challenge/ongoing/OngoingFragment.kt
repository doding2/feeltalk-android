package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.PageEvents
import com.clonect.feeltalk.databinding.FragmentOngoingBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ChallengeViewModel
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.completed.SnackbarState
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@AndroidEntryPoint
class OngoingFragment : Fragment() {

    private lateinit var binding: FragmentOngoingBinding
    private val challengeViewModel: ChallengeViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private val ongoingViewModel: OngoingViewModel by viewModels()
    @Inject
    lateinit var adapter: OngoingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOngoingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        collectViewModel()
    }

    private fun scrollToTop() {
        binding.rvOngoingChallenge.smoothScrollToPosition(0)
    }


    private fun onItemClick(item: Challenge) {
        navigateToDetail(item)
        ongoingViewModel.modifyPage(PageEvents.Edit(item.copy(isNew = false)))
    }

    private fun navigateToDetail(item: Challenge) {
        val bundle = bundleOf("challenge" to item)
        requireParentFragment()
            .requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_ongoingChallengeDetailFragment, bundle)
    }

    private fun onFirstImminentItemShow() {
        challengeViewModel.setSnackbarState(
            SnackbarState(
                isVisible = true
            )
        )
        lifecycleScope.launch(Dispatchers.IO) {
            delay(3000)
            challengeViewModel.setSnackbarState(
                SnackbarState(
                    isVisible = false,
                    goneSoftly = true
                )
            )
        }
    }


    private fun initRecyclerView() = binding.run {
        rvOngoingChallenge.adapter = adapter.apply {
            setOnItemClickListener(::onItemClick)
            setOnFirstImminentItemListener(::onFirstImminentItemShow)
            addOnPagesUpdatedListener {
                val isEmpty = itemCount == 0
                ongoingViewModel.setEmpty(isEmpty)
            }
            addLoadStateListener {
                if (it.prepend is LoadState.Error && !ongoingViewModel.ongoingChallengePagingRetryLock.isLocked) {
                    lifecycleScope.launch {
                        ongoingViewModel.ongoingChallengePagingRetryLock.withLock {
                            delay(10000)
                            retry()
                        }
                    }
                }
            }
        }

        rvOngoingChallenge.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    challengeViewModel.setSnackbarState(
                        SnackbarState(
                            isVisible = false,
                            goneSoftly = false
                        )
                    )
                }
            }
        })
    }

    private fun applyIsEmptyChanges(isEmpty: Boolean) = binding.run {
        if (isEmpty) {
            rvOngoingChallenge.visibility = View.GONE
            svEmptyChallenge.visibility = View.VISIBLE
        } else {
            rvOngoingChallenge.visibility = View.VISIBLE
            svEmptyChallenge.visibility = View.GONE
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { ongoingViewModel.isEmpty.collectLatest(::applyIsEmptyChanges) }
            launch {
                challengeViewModel.ongoingFragmentScrollToTop.collectLatest {
                    if (it) scrollToTop()
                }
            }
            launch {
                ongoingViewModel.pagingOngoingChallenge.collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }
}