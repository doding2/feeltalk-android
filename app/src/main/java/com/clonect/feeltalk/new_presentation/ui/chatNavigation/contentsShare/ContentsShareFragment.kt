package com.clonect.feeltalk.new_presentation.ui.chatNavigation.contentsShare

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentContentsShareBinding
import com.clonect.feeltalk.databinding.TabItemContentsShareBinding
import com.clonect.feeltalk.new_presentation.ui.util.*
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContentsShareFragment : Fragment() {

    private lateinit var binding: FragmentContentsShareBinding
    private val viewModel: ContentsShareViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentContentsShareBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
            setLightStatusBars(true, activity, binding.root)
        } else {
            activity.setStatusBarColor(binding.root, requireContext().getColor(R.color.gray_200), true)
        }
        initTabLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initTabLayout() = binding.run {
        vpNavigation.adapter = ContentsShareAdapter(requireContext(), listOf("question", "challenge"))

        val questionTab = tbContentsTabs.newTab().apply {
            set(isQuestion = true, enabled = true)
        }
        val challengeTab = tbContentsTabs.newTab().apply {
            set(isQuestion = false, enabled = false)
        }
        tbContentsTabs.addTab(questionTab)
        tbContentsTabs.addTab(challengeTab)
        tbContentsTabs.setupWithViewPager(vpNavigation)

        tbContentsTabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> tab.set(isQuestion = true, enabled = true)
                    1 -> tab.set(isQuestion = false, enabled = true)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> tab.set(isQuestion = true, enabled = false)
                    1 -> tab.set(isQuestion = false, enabled = false)
                }
            }
        })

        tbContentsTabs.selectTab(questionTab, true)
    }

    private fun TabLayout.Tab.set(isQuestion: Boolean, enabled: Boolean) {
        if (customView == null) {
            setCustomView(R.layout.tab_item_contents_share)
        }
        val tabBinding = TabItemContentsShareBinding.bind(customView!!)
        tabBinding.run {
            if (isQuestion) {
                tvBody.setText(R.string.contents_share_tab_question)
            } else {
                tvBody.setText(R.string.contents_share_tab_challenge)
            }

            val dividerParams = divider.layoutParams as LinearLayout.LayoutParams

            if (enabled) {
                tvBody.setTextColor(requireContext().getColor(R.color.black))
                divider.setBackgroundColor(requireContext().getColor(R.color.black))
                dividerParams.height = activity.dpToPx(2f).toInt()
                divider.layoutParams = dividerParams
            } else {
                tvBody.setTextColor(requireContext().getColor(R.color.gray_400))
                divider.setBackgroundColor(requireContext().getColor(R.color.gray_400))
                dividerParams.height = activity.dpToPx(1f).toInt()
                divider.layoutParams = dividerParams
            }
        }
    }
}