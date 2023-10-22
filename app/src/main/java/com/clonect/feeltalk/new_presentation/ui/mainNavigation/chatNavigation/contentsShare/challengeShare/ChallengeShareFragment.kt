package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.contentsShare.challengeShare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.databinding.FragmentChallengeShareBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.contentsShare.ContentsShareViewModel
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengeShareFragment : Fragment() {

    private lateinit var binding: FragmentChallengeShareBinding
    private val shareViewModel: ContentsShareViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private val viewModel: ChallengeShareViewModel by viewModels()
    lateinit var adapter: ChallengeShareAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChallengeShareBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()
    }

    private fun initRecyclerView() = binding.run {
        adapter = ChallengeShareAdapter().apply {
            setOnItemSelectListener(::onChallengeSelect)
        }
        rvChallengeShare.adapter = adapter
        rvChallengeShare.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val isInTop = !recyclerView.canScrollVertically(-10)
                viewModel.setInTop(isInTop)
            }
        })
    }

    private fun onChallengeSelect(challenge: Challenge?) {
        shareViewModel.selectChallenge(challenge)
    }


    private fun scrollToTop() {
        binding.rvChallengeShare.smoothScrollToPosition(0)
    }


    private fun showSnackBar(message: String) {
        val decorView = activity?.window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = message,
            duration = Snackbar.LENGTH_SHORT,
            onClick = {
                it.dismiss()
            }
        ).show()
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch {
                viewModel.pagingChallenge.collectLatest {
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