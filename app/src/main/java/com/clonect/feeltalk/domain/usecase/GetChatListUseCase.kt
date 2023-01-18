package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.chat.Chat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetChatListUseCase {
    operator fun invoke(): Flow<Resource<List<Chat>>> {
        return flow {
            emit(Resource.Success(emptyList()))
        }
    }
}