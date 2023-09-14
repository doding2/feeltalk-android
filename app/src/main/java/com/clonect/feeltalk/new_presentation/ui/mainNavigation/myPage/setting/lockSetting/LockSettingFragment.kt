package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.lockSetting

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.FragmentLockSettingBinding
import com.clonect.feeltalk.new_presentation.ui.util.getNavigationBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LockSettingFragment : Fragment() {

    private lateinit var binding: FragmentLockSettingBinding
    private val viewModel: LockSettingViewModel by viewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLockSettingBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setPadding(0, getStatusBarHeight(), 0, getNavigationBarHeight())
        }
        setFragmentResultListener("passwordSettingFragment") { requestKey, bundle ->
            viewModel.setLockEnabled(bundle.getBoolean("lockEnabled", false))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectViewModel()

        binding.run {
            ivBack.setOnClickListener { onBackCallback.handleOnBackPressed() }
            llLockSwitch.setOnClickListener { toggleLock() }
            llChangePassword.setOnClickListener { navigateToPasswordSetting(isLockEnabled = true) }
        }
    }


    private fun navigateToPasswordSetting(isLockEnabled: Boolean) {
        val bundle = bundleOf(
            "isLockEnabled" to isLockEnabled
        )
        requireParentFragment()
            .findNavController()
            .navigate(R.id.action_lockSettingFragment_to_passwordSettingFragment, bundle)
    }

    private fun toggleLock() = binding.run {
        if (viewModel.lockEnabled.value) {
            viewModel.setLockEnabled(false)
        } else {
            navigateToPasswordSetting(isLockEnabled = false)
        }
    }






    private fun changeSwitchLock(enabled: Boolean) = binding.run {
        switchLock.isChecked = enabled
        if (enabled) {
            llChangePassword.visibility = View.VISIBLE
            switchLock.setBackDrawableRes(R.drawable.n_ic_switch_track_on)
        } else {
            llChangePassword.visibility = View.GONE
            switchLock.setBackDrawableRes(R.drawable.n_ic_switch_track_off)
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.lockEnabled.collectLatest(::changeSwitchLock) }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }
}