package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentChallengeBinding
import com.clonect.feeltalk.databinding.TabItemCompletedBinding
import com.clonect.feeltalk.databinding.TabItemOngoingBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengeFragment : Fragment() {

    private lateinit var binding: FragmentChallengeBinding
    private val viewModel: ChallengeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChallengeBinding.inflate(inflater, container, false)
//        binding.root.setPadding(0, getStatusBarHeight(), 0, 0)
        setLightStatusBars(true, activity, binding.root)
        activity?.window?.statusBarColor = requireContext().getColor(R.color.gray_100)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabLayout()
        collectViewModel()

        binding.run {
            ivScrollTop.setOnClickListener { scrollTop() }
            efabAddChallenge.setOnClickListener { navigateToAddChallenge() }
        }
    }

    private fun navigateToAddChallenge() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_addChallengeFragment)
    }

    private fun scrollTop() {
        val selectedTabPosition = binding.tbChallengeTabs.selectedTabPosition
        when (selectedTabPosition) {
            0 -> viewModel.setOngoingFragmentScrollToTop()
            1 -> viewModel.setCompletedFragmentScrollToTop()
        }
    }


    private fun initTabLayout() = binding.run {
        tbChallengeTabs.addTab(tbChallengeTabs.newTab())
        tbChallengeTabs.addTab(tbChallengeTabs.newTab())
        vpNavigation.adapter = ChallengePageAdapter(childFragmentManager, lifecycle)

        tbChallengeTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        tab.enableOngoingTabView(true)
                        setAddButtonHidden(false)
                    }
                    1 -> tab.enableCompletedTabView(true)
                    else -> return
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        tab.enableOngoingTabView(false)
                        setAddButtonHidden(true)
                    }
                    1 -> tab.enableCompletedTabView(false)
                    else -> return
                }
            }
        })

        TabLayoutMediator(tbChallengeTabs, vpNavigation) { tab, position ->
            when (position) {
                0 -> {
                    tab.setCustomView(R.layout.tab_item_ongoing)
                    tab.enableOngoingTabView(tab.isSelected)
                }
                else -> {
                    tab.setCustomView(R.layout.tab_item_completed)
                    tab.enableCompletedTabView(tab.isSelected)
                }
            }
        }.attach()
    }

    private fun TabLayout.Tab.enableOngoingTabView(enabled: Boolean) {
        val tabBinding = TabItemOngoingBinding.bind(customView ?: return)
        tabBinding.run {
            tvCount.text = viewModel.challenges.value.count { !it.isCompleted }.toString()

            if (enabled) {
                tvBody.setTextColor(requireContext().getColor(R.color.system_black))
                tvCount.setTextColor(requireContext().getColor(R.color.main_500))
                divider.setBackgroundColor(requireContext().getColor(R.color.main_500))
            } else {
                tvBody.setTextColor(requireContext().getColor(R.color.gray_600))
                tvCount.setTextColor(requireContext().getColor(R.color.main_400))
                divider.setBackgroundColor(requireContext().getColor(R.color.main_400))
            }
        }
    }

    private fun TabLayout.Tab.enableCompletedTabView(enabled: Boolean) {
        val tabBinding = TabItemCompletedBinding.bind(customView ?: return)
        tabBinding.run {
            tvCount.text = viewModel.challenges.value.count { it.isCompleted }.toString()

            if (enabled) {
                tvBody.setTextColor(requireContext().getColor(R.color.system_black))
                tvCount.setTextColor(requireContext().getColor(R.color.system_black))
                divider.setBackgroundColor(requireContext().getColor(R.color.system_black))
            } else {
                tvBody.setTextColor(requireContext().getColor(R.color.gray_600))
                tvCount.setTextColor(requireContext().getColor(R.color.gray_600))
                divider.setBackgroundColor(requireContext().getColor(R.color.gray_600))
            }
        }
    }

    private fun setAddButtonHidden(isHidden: Boolean) = binding.run {
        if (isHidden) {
            mcvAddChallengeWrapper.visibility = View.GONE
            efabAddChallenge.hide()
        } else {
            mcvAddChallengeWrapper.visibility = View.VISIBLE
            efabAddChallenge.show()
        }
    }



    private fun changeChallengeViews(list: List<Challenge>) = binding.run {
        changeCountTitleText(list.size)
    }

    private fun changeCountTitleText(count: Int) {
        val text = count.toString() + requireContext().getString(R.string.challenge_count_title_deco)
        val span = SpannableStringBuilder(text).apply {
            setSpan(ForegroundColorSpan(requireContext().getColor(R.color.main_500)), 0, count.toString().length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.BLACK), count.toString().length + 1, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.tvCountTitle.text = span
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.challenges.collectLatest(::changeChallengeViews) }
        }
    }
}