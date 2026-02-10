package com.clonect.feeltalk.mvp_presentation.ui.couple_setting

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.user.UserInfo
import com.clonect.feeltalk.mvp_domain.usecase.user.*
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CoupleSettingViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPartnerInfo2UseCase: GetPartnerInfo2UseCase,
    private val getCoupleAnniversaryUseCase: GetCoupleAnniversaryUseCase,
    private val breakUpCoupleUseCase2: BreakUpCoupleUseCase2,
    private val clearCoupleInfoUseCase: ClearCoupleInfoUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val getMyProfileImageUrlUseCase: GetMyProfileImageUrlUseCase,
    private val getPartnerProfileImageUrlUseCase: GetPartnerProfileImageUrlUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase,
    private val updateBirthUseCase: UpdateBirthUseCase,
    private val updateCoupleAnniversaryUseCase: UpdateCoupleAnniversaryUseCase,
    private val leaveFeeltalkUseCase: LeaveFeeltalkUseCase
): ViewModel() {

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo = _userInfo.asStateFlow()

    private val _partnerInfo = MutableStateFlow(UserInfo())
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _myProfileImageUrl = MutableStateFlow<String?>(null)
    val myProfileImageUrl = _myProfileImageUrl.asStateFlow()

    private val _partnerProfileImageUrl = MutableStateFlow<String?>(null)
    val partnerProfileImageUrl = _partnerProfileImageUrl.asStateFlow()

    private val _coupleAnniversary = MutableStateFlow<String?>(null)
    val coupleAnniversary = _coupleAnniversary.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        getUserInfo()
        getPartnerInfo()
        getMyProfileImageUrl()
        getPartnerProfileImageUrl()
        getCoupleAnniversary()
    }


    suspend fun updateNickname(nickname: String) = withContext(Dispatchers.IO) {
        if (nickname == _userInfo.value.nickname) return@withContext true

        val result = updateNicknameUseCase(nickname)
        return@withContext when (result) {
            is Resource.Success -> {
                _userInfo.value = _userInfo.value.copy(nickname = nickname)
                true
            }
            else -> {
                infoLog("Fail to update nickname")
                false
            }
        }
    }

    suspend fun updateBirth(birth: String) = withContext(Dispatchers.IO) {
        if (birth == _userInfo.value.birth) return@withContext true

        val result = updateBirthUseCase(birth)
        return@withContext when (result) {
            is Resource.Success -> {
                _userInfo.value = _userInfo.value.copy(birth = birth)
                true
            }
            else -> {
                infoLog("Fail to update birth")
                false
            }
        }
    }

    suspend fun updateCoupleAnniversary(coupleAnniversary: String) = withContext(Dispatchers.IO) {
        if (coupleAnniversary == _coupleAnniversary.value) return@withContext true

        val result = updateCoupleAnniversaryUseCase(coupleAnniversary)
        return@withContext when (result) {
            is Resource.Success -> {
                _coupleAnniversary.value = coupleAnniversary
                true
            }
            else -> {
                infoLog("Fail to update nickname")
                false
            }
        }
    }


    suspend fun updateProfileImage(image: Bitmap) = withContext(Dispatchers.IO) {
        val result = updateProfileImageUseCase(image)
        return@withContext when (result) {
            is Resource.Success -> {
                val profileUrl = result.data.url
                _myProfileImageUrl.value = profileUrl
                true
            }
            is Resource.Error -> {
                infoLog("Fail to update profile image: ${result.throwable.localizedMessage}")
                false
            }
            else -> {
                infoLog("Fail to update profile image")
                false
            }
        }
    }

    private fun getUserInfo() = viewModelScope.launch(Dispatchers.IO) {
        when (val result = getUserInfoUseCase()) {
            is Resource.Success -> _userInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get user info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get user info")
        }
    }

    private fun getPartnerInfo() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPartnerInfo2UseCase()
        when (result) {
            is Resource.Success -> _partnerInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get partner info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner info")
        }
    }

    private fun getMyProfileImageUrl() = viewModelScope.launch(Dispatchers.IO) {
        val result = getMyProfileImageUrlUseCase()
        when (result) {
            is Resource.Success -> { _myProfileImageUrl.value = result.data }
            is Resource.Error -> infoLog("Fail to get my profile image url: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get my profile image url")
        }
    }

    private fun getPartnerProfileImageUrl() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPartnerProfileImageUrlUseCase()
        when (result) {
            is Resource.Success -> { _partnerProfileImageUrl.value = result.data }
            is Resource.Error -> infoLog("Fail to get partner profile image url: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner profile image url")
        }
    }

    private fun getCoupleAnniversary() = viewModelScope.launch(Dispatchers.IO) {
        val result = getCoupleAnniversaryUseCase()
        when (result) {
            is Resource.Success -> {
                _coupleAnniversary.value = result.data
            }
            is Resource.Error -> infoLog("Fail to get d day: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner d day")
        }
    }


    suspend fun leaveFeeltalk() = withContext(Dispatchers.IO) {
        val result = leaveFeeltalkUseCase()
        when (result) {
            is Resource.Success -> {
                result.data
            }
            is Resource.Error -> {
                infoLog("Fail to leave feeltalk: ${result.throwable.localizedMessage}")
                false
            }
            else -> {
                infoLog("Fail to leave feeltalk")
                false
            }
        }
    }


    suspend fun breakUpCouple() = withContext(Dispatchers.IO) {
        val result = breakUpCoupleUseCase2()

        when (result) {
            is Resource.Success -> {
                clearCoupleInfoUseCase()
                infoLog("Success to break up couple: ${result.data}")
                true
            }
            is Resource.Error -> {
                infoLog("Fail to break up couple: ${result.throwable.localizedMessage}")
                false
            }
            else -> false
        }
    }


    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

}