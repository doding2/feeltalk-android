package com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.bubble

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.clonect.feeltalk.databinding.ActivityBubbleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BubbleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBubbleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBubbleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}