package com.clonect.feeltalk.new_presentation.ui.mainNavigation.home.signal

import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.new_domain.model.signal.Signal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignalViewModel @Inject constructor(

) : ViewModel() {

    private val _currentSignal = MutableStateFlow<Signal?>(null)
    val currentSignal = _currentSignal.asStateFlow()

    private val _selectedSignal = MutableStateFlow<Signal?>(null)
    val selectedSignal = _selectedSignal.asStateFlow()


    fun setCurrentSignal(signal: Signal) {
        _currentSignal.value = signal
    }

    fun setSelectedSignal(signal: Signal) {
        _selectedSignal.value = signal
    }

}