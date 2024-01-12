package com.clonect.feeltalk.new_domain.repository.mixpanel

/**
 * Created by doding2 on 2024/01/12.
 */
interface MixpanelRepository {
    suspend fun logIn(id: Long)
    suspend fun navigatePage()
    suspend fun setInChatSheet(isInChat: Boolean)
    suspend fun setInQuestionPage(isInQuestion: Boolean)
    suspend fun answerQuestion()
}