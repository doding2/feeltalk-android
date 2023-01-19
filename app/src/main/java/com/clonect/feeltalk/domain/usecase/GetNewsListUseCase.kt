package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.news.News
import com.clonect.feeltalk.domain.model.news.NewsType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetNewsListUseCase() {

    operator fun invoke(): Flow<Resource<List<News>>> = flow {
        val testList = mutableListOf(
            News(0, "Daniel", "님과 커플이 되었습니다.", "1일 전", NewsType.Official),
            News(1, "Jenny", "님께 1번째 질문이 도착했습니다.", "5시간 전", NewsType.News),
            News(2, "Daniel", "님의 메시지가 도착했습니다 :\n오늘은 몇시에 퇴근하세요?", "3시간 전", NewsType.Chat),
            News(3, "Daniel", "님께서 2번째 질문의 확인하였습니다.", "1시간 전", NewsType.News),
            News(4, "Daniel", "님께서 답변을 등록하셨습니다.", "22분 전", NewsType.News)
        )

        emit(Resource.Success(testList))
    }

}