package com.clonect.feeltalk.release_data.repository.mixpanel.dataSource

import com.mixpanel.android.mpmetrics.MixpanelAPI

/**
 * Created by doding2 on 2024/01/12.
 */
interface MixpanelCacheDataSource {

    /* p0 */

    fun getMixpanelInstance(): MixpanelAPI

    fun startChatTimer()
    fun cancelChatTimer()

    fun startQuestionTimer()
    fun cancelQuestionTimer()

    fun startAnswerTimer()
    fun cancelAnswerTimer()

    suspend fun savePageNavigationCount(date: String, count: Long)
    suspend fun getPageNavigationCount(): Pair<String, Long>?


    /* p1 */

    fun startContentShareTimer()
    fun cancelContentShareTimer()
}