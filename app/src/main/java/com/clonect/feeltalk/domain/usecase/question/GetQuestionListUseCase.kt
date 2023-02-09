package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetQuestionListUseCase {
    operator fun invoke(): Flow<Resource<List<Question>>> = flow {
        val testList = mutableListOf(
            Question(id = 0, contentPrefix = "놀랍게도 ", content = "둘 다 답변 등록 안 함 1", contentSuffix = " ! ! !", myAnswer = "", partnerAnswer = ""),
            Question(id = 1, contentPrefix = "아마도 ", content = "둘 다 답변 등록 안 함 2", contentSuffix = " ㄷ ㄷ ㄷ", myAnswer = "", partnerAnswer = ""),
            Question(id = 2, contentPrefix = "놀랍게도 ", content = "상대만 답변 등록 함 1", contentSuffix = " ! ! !", myAnswer = "", partnerAnswer = "상대방 답변 3"),
            Question(id = 3, contentPrefix = "아마도 ", content = "상대만 답변 등록 함 2", contentSuffix = " ㄷ ㄷ ㄷ", myAnswer = "", partnerAnswer = "상대방 답변 4"),
            Question(id = 4, contentPrefix = "허걱 ", content = "상대만 답변 등록 함 3", contentSuffix = " ; ; ;", myAnswer = "", partnerAnswer = "상대방 답변 5"),
            Question(id = 5, contentPrefix = "놀랍게도 ", content = "나만 답변 등록 함 1", contentSuffix = " ! ! !", myAnswer = "내 답변 6", partnerAnswer = ""),
            Question(id = 6, contentPrefix = "아마도 ", content = "나만 답변 등록 함 2", contentSuffix = " ㄷ ㄷ ㄷ", myAnswer = "내 답변 7", partnerAnswer = ""),
            Question(id = 7, contentPrefix = "허걱 ", content = "나만 답변 등록 함 3", contentSuffix = " ; ; ;", myAnswer = "내 답변 8", partnerAnswer = ""),
            Question(id = 8, contentPrefix = "놀랍게도 ", content = "둘 다 답변 등록 함 1", contentSuffix = " ! ! !", myAnswer = "내 답변 9", partnerAnswer = "상대방 답변 9"),
            Question(id = 9, contentPrefix = "아마도 ", content = "둘 다 답변 등록 함 2", contentSuffix = " ㄷ ㄷ ㄷ", myAnswer = "내 답변 10", partnerAnswer = "상대방 답변 10"),
            Question(id = 10, contentPrefix = "허걱 ", content = "둘 다 답변 등록 함 3", contentSuffix = " ; ; ;", myAnswer = "내 답변 11", partnerAnswer = "상대방 답변 11"),
        )

        emit(Resource.Success(testList))
    }
}