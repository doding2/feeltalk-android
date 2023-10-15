package com.clonect.feeltalk.new_presentation.ui.mainNavigation.home.signal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Point
import com.clonect.feeltalk.new_domain.model.signal.Signal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignalViewModel @Inject constructor(

) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _signal = MutableStateFlow<Signal?>(null)
    val signal = _signal.asStateFlow()


    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }


    fun setSignal(signal: Signal) {
        _signal.value = signal
    }

    fun sendSignal(onComplete: (Signal) -> Unit) = viewModelScope.launch {
        val signal = signal.value ?: return@launch
        setLoading(true)
        onComplete(signal)
        setLoading(false)
    }



    private val _centerPoint = MutableStateFlow<Point<Float>?>(null)
    val centerPoint = _centerPoint.asStateFlow()

    private val _diameter = MutableStateFlow<Float?>(null)
    val diameter = _diameter.asStateFlow()

    private val _angle = MutableStateFlow<Float?>(null)
    val angle = _angle.asStateFlow()


    fun setCenterPoint(point: Point<Float>) = viewModelScope.launch {
        _centerPoint.value = point
    }

    fun setDiameter(diameter: Float) = viewModelScope.launch {
        _diameter.value = diameter
    }

    fun setAngle(angle: Float) = viewModelScope.launch {
        _angle.value = angle
    }
}