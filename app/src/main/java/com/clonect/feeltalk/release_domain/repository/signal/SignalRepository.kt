package com.clonect.feeltalk.release_domain.repository.signal

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.signal.ChangeMySignalResponse
import com.clonect.feeltalk.release_domain.model.signal.Signal
import kotlinx.coroutines.flow.Flow

/**
 * Created by doding2 on 2023/11/10.
 */
interface SignalRepository {
    suspend fun getMySignal(accessToken: String): Resource<Signal>
    suspend fun getMySignalCacheFlow(): Flow<Signal?>
    suspend fun changeMySignal(accessToken: String, signal: Signal): Resource<ChangeMySignalResponse>

    suspend fun getPartnerSignal(accessToken: String): Resource<Signal>
    suspend fun getPartnerSignalCacheFlow(): Flow<Signal?>
    suspend fun changePartnerSignalCache(signal: Signal)
}