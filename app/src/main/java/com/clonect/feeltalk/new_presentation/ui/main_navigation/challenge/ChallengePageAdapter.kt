package com.clonect.feeltalk.new_presentation.ui.main_navigation.challenge

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.clonect.feeltalk.new_presentation.ui.main_navigation.challenge.completed.CompletedFragment
import com.clonect.feeltalk.new_presentation.ui.main_navigation.challenge.ongoing.OngoingFragment

class ChallengePageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object {
        const val NUM_TABS = 2
    }

    override fun getItemCount(): Int = NUM_TABS

    override fun createFragment(position: Int) = when (position) {
        0 -> OngoingFragment()
        else -> CompletedFragment()
    }

}