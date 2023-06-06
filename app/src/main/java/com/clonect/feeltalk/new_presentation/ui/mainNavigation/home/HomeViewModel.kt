package com.clonect.feeltalk.new_presentation.ui.mainNavigation.home

import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.new_domain.model.signal.Signal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : ViewModel() {

    private val _questionCount = MutableStateFlow(100)
    val questionCount = _questionCount.asStateFlow()

    private val _mySignal = MutableStateFlow(Signal.Seduce)
    val mySignal = _mySignal.asStateFlow()

    private val _partnerSignal = MutableStateFlow(Signal.Seduce)
    val partnerSignal = _partnerSignal.asStateFlow()

    fun setMySignal(signal: Signal) {
        _mySignal.value = signal
    }

    fun setPartnerSignal(signal: Signal) {
        _partnerSignal.value = signal
    }
}