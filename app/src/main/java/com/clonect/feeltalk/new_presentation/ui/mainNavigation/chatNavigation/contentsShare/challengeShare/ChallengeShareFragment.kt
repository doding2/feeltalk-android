package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.contentsShare.challengeShare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChallengeShareFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_challenge_share, container, false)
    }
}