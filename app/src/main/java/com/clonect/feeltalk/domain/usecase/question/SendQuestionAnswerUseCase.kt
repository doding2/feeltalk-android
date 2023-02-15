package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import com.clonect.feeltalk.domain.repository.ChatRepository
import com.clonect.feeltalk.domain.repository.QuestionRepository
import com.clonect.feeltalk.domain.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.*

class SendQuestionAnswerUseCase(
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(question: Question): Resource<SendQuestionDto> {
        val accessTokenResult = userRepository.getAccessToken()
        if (accessTokenResult is Resource.Error) {
            return Resource.Error(accessTokenResult.throwable)
        }

        val accessToken = (accessTokenResult as? Resource.Success<String>)?.data
            ?: return Resource.Error(Exception("Unexpected Error Occurred."))
        val questionAnswerResult = questionRepository.sendQuestionAnswer(accessToken, question)
        if (questionAnswerResult is Resource.Error) {
            return Resource.Error(questionAnswerResult.throwable)
        }

        val chat = Chat(
            question = question.question,
            owner = "mine",
            message = question.myAnswer ?: "",
            date = question.myAnswerDate
                ?: run {
                    val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
                    val date = format.format(Date())
                    date
                },
            isAnswer = true
        )
        chatRepository.saveChat(chat)

        return questionAnswerResult
    }
}