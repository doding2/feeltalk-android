package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.completed

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
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentCompletedBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ChallengeViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CompletedFragment : Fragment() {

    private lateinit var binding: FragmentCompletedBinding
    private val challengeViewModel: ChallengeViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private val completedViewModel: CompletedViewModel by viewModels()
    @Inject
    lateinit var adapter: CompletedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCompletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        collectViewModel()
    }

    private fun scrollToTop() {
        binding.rvCompletedChallenge.smoothScrollToPosition(0)
    }

    private fun onItemClick(item: Challenge) {
        navigateToDetail(item)
    }

    private fun navigateToDetail(item: Challenge) {
        val bundle = bundleOf("challenge" to item)
        requireParentFragment()
            .requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_completedChallengeDetailFragment, bundle)
    }

    private fun initRecyclerView() = binding.run {
        rvCompletedChallenge.adapter = adapter.apply {
            setOnItemClickListener(::onItemClick)
            addOnPagesUpdatedListener {
                val isEmpty = itemCount == 0
                completedViewModel.setEmpty(isEmpty)
            }
        }
    }

    private fun applyIsEmptyChanges(isEmpty: Boolean) = binding.run {
        if (isEmpty) {
            rvCompletedChallenge.visibility = View.GONE
            svEmptyChallenge.visibility = View.VISIBLE
        } else {
            rvCompletedChallenge.visibility = View.VISIBLE
            svEmptyChallenge.visibility = View.GONE
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { completedViewModel.isEmpty.collectLatest(::applyIsEmptyChanges) }
            launch {
                challengeViewModel.completedFragmentScrollToTop.collectLatest {
                    if (it) scrollToTop()
                }
            }

            launch {
                completedViewModel.pagingCompletedChallenge.collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }
}