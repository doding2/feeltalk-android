package com.clonect.feeltalk.new_data.repository.signal

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_data.mapper.toSignal
import com.clonect.feeltalk.new_domain.repository.signal.SignalRepository
import com.clonect.feeltalk.new_data.repository.signal.dataSource.SignalCacheDataSource
import com.clonect.feeltalk.new_data.repository.signal.dataSource.SignalLocalDataSource
import com.clonect.feeltalk.new_data.repository.signal.dataSource.SignalRemoteDataSource
import com.clonect.feeltalk.new_domain.model.signal.ChangeMySignalResponse
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.presentation.utils.infoLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

/**
 * Created by doding2 on 2023/11/10.
 */
class SignalRepositoryImpl(
    private val cacheDataSource: SignalCacheDataSource,
    private val localDataSource: SignalLocalDataSource,
    private val remoteDataSource: SignalRemoteDataSource,
) : SignalRepository {
    override suspend fun getMySignal(accessToken: String): Resource<Signal> {
        try {
            val cache = cacheDataSource.getMySignal()
            if (cache != null) {
                return Resource.Success(cache)
            }
            val remote = remoteDataSource.getMySignal(accessToken).toSignal()
            cacheDataSource.saveMySignal(remote)
            return Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun getMySignalCacheFlow(): Flow<Signal?> {
        return cacheDataSource.getMySignalFlow()
    }

    override suspend fun changeMySignal(accessToken: String, signal: Signal): Resource<ChangeMySignalResponse> {
        return try {
            val result = remoteDataSource.changeMySignal(accessToken, signal)
            cacheDataSource.saveMySignal(signal)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }



    override suspend fun getPartnerSignal(accessToken: String): Resource<Signal> {
        try {
            val cache = cacheDataSource.getPartnerSignal()
            if (cache != null) {
                return Resource.Success(cache)
            }
            val result = remoteDataSource.getPartnerSignal(accessToken).toSignal()
            cacheDataSource.savePartnerSignal(result)
            return Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun getPartnerSignalCacheFlow(): Flow<Signal?> {
        return cacheDataSource.getPartnerSignalFlow()
    }

    override suspend fun changePartnerSignalCache(signal: Signal) {
        cacheDataSource.savePartnerSignal(signal)
    }

}