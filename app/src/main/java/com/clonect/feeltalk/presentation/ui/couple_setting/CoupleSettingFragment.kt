package com.clonect.feeltalk.presentation.ui.couple_setting

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
import com.clonect.feeltalk.presentation.utils.makeLoadingDialog
import com.clonect.feeltalk.presentation.utils.showBreakUpCoupleDialog
import com.clonect.feeltalk.presentation.utils.toBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
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

        initEditTexts()

        collectUserInfo()
        collectPartnerInfo()
        collectMyProfileImageUrl()
        collectPartnerProfileImageUrl()
        collectCoupleAnniversary()
        collectIsLoading()

        binding.apply {
            btnBack.setOnClickListener { onBackCallback.handleOnBackPressed() }

            ivMyProfile.setOnClickListener { updateProfileImage() }
            
            clPartnerProfile.setOnClickListener { 
                showBreakUpCoupleDialog(
                    partnerNickname = viewModel.partnerInfo.value.nickname,
                    onConfirm = {
                        lifecycleScope.launch {
                            val isSuccessful = viewModel.breakUpCouple()
                            if (isSuccessful) {
                                Toast.makeText(requireContext(), "커플이 해제되었습니다", Toast.LENGTH_SHORT).show()
                                navigateToCoupleRegistrationPage()
                            } else {
                                Toast.makeText(requireContext(), "실패했습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }

    private fun collectUserInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.userInfo.collectLatest {
                binding.etMyName.setText(it.nickname)
                binding.metMyBirthDate.setText(it.birth)
            }
        }
    }

    private fun collectPartnerInfo() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.partnerInfo.collectLatest {
                binding.textPartnerName.setText(it.nickname)
                binding.textPartnerBirthDate.text = it.birth
            }
        }
    }

    private fun collectCoupleAnniversary() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.coupleAnniversary.collectLatest {
                binding.metCoupleAnniversary.setText(it?.replace("/", ". "))
                if (it != null) {
                    binding.textDDayValue.text = calculateDDay(it)
                }
                if (it.isNullOrBlank()) {
                    binding.metCoupleAnniversary.setText("0000/00/00")
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


    private fun initEditTexts() = binding.apply {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        dateFormat.isLenient = false

        etMyName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !etMyName.text.isNullOrBlank()) {
                lifecycleScope.launch {
                    val isSuccessful = viewModel.updateNickname(etMyName.text.toString())
                    if (isSuccessful) {
                        etMyName.clearFocus()
                        etMyName.hideKeyboard()
                        Toast.makeText(requireContext(), "닉네임을 변경했습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "닉네임 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
                return@setOnEditorActionListener true
            }
            false
        }

        metMyBirthDate.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !metMyBirthDate.text.isNullOrBlank()) {
                lifecycleScope.launch {
                    val dateString = metMyBirthDate.masked.replace(". ", "/")
                    try {
                        dateFormat.parse(dateString)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "존재하지 않는 날짜입니다", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val isSuccessful = viewModel.updateBirth(dateString)
                    if (isSuccessful) {
                        metMyBirthDate.clearFocus()
                        metMyBirthDate.hideKeyboard()
                        Toast.makeText(requireContext(), "생일을 변경했습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "생일 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
                return@setOnEditorActionListener true
            }
            false
        }

        metCoupleAnniversary.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !metCoupleAnniversary.text.isNullOrBlank()) {
                lifecycleScope.launch {
                    val dateString = metCoupleAnniversary.masked.replace(". ", "/")
                    try {
                        dateFormat.parse(dateString)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "존재하지 않는 날짜입니다", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val isSuccessful = viewModel.updateCoupleAnniversary(dateString)
                    if (isSuccessful) {
                        metCoupleAnniversary.clearFocus()
                        metCoupleAnniversary.hideKeyboard()
                        Toast.makeText(requireContext(), "사귄 첫 날을 변경했습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "사귄 첫 날 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun View.hideKeyboard() {
        val imm: InputMethodManager? = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(this.windowToken, 0)
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