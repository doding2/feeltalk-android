package com.clonect.feeltalk.release_presentation.ui.serverDown

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.databinding.FragmentServerDownBinding
import com.clonect.feeltalk.release_presentation.ui.util.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServerDownFragment : Fragment() {

    private lateinit var binding: FragmentServerDownBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentServerDownBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, 0)
        }
        return binding.root
    }
}