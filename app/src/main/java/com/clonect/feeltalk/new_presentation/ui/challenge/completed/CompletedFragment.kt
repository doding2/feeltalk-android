package com.clonect.feeltalk.new_presentation.ui.challenge.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.databinding.FragmentCompletedBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.challenge.ChallengeViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CompletedFragment : Fragment() {

    private lateinit var binding: FragmentCompletedBinding
    private val viewModel: ChallengeViewModel by viewModels(ownerProducer = { requireParentFragment() })
    @Inject
    lateinit var adapter: CompletedChallengeAdapter

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

    // TODO
    private fun onItemClick(item: Challenge) {

    }

    private fun initRecyclerView() = binding.run {
        rvCompletedChallenge.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            justifyContent = JustifyContent.SPACE_BETWEEN
        }

        adapter.calculateItemSize(requireActivity())
        adapter.setOnItemClickListener(::onItemClick)
        rvCompletedChallenge.adapter = adapter
    }


    private fun changeAdapter(fullList: List<Challenge>) {
        val filtered = fullList.filter { it.isCompleted }
        adapter.differ.submitList(filtered)
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.completedFragmentScrollToTop.collectLatest {
                    if (it) scrollToTop()
                }
            }

            launch { viewModel.challenges.collectLatest(::changeAdapter) }
        }
    }
}