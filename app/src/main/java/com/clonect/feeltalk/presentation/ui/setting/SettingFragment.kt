package com.clonect.feeltalk.presentation.ui.setting

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.FragmentSettingBinding
import com.clonect.feeltalk.presentation.ui.bottom_navigation.BottomNavigationViewModel
import com.clonect.feeltalk.presentation.utils.showPermissionRequestDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.kyleduo.switchbutton.SwitchButton
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.ceil

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()
    private val navViewModel: BottomNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        restoreScrollViewState()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectUserInfo()
        collectCoupleAnniversary()
        collectMyProfileImageUrl()
        initSwitch()

        binding.apply {
            textMyName.setOnClickListener { navigateToCoupleSettingPage() }
            ivHalfArrowRight.setOnClickListener { navigateToCoupleSettingPage() }
            flProfile.setOnClickListener { navigateToCoupleSettingPage() }
            llDDay.setOnClickListener { navigateToCoupleSettingPage() }

            llLogOut.setOnClickListener { logOut() }
        }
    }


    private fun navigateToCoupleSettingPage() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_coupleSettingFragment)
    }

    private fun navigateToSignUpPage() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_signUpFragment)
    }


    private fun collectUserInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.userInfo.collectLatest {
                binding.textMyName.text = it.nickname
            }
        }
    }

    private fun collectCoupleAnniversary() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.coupleAnniversary.collectLatest {
                binding.textCoupleAnniversary.text = it?.replace("/", ". ")
                it?.let {
                    binding.textDDayValue.text = calculateDDay(it)
                }
            }
        }
    }

    private fun collectMyProfileImageUrl() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.myProfileImageUrl.collectLatest {
                    binding.ivMyProfile.setProfileImageUrl(it)
                }
            }
        }
    }


    private fun ImageView.setProfileImageUrl(url: String?) {
        Glide.with(this)
            .load(url)
            .into(this)
    }


    private fun calculateDDay(date: String): String {
        try {
            val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val anniversaryDate = format.parse(date) ?: return "0"
            val ddayPoint = (Date().time - anniversaryDate.time).toDouble() / Constants.ONE_DAY
            return ceil(ddayPoint).toLong().toString()
        } catch (e: Exception) {
            return "0"
        }
    }


    private fun restoreScrollViewState() {
        binding.root.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                binding.scrollView.viewTreeObserver.removeOnPreDrawListener(this)
                navViewModel.settingScrollState.value?.let {
                    binding.scrollView.scrollY = it
                    navViewModel.setSettingScrollState(null)
                }
                return false
            }
        })
    }


    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            binding.switchPushNotification.toggle()
            return@registerForActivityResult
        }
        showPermissionRequestDialog(
            title = "알림 권한 설정",
            message = "푸쉬 알림을 활성화 하려면 알림 권한을 설정해주셔야 합니다."
        )
    }

    private val usageInfoNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            binding.switchUsageInfoNotification.toggle()
            return@registerForActivityResult
        }
        showPermissionRequestDialog(
            title = "알림 권한 설정",
            message = "이용 정보 알림을 활성화 하려면 알림 권한을 설정해주셔야 합니다."
        )
    }

    private fun initSwitch() = binding.apply {
        layoutPushNotification.setOnClickListener {
            val isPushNotificationEnabled = viewModel.isPushNotificationEnabled.value
            if (isPushNotificationEnabled) {
                switchPushNotification.toggle()
                return@setOnClickListener
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                switchPushNotification.toggle()
            }
        }

        layoutUsageInfoNotification.setOnClickListener {
            val isUsageInfoNotificationEnabled = viewModel.isUsageInfoNotificationEnabled.value
            if (isUsageInfoNotificationEnabled) {
                switchUsageInfoNotification.toggle()
                return@setOnClickListener
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                usageInfoNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                switchUsageInfoNotification.toggle()
            }
        }

        switchPushNotification.apply {
            val initialChecked = viewModel.isPushNotificationEnabled.value
            setCheckedImmediatelyNoEvent(initialChecked)
            setSwitchChecked(initialChecked)

            setOnCheckedChangeListener { _, isChecked ->
                setSwitchChecked(isChecked)
                viewModel.enablePushNotification(isChecked)
            }
        }

        switchUsageInfoNotification.apply {
            val initialChecked = viewModel.isUsageInfoNotificationEnabled.value
            setCheckedImmediatelyNoEvent(initialChecked)
            setSwitchChecked(initialChecked)

            setOnCheckedChangeListener { _, isChecked ->
                setSwitchChecked(isChecked)
                viewModel.enableUsageInfoNotification(isChecked)
            }
        }
    }

    private fun SwitchButton.setSwitchChecked(isChecked: Boolean) {
        val drawableRes =
            if (isChecked) R.drawable.ic_switch_thumb_on
            else R.drawable.ic_switch_thumb_off
        setThumbDrawableRes(drawableRes)
    }


    private fun logOut() = lifecycleScope.launch {
        tryGoogleLogOut()
        tryKakaoLogOut()
        tryNaverLogOut()
        viewModel.clearAllExceptKeys()
        navigateToSignUpPage()
    }

    private suspend fun tryKakaoLogOut() = suspendCoroutine { continuation ->
        UserApiClient.instance.logout { error ->
            if (error == null) {
                continuation.resume(true)
            } else {
                continuation.resume(false)
            }
        }
    }

    private fun tryNaverLogOut(): Boolean {
        NaverIdLoginSDK.logout()
        return true
    }

    private suspend fun tryGoogleLogOut() = suspendCoroutine { continuation ->
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mGoogleSignInClient.signOut()
            .addOnSuccessListener {
                continuation.resume(true)
            }
            .addOnFailureListener {
                continuation.resume(false)
            }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this@SettingFragment.requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDetach() {
        super.onDetach()
        onBackCallback.remove()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        val scrollState = binding.scrollView.scrollY
        navViewModel.setSettingScrollState(scrollState)
    }
}