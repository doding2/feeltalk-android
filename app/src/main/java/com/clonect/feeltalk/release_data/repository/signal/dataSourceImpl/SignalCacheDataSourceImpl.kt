package com.clonect.feeltalk.release_data.repository.signal.dataSourceImpl

import com.clonect.feeltalk.release_data.repository.signal.dataSource.SignalCacheDataSource
import com.clonect.feeltalk.release_domain.model.signal.Signal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by doding2 on 2023/11/10.
 */
class SignalCacheDataSourceImpl : SignalCacheDataSource {

    private var mySignal = MutableStateFlow<Signal?>(null)
    private var partnerSignalFlow = MutableStateFlow<Signal?>(null)

    override fun saveMySignal(signal: Signal) {
        mySignal.value = signal
    }
    override fun getMySignal(): Signal? = mySignal.value
    override suspend fun getMySignalFlow(): Flow<Signal?> = mySignal.asStateFlow()

    override fun savePartnerSignal(signal: Signal) {
        partnerSignalFlow.value = signal
    }
    override fun getPartnerSignal(): Signal? = partnerSignalFlow.value
    override suspend fun getPartnerSignalFlow(): Flow<Signal?> = partnerSignalFlow.asStateFlow()
}