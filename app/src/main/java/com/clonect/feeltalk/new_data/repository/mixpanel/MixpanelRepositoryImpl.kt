package com.clonect.feeltalk.new_data.repository.mixpanel

import com.clonect.feeltalk.new_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSource.MixpanelCacheDataSource
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSource.MixpanelLocalDataSource
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSource.MixpanelRemoteDataSource
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by doding2 on 2024/01/12.
 */
class MixpanelRepositoryImpl(
    private val cacheDataSource: MixpanelCacheDataSource,
    private val localDataSource: MixpanelLocalDataSource,
    private val remoteDataSource: MixpanelRemoteDataSource,
) : MixpanelRepository {

    override suspend fun logIn(id: Long) {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.identify("$id", true)
    }

    override suspend fun navigatePage() {
        val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var (date, count) = cacheDataSource.getPageNavigationCount()
            ?: localDataSource.getPageNavigationCount()
            ?: (now to 0L)
        if (date < now) {
            date = now
            count = 1
        } else {
            count++
        }
        cacheDataSource.savePageNavigationCount(date, count)
        localDataSource.savePageNavigationCount(date, count)

        if (count == 3L) {
            localDataSource.saveUserActiveDate(date)
        }

        val isActive = count >= 3L || localDataSource.getUserActiveDate() == date
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.registerSuperProperties(JSONObject().apply {
            put("isActive", isActive)
        })
    }

    override suspend fun setInChatSheet(isInChat: Boolean) {
        if (isInChat) {
            cacheDataSource.startChatTimer()
        } else {
            cacheDataSource.cancelChatTimer()
        }
    }

    override suspend fun setInQuestionPage(isInQuestion: Boolean) {
        if (isInQuestion) {
            cacheDataSource.startQuestionTimer()
        } else {
            cacheDataSource.cancelQuestionTimer()
        }
    }

    override suspend fun answerQuestion() {
        val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        localDataSource.saveUserActiveDate(now)

        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.registerSuperProperties(JSONObject().apply {
            put("isActive", true)
        })
    }

}