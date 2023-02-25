package com.clonect.feeltalk.presentation.ui.news

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.databinding.FragmentNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private val viewModel: NewsViewModel by viewModels()
    @Inject
    lateinit var adapter: NewsAdapter
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)

        collectPartnerProfileUrl()
        collectNewsList()
        initRecyclerView()
        updateHomeNotificationIconState()

        binding.btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

        return binding.root
    }

    private fun updateHomeNotificationIconState() {
        val appSettings = viewModel.getAppSettings().apply {
            isNotificationUpdated = false
        }
        viewModel.saveAppSettings(appSettings)
    }

    private fun initRecyclerView() {
        binding.rvNews.adapter = adapter
    }


    private fun collectNewsList() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.newsList.collectLatest {
                adapter.differ.submitList(it)
            }
        }
    }

    private fun collectPartnerProfileUrl() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.partnerProfileUrl.collectLatest {
                adapter.setPartnerProfileUrl(it)
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}