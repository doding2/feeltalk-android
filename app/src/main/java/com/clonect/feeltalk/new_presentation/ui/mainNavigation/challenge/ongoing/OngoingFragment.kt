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
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentOngoingBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ChallengeViewModel
import com.clonect.feeltalk.new_presentation.ui.util.showConfirmDialog
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OngoingFragment : Fragment() {

    private lateinit var binding: FragmentOngoingBinding
    private val challengeViewModel: ChallengeViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private val ongoingViewModel: OngoingChallengeViewModel by viewModels()
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


    private fun onItemClick(item: Challenge) {
        navigateToDetail(item)
    }

    private fun navigateToDetail(item: Challenge) {
        val bundle = bundleOf("challenge" to item)
        requireParentFragment()
            .requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_ongoingChallengeDetailFragment, bundle)
    }

    private fun onCompleteChallenge(item: Challenge) {
        showConfirmDialog(
            title = requireContext().getString(R.string.complete_challenge_title),
            body = null,
            cancelButton = requireContext().getString(R.string.complete_challenge_cancel),
            confirmButton = requireContext().getString(R.string.complete_challenge_confirm),
            onConfirm = {
                // TODO
            }
        )
    }


    private fun initRecyclerView() = binding.run {
        rvOngoingChallenge.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            justifyContent = JustifyContent.SPACE_BETWEEN
        }

        adapter.calculateItemSize(requireActivity())
        adapter.setOnItemClickListener(::onItemClick)
        adapter.setOnCompleteChallengeListener(::onCompleteChallenge)
        rvOngoingChallenge.adapter = adapter
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
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