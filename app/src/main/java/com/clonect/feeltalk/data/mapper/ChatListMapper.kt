package com.clonect.feeltalk.data.mapper

import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.dto.chat.ChatListItemDto

suspend fun List<ChatListItemDto>.toChatList(
    accessToken: String,
    questionString: String,
    userLevelEncryptHelper: UserLevelEncryptHelper,
): List<Chat> {
    var isMyAnswerExist = false
    var isPartnerAnswerExist = false

    return mapIndexed { index, itemDto ->
        val isMine = accessToken == itemDto.accessToken
        val owner = if (isMine) "mine" else "partner"
        val message = userLevelEncryptHelper.run {
            if (isMine) decryptMyText(itemDto.message)
            else decryptPartnerText(itemDto.message)
        }
        val date = itemDto.time
            .replace("T", "/")
            .replace(":", "/")
            .replace("-", "/")

        var isAnswer = false
        if (!isMyAnswerExist && isMine) {
            isMyAnswerExist = true
            isAnswer = true
        }
        if (!isPartnerAnswerExist && !isMine) {
            isPartnerAnswerExist = true
            isAnswer = true
        }

        Chat(
            id = index.toLong(),
            question = questionString,
            owner = owner,
            message = message,
            date = date,
            isAnswer = isAnswer
        )
    }
}