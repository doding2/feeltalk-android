package com.clonect.feeltalk.new_presentation.ui.main_navigation.bucket_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentBucketListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BucketListFragment : Fragment() {


    private lateinit var binding: FragmentBucketListBinding
    private val viewModel: BucketListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBucketListBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_bucket_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}