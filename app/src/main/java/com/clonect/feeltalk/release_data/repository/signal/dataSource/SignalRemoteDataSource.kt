package com.clonect.feeltalk.release_data.repository.signal.dataSource

import com.clonect.feeltalk.release_domain.model.signal.ChangeMySignalResponse
import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_domain.model.signal.MySignalResponse
import com.clonect.feeltalk.release_domain.model.signal.PartnerSignalResponse

/**
 * Created by doding2 on 2023/11/10.
 */
interface SignalRemoteDataSource {
    suspend fun getMySignal(accessToken: String): MySignalResponse
    suspend fun getPartnerSignal(accessToken: String): PartnerSignalResponse
    suspend fun changeMySignal(accessToken: String, signal: Signal): ChangeMySignalResponse
}