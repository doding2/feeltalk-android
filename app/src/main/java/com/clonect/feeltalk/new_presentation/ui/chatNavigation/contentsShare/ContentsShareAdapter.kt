package com.clonect.feeltalk.new_presentation.ui.chatNavigation.contentsShare

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.contentsShare.challengeShare.ChallengeShareFragment
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.contentsShare.questionShare.QuestionShareFragment

class ContentsShareAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object {
        const val NUM_TABS = 2
    }

    override fun getItemCount(): Int = NUM_TABS

    override fun createFragment(position: Int) = when (position) {
        0 -> QuestionShareFragment()
        else -> ChallengeShareFragment()
    }

}