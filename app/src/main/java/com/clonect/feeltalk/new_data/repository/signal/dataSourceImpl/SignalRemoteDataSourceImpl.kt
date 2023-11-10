package com.clonect.feeltalk.new_data.repository.signal.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.common.FeelTalkException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.signal.dataSource.SignalRemoteDataSource
import com.clonect.feeltalk.new_domain.model.signal.ChangeMySignalResponse
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.model.signal.SignalResponse
import com.google.gson.JsonObject

/**
 * Created by doding2 on 2023/11/10.
 */
class SignalRemoteDataSourceImpl(
    private val clonectService: ClonectService
) : SignalRemoteDataSource {
    override suspend fun getMySignal(accessToken: String): SignalResponse {
        val response = clonectService.getMySignal("Bearer $accessToken")
        if (!response.isSuccessful) throw FeelTalkException.ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getPartnerSignal(accessToken: String): SignalResponse {
        val response = clonectService.getPartnerSignal("Bearer $accessToken")
        if (!response.isSuccessful) throw FeelTalkException.ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun changeMySignal(accessToken: String, signal: Signal): ChangeMySignalResponse {
        val body = JsonObject().apply {
            addProperty("mySignal", when (signal) {
                Signal.Zero -> 1
                Signal.Quarter -> 2
                Signal.Half -> 3
                Signal.ThreeFourth -> 4
                Signal.One -> 5
            })
        }
        val response = clonectService.changeMySignal("Bearer $accessToken", body)
        if (!response.isSuccessful) throw FeelTalkException.ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

}