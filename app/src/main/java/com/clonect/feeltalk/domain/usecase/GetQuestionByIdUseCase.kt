package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.question.Question

class GetQuestionByIdUseCase {
    operator fun invoke(id: Long): Resource<Question> {
        return Resource.Success(Question(
            id = 0,
            contentPrefix = "임시로 생성된",
            content = "테스트 질문",
            contentSuffix = "입니다.")
        )
    }
}