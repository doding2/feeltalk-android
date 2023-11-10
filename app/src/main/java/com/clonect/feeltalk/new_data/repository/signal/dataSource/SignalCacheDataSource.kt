package com.clonect.feeltalk.new_data.repository.signal.dataSource

import com.clonect.feeltalk.new_domain.model.signal.Signal
import kotlinx.coroutines.flow.Flow

/**
 * Created by doding2 on 2023/11/10.
 */
interface SignalCacheDataSource {
    fun saveMySignal(signal: Signal)
    fun getMySignal(): Signal?

    fun savePartnerSignal(signal: Signal)
    fun getPartnerSignal(): Signal?
    suspend fun getPartnerSignalFlow(): Flow<Signal?>
}