package com.clonect.feeltalk.new_data.repository.mixpanel.dataSourceImpl

import android.content.Context
import android.os.CountDownTimer
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.plusSecondsBy
import com.clonect.feeltalk.data.mapper.toStringLowercase
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSource.MixpanelCacheDataSource
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

/**
 * Created by doding2 on 2024/01/12.
 */
class MixpanelCacheDataSourceImpl(
    context: Context
) : MixpanelCacheDataSource {

    private val mixpanel: MixpanelAPI by lazy {
        val token = if (BuildConfig.DEBUG) Constants.MIXPANEL_DEBUG_TOKEN else Constants.MIXPANEL_RELEASE_TOKEN
        MixpanelAPI.getInstance(context, token, true)
    }

    private var chatTimer: Timer? = null
    private var questionTimer: Timer? = null

    private var pageCountPair: Pair<String, Long>? = null


    override fun getMixpanelInstance() = mixpanel

    override fun startChatTimer() {
        chatTimer?.cancel()
        chatTimer = Timer()
        chatTimer?.schedule(300000) {
            val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
            mixpanel.track("Session Chat", JSONObject().apply {
                put("ChatDate", now)
            })
            chatTimer = null
        }
    }

    override fun cancelChatTimer() {
        chatTimer?.cancel()
    }

    override fun startQuestionTimer() {
        questionTimer?.cancel()
        questionTimer = Timer()
        questionTimer?.schedule(300000) {
            val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
            mixpanel.track("Session Question", JSONObject().apply {
                put("QuestionDate", now)
            })
            questionTimer = null
        }
    }

    override fun cancelQuestionTimer() {
        questionTimer?.cancel()
    }

    override suspend fun savePageNavigationCount(date: String, count: Long) {
        pageCountPair = date to count
    }
    override suspend fun getPageNavigationCount() = pageCountPair

}