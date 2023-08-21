package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentChallengeBinding
import com.clonect.feeltalk.databinding.TabItemCompletedBinding
import com.clonect.feeltalk.databinding.TabItemOngoingBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCountDto
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.completed.SnackbarState
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.new_presentation.ui.util.setStatusBarColor
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengeFragment : Fragment() {

    private lateinit var binding: FragmentChallengeBinding
    private val viewModel: ChallengeViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChallengeBinding.inflate(inflater, container, false)
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

        initTabLayout()
        collectViewModel()

        binding.run {
            ivScrollTop.setOnClickListener { scrollTop() }
            efabAddChallenge.setOnClickListener { navigateToAddChallenge() }

            snackbarDeadline.root.setOnClickListener {
                viewModel.setSnackbarState(
                    SnackbarState(
                        isVisible = false,
                        goneSoftly = true
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setLightStatusBars(true, activity, binding.root)
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
        vp2Navigation.adapter = ChallengePageAdapter(childFragmentManager, lifecycle)
        vp2Navigation.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                viewModel.setSnackbarState(
                    SnackbarState(
                        isVisible = false,
                        goneSoftly = false
                    )
                )
            }
        })

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
                        viewModel.setSnackbarState(
                            SnackbarState(
                                isVisible = false,
                                goneSoftly = false
                            )
                        )
                    }
                    1 -> tab.enableCompletedTabView(false)
                    else -> return
                }
            }
        })

        TabLayoutMediator(tbChallengeTabs, vp2Navigation) { tab, position ->
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
            tvCount.text = viewModel.challengeCount.value?.ongoingCount?.toString() ?: ""

            if (enabled) {
                tvBody.setTextColor(requireContext().getColor(R.color.black))
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
            tvCount.text = viewModel.challengeCount.value?.completedCount?.toString() ?: ""

            if (enabled) {
                tvBody.setTextColor(requireContext().getColor(R.color.black))
                tvCount.setTextColor(requireContext().getColor(R.color.black))
                divider.setBackgroundColor(requireContext().getColor(R.color.black))
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



    private fun changeChallengeCountViews(count: ChallengeCountDto?) = binding.run {
        changeCountTitleText(count?.totalCount?.toString() ?: "")
        tbChallengeTabs.getTabAt(0)?.let {
            val tabBinding = TabItemOngoingBinding.bind(it.customView ?: return@let)
            tabBinding.tvCount.text = count?.ongoingCount?.toString() ?: ""
        }
        tbChallengeTabs.getTabAt(1)?.let {
            val tabBinding = TabItemCompletedBinding.bind(it.customView ?: return@let)
            tabBinding.tvCount.text = count?.completedCount?.toString() ?: ""
        }
    }

    private fun changeCountTitleText(count: String) {
        val text = count + requireContext().getString(R.string.challenge_count_title_deco)
        val span = SpannableStringBuilder(text).apply {
            setSpan(ForegroundColorSpan(requireContext().getColor(R.color.main_500)), 0, count.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.BLACK), count.length + 1, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.tvCountTitle.text = span
    }

    private fun changeSnackbarView(state: SnackbarState) = binding.snackbarDeadline.root.run {
        if (state.isVisible) {
            animate()
                .setDuration(200)
                .alpha(1f)
                .withStartAction {
                    visibility = View.VISIBLE
                }.start()

        } else if (state.goneSoftly) {
            animate()
                .setDuration(200)
                .alpha(0f)
                .withEndAction {
                    visibility = View.GONE
                }.start()
        } else {
            animate()
                .setDuration(0)
                .alpha(0f)
                .withEndAction {
                    visibility = View.GONE
                }.start()
        }
    }

    private fun showChallengeDetail(challenge: Challenge?) {
        if (challenge == null) return

        if (challenge.isCompleted) {
            binding.vp2Navigation.currentItem = 1
            navigateToCompletedDetail(challenge)
        } else {
            binding.vp2Navigation.currentItem = 0
            navigateToOngoingDetail(challenge)
        }
        navViewModel.setShowChallengeDetail(null)
    }

    private fun navigateToOngoingDetail(item: Challenge) {
        val bundle = bundleOf("challenge" to item)
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_ongoingChallengeDetailFragment, bundle)
    }

    private fun navigateToCompletedDetail(item: Challenge) {
        val bundle = bundleOf("challenge" to item)
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_completedChallengeDetailFragment, bundle)
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.challengeCount.collectLatest(::changeChallengeCountViews) }
            launch { viewModel.snackbarState.collectLatest(::changeSnackbarView) }
            launch { navViewModel.showChallengeDetail.collectLatest(::showChallengeDetail) }
        }
    }
}