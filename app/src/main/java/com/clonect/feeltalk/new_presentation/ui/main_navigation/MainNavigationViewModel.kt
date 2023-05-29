package com.clonect.feeltalk.new_presentation.ui.main_navigation

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(

): ViewModel() {

    private val _questionFragmentScrollState = MutableStateFlow<Parcelable?>(null)
    val questionFragmentScrollState = _questionFragmentScrollState.asStateFlow()

    fun setQuestionFragmentScrollState(state: Parcelable?) {
        _questionFragmentScrollState.value = state
    }
}