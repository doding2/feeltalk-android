package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat2
import com.clonect.feeltalk.domain.model.data.question.Question2
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import com.clonect.feeltalk.domain.repository.ChatRepository2
import com.clonect.feeltalk.domain.repository.QuestionRepository2
import com.clonect.feeltalk.domain.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.*

class SendQuestionAnswerUseCase(
    private val userRepository: UserRepository,
    private val questionRepository2: QuestionRepository2,
    private val chatRepository2: ChatRepository2,
) {
    suspend operator fun invoke(question2: Question2): Resource<SendQuestionDto> {
        val accessTokenResult = userRepository.getAccessToken()
        if (accessTokenResult is Resource.Error) {
            return Resource.Error(accessTokenResult.throwable)
        }

        val accessToken = (accessTokenResult as? Resource.Success<String>)?.data
            ?: return Resource.Error(Exception("Unexpected Error Occurred."))
        val questionAnswerResult = questionRepository2.sendQuestionAnswer(accessToken, question2)
        if (questionAnswerResult is Resource.Error) {
            return Resource.Error(questionAnswerResult.throwable)
        }

        val chat2 = Chat2(
            question = question2.question,
            owner = "mine",
            message = question2.myAnswer ?: "",
            date = question2.myAnswerDate
                ?: run {
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = format.format(Date())
                    date
                },
            isAnswer = true
        )
        chatRepository2.saveChat(chat2)

        return questionAnswerResult
    }
}