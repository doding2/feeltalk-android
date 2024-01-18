package com.clonect.feeltalk.new_data.repository.mixpanel.dataSource

/**
 * Created by doding2 on 2024/01/12.
 */
interface MixpanelLocalDataSource {

    /* p0 */

    suspend fun saveUserActiveDate(date: String)
    suspend fun getUserActiveDate(): String?

    suspend fun savePageNavigationCount(date: String, count: Long)
    suspend fun getPageNavigationCount(): Pair<String, Long>?


    /* p1 */

    suspend fun saveSignalChangeDate(date: String)
    suspend fun getSignalChangeDate(): String?
}