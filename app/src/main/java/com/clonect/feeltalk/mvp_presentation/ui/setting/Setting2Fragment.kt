package com.clonect.feeltalk.mvp_presentation.ui.setting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.FragmentSetting2Binding
import com.clonect.feeltalk.mvp_presentation.ui.bottom_navigation.BottomNavigationViewModel
import com.clonect.feeltalk.mvp_presentation.utils.showPermissionRequestDialog
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
class Setting2Fragment : Fragment() {

    private lateinit var binding: FragmentSetting2Binding
    private val viewModel: Setting2ViewModel by viewModels()
    private val navViewModel: BottomNavigationViewModel by activityViewModels()
    private lateinit var onBackCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSetting2Binding.inflate(inflater, container, false)
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
//            textMyName.setOnClickListener { navigateToCoupleSettingPage() }
            ivHalfArrowRight.setOnClickListener { navigateToCoupleSettingPage() }
            flProfile.setOnClickListener { navigateToCoupleSettingPage() }
            llDDay.setOnClickListener { navigateToCoupleSettingPage() }

            llRequestKeyRestoring.setOnClickListener { navigateToKeyRestoringRequestPage() }
            llPrivacyPolicy.setOnClickListener { navigateToPrivacyPolicy() }

            llCustomerQuestionService.setOnClickListener { sendQuestionEmail() }
            llCustomerFeedbackService.setOnClickListener { sendFeedbackEmail() }

//            llLogOut.setOnClickListener { logOut() }
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

    private fun navigateToKeyRestoringRequestPage() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_keyRestoringRequestFragment)
    }

    private fun navigateToPrivacyPolicy() {
        requireParentFragment()
            .requireParentFragment()
            .findNavController()
            .navigate(R.id.action_bottomNavigationFragment_to_privacyPolicyFragment)
    }


    private fun sendQuestionEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.FEEDBACK_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, "[필로우톡] 이런 질문도 받고싶어요 !")
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun sendFeedbackEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.FEEDBACK_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, "[필로우톡] 이런 점이 아쉬워요 !")
        }
        startActivity(Intent.createChooser(intent, null))
    }
    


    private fun collectUserInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.userInfo.collectLatest {
//                binding.textMyName.text = it.nickname
            }
        }
    }

    private fun collectCoupleAnniversary() = lifecycleScope.launch {
        val dataFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val anniversaryFormat = SimpleDateFormat("yyyy. M. d", Locale.getDefault())

        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.coupleAnniversary.collectLatest {
                val date = it ?: "0000/00/00"
                val formattedDate = dataFormat.parse(date) ?: "0000. 0. 0"
                val anniversary = anniversaryFormat.format(formattedDate)

                binding.textCoupleAnniversary.text = anniversary
                binding.textDDayValue.text = calculateDDay(date)
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
            .circleCrop()
            .fallback(R.drawable.image_my_default_profile)
            .error(R.drawable.image_my_default_profile)
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


    override fun onResume() {
        super.onResume()
        viewModel.run {
            getUserInfo()
            getCoupleAnniversary()
            getMyProfileImageUrl()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this@Setting2Fragment.requireActivity().finish()
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