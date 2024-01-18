package com.clonect.feeltalk.new_domain.repository.mixpanel

/**
 * Created by doding2 on 2024/01/12.
 */
interface MixpanelRepository {
    /* p0 */
    suspend fun logIn(id: Long)
    suspend fun navigatePage()
    suspend fun setInChatSheet(isInChat: Boolean)
    suspend fun setInQuestionPage(isInQuestion: Boolean)
    suspend fun setInAnswerSheet(isInAnswer: Boolean)
    suspend fun answerQuestion()

    /* p1 */
    suspend fun sendChat()
    suspend fun shareContent()
    suspend fun setInContentShare(isInContentShare: Boolean)
    suspend fun openSignalSheet()
    suspend fun changeMySignal()
    suspend fun addChallenge()
    suspend fun completeChallenge()
    suspend fun deleteChallenge()
    suspend fun openCompletedChallengeDetail()

}