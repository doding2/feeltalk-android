package com.clonect.feeltalk.release_data.repository.partner

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_data.mapper.toPartnerInfo
import com.clonect.feeltalk.release_data.repository.partner.dataSource.PartnerCacheDataSource
import com.clonect.feeltalk.release_data.repository.partner.dataSource.PartnerLocalDataSource
import com.clonect.feeltalk.release_data.repository.partner.dataSource.PartnerRemoteDataSource
import com.clonect.feeltalk.release_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.release_domain.repository.partner.PartnerRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf

/**
 * Created by doding2 on 2023/09/27.
 */
class PartnerRepositoryImpl(
    private val cacheDataSource: PartnerCacheDataSource,
    private val localDataSource: PartnerLocalDataSource,
    private val remoteDataSource: PartnerRemoteDataSource
 ): PartnerRepository {

    override suspend fun getPartnerInfo(accessToken: String): Resource<PartnerInfo> {
        try {
            val cache = cacheDataSource.getPartnerInfo()
            if (cache != null) return Resource.Success(cache)

            val local = localDataSource.getPartnerInfo()
            if (local != null) {
                cacheDataSource.savePartnerInfo(local)
                return Resource.Success(local)
            }

            val remote = remoteDataSource.getPartnerInfo(accessToken).toPartnerInfo()
            cacheDataSource.savePartnerInfo(remote)
            localDataSource.savePartnerInfo(remote)
            return Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun getPartnerInfoFlow(accessToken: String): Flow<Resource<PartnerInfo>> {
        val cacheFlow = channelFlow {
            val cache = cacheDataSource.getPartnerInfo()
            if (cache != null) {
                send(Resource.Success(cache))
            }
        }
        val localFlow = channelFlow {
            val local = localDataSource.getPartnerInfo()
            if (local != null) {
                cacheDataSource.savePartnerInfo(local)
                send(Resource.Success(local))
            }
        }
        val remoteFlow = channelFlow {
            try {
                val remote = remoteDataSource.getPartnerInfo(accessToken).toPartnerInfo()
                localDataSource.savePartnerInfo(remote)
                cacheDataSource.savePartnerInfo(remote)
                send(Resource.Success(remote))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                send(Resource.Error(e))
            }
        }
        return flowOf(cacheFlow, localFlow, remoteFlow).flattenMerge()
    }

}