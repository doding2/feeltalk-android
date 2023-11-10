package com.clonect.feeltalk.new_data.repository.signal.dataSource

import com.clonect.feeltalk.new_domain.model.signal.ChangeMySignalResponse
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.model.signal.SignalResponse

/**
 * Created by doding2 on 2023/11/10.
 */
interface SignalRemoteDataSource {
    suspend fun getMySignal(accessToken: String): SignalResponse
    suspend fun getPartnerSignal(accessToken: String): SignalResponse
    suspend fun changeMySignal(accessToken: String, signal: Signal): ChangeMySignalResponse
}