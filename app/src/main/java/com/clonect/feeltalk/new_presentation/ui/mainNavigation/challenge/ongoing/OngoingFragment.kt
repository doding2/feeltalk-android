package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.clonect.feeltalk.databinding.FragmentOngoingBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ChallengeViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OngoingFragment : Fragment() {

    private lateinit var binding: FragmentOngoingBinding
    private val viewModel: ChallengeViewModel by viewModels(ownerProducer = { requireParentFragment() })
    @Inject
    lateinit var adapter: OngoingChallengeAdapter

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

    // TODO
    private fun onItemClick(item: Challenge) {

    }

    private fun initRecyclerView() = binding.run {
        rvOngoingChallenge.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            justifyContent = JustifyContent.SPACE_BETWEEN
        }

        adapter.calculateItemSize(requireActivity())
        adapter.setOnItemClickListener(::onItemClick)
        rvOngoingChallenge.adapter = adapter
    }


    private fun changeAdapter(fullList: List<Challenge>) {
        val filtered = fullList.filter { !it.isCompleted }
        adapter.differ.submitList(filtered)
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.ongoingFragmentScrollToTop.collectLatest {
                    if (it) scrollToTop()
                }
            }

            launch { viewModel.challenges.collectLatest(::changeAdapter) }
        }
    }
}