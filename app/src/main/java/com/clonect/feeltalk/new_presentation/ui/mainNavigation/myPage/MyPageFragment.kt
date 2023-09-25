package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentMyPageBinding
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.MainNavigationViewModel
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.new_presentation.ui.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMyPageBinding
    private val viewModel: MyPageViewModel by viewModels()
    private val navViewModel: MainNavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, 0)
            setLightStatusBars(true, activity, binding.root)
        } else {
            activity.setStatusBarColor(binding.root, requireContext().getColor(R.color.gray_100), true)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setLightStatusBars(true, activity, binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            llSetting.setOnClickListener { navigateToSetting() }
            llInquire.setOnClickListener { navigateToInquire() }
        }
    }

    private fun navigateToSetting() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_settingFragment)
    }

    private fun navigateToInquire() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_mainNavigationFragment_to_inquireFragment)
    }


    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {

        }
    }
}