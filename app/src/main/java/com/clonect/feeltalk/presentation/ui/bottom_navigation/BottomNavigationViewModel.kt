package com.clonect.feeltalk.presentation.ui.bottom_navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.domain.usecase.GetFcmTokenUseCase
import com.clonect.feeltalk.domain.usecase.SaveFcmTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomNavigationViewModel @Inject constructor(
    private val saveFcmTokenUseCase: SaveFcmTokenUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase
): ViewModel() {

    fun saveFcmToken(fcmToken: String) = viewModelScope.launch(Dispatchers.IO) {
        saveFcmTokenUseCase(fcmToken)
    }

}