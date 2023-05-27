package com.clonect.feeltalk.presentation.ui.couple_setting

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.FragmentCoupleSettingBinding
import com.clonect.feeltalk.presentation.utils.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.ceil


@AndroidEntryPoint
class CoupleSettingFragment : Fragment() {

    private lateinit var binding: FragmentCoupleSettingBinding
    private lateinit var onBackCallback: OnBackPressedCallback
    private val viewModel: CoupleSettingViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCoupleSettingBinding.inflate(inflater, container, false)
        loadingDialog = makeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEditInfoDialogs()

        collectUserInfo()
        collectPartnerInfo()
        collectMyProfileImageUrl()
        collectPartnerProfileImageUrl()
        collectCoupleAnniversary()
        collectIsLoading()

        binding.apply {
            btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            ivMyProfile.setOnClickListener { updateProfileImage() }
            
//            clPartnerProfile.setOnClickListener {
//                showBreakUpCoupleDialog(
//                    partnerNickname = viewModel.partnerInfo.value.nickname,
//                    onConfirm = {
//                        lifecycleScope.launch {
//                            val isSuccessful = viewModel.breakUpCouple()
//                            if (isSuccessful) {
//                                Toast.makeText(requireContext(), "커플이 해제되었습니다", Toast.LENGTH_SHORT).show()
//                                navigateToCoupleRegistrationPage()
//                            } else {
//                                Toast.makeText(requireContext(), "실패했습니다", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                )
//            }

            binding.llLeaveFeeltalk.setOnClickListener { leaveFeeltalk() }
        }
    }

    private fun leaveFeeltalk() {
        showAlertDialog(
            title = "필로우톡 회원 탈퇴",
            message = "정말 탈퇴하시겠습니까?",
            confirmButtonText = "확 인",
            onConfirmClick = {
                lifecycleScope.launch {
                    val succeed = viewModel.leaveFeeltalk()
                    if (succeed) {
                        logOut()
                    } else {
                        Toast.makeText(requireContext(), "탈퇴에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    private fun logOut() = lifecycleScope.launch {
        tryGoogleLogOut()
        tryKakaoLogOut()
        tryNaverLogOut()
        restartApplication()
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

    private fun restartApplication() {
        val pm = requireContext().packageManager
        val intent = pm.getLaunchIntentForPackage(requireContext().packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        requireContext().startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }



    private fun collectUserInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.userInfo.collectLatest {
                binding.tvMyName.setText(it.nickname)
                binding.tvMyBirthDate.setText(if (it.birth == "") "(생일 미등록)" else it.birth)
                binding.tvMyEmail.setText(it.email)
            }
        }
    }

    private fun collectPartnerInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.partnerInfo.collectLatest {
//                binding.textPartnerName.setText(it.nickname)
                binding.textPartnerBirthDate.text = if (it.birth == "") "(생일 미등록)" else it.birth
                binding.tvPartnerEmail.setText(it.email)
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

                binding.tvCoupleAnniversary.text = anniversary
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

    private fun collectPartnerProfileImageUrl() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                viewModel.partnerProfileImageUrl.collectLatest {
                    binding.ivPartnerProfile.setProfileImageUrl(it)
                }
            }
        }
    }

    private fun collectIsLoading() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.isLoading.collectLatest {
                if (it) {
                    loadingDialog.show()
                } else {
                    loadingDialog.dismiss()
                }
            }
        }
    }


    private fun initEditInfoDialogs() = binding.apply {
        tvMyName.setOnClickListener {
            showEditNicknameDialog { nickname, dialog ->
                lifecycleScope.launch {
                    val isSuccessful = viewModel.updateNickname(nickname)
                    if (isSuccessful) {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "닉네임을 변경했습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "닉네임 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvMyBirthDate.setOnClickListener {
            showEditBirthDialog { birth, dialog ->
                lifecycleScope.launch {
                    val isSuccessful = viewModel.updateBirth(birth)
                    if (isSuccessful) {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "생일을 변경했습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "생일 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvCoupleAnniversary.setOnClickListener {
            showEditCoupleAnniversaryDialog { birth, dialog ->
                lifecycleScope.launch {
                    val isSuccessful = viewModel.updateCoupleAnniversary(birth)
                    if (isSuccessful) {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "사귄 첫날을 변경했습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "사귄 첫날 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
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


    private fun updateProfileImage() {
        val mimeTypes = arrayOf("image/*")

        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }

        imageLauncher.launch(intent)
    }

    private val imageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val intent = it.data ?: return@registerForActivityResult
        handleImageIntent(intent)
    }

    private fun handleImageIntent(intent: Intent) = lifecycleScope.launch(Dispatchers.IO) {
        val uri = intent.data ?: return@launch
        viewModel.setLoading(true)

        val image = uri.toBitmap(requireContext())
        if (image == null) {
            Toast.makeText(requireContext(), "프로필 이미지 로딩에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return@launch
        }

        val isSuccessful = viewModel.updateProfileImage(image)
        if (!isSuccessful) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "프로필 이미지 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.setLoading(false)
    }


    private fun navigateToCoupleRegistrationPage() {
        findNavController()
            .navigate(R.id.action_coupleSettingFragment_to_coupleRegistrationFragment)
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