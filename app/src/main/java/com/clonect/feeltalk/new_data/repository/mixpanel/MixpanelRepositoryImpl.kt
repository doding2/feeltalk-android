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

    /* p0 */

    override suspend fun logIn(id: Long) {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.identify("$id", true)

        val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val isActive = localDataSource.getUserActiveDate() == now
        mixpanel.registerSuperProperties(JSONObject().apply {
            put("isActive", isActive)
        })
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

    override suspend fun setInAnswerSheet(isInAnswer: Boolean) {
        if (isInAnswer) {
            cacheDataSource.startAnswerTimer()
        } else {
            cacheDataSource.cancelAnswerTimer()
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




    /* p1 */

    override suspend fun sendChat() {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.track("Send Chat")
    }

    override suspend fun shareContent() {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.track("Click Chat Menu Share Button")
    }

    override suspend fun setInContentShare(isInContentShare: Boolean) {
        if (isInContentShare) {
            cacheDataSource.startContentShareTimer()

            val mixpanel = cacheDataSource.getMixpanelInstance()
            mixpanel.track("Open Chat Menu")
        } else {
            cacheDataSource.cancelContentShareTimer()
        }
    }

    override suspend fun openSignalSheet() {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.track("Open Signal Sheet")
    }

    override suspend fun changeMySignal() {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.track("Change Signal")

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val lastDate = localDataSource.getSignalChangeDate()?.let { formatter.parse(it) }
        val nowDate = Date()
        localDataSource.saveSignalChangeDate(formatter.format(nowDate))
        if (lastDate == null) return

        val diff = nowDate.time - lastDate.time
        val totalSeconds = diff / 1000
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60

        val seconds = totalSeconds % 60
        val minutes = totalMinutes % 60
        val dateTime = "$totalHours:$minutes:$seconds"

        mixpanel.track("Signal Change Interval", JSONObject().apply {
            put("Time_BetweenChangeSignal", dateTime)
        })
    }

    override suspend fun addChallenge() {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.track("Add Challenge")
    }

    override suspend fun completeChallenge() {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.track("Complete Challenge")
    }

    override suspend fun deleteChallenge() {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.track("Delete Challenge")
    }

    override suspend fun openCompletedChallengeDetail() {
        val mixpanel = cacheDataSource.getMixpanelInstance()
        mixpanel.track("Open Completed Challenge Detail")
    }

}