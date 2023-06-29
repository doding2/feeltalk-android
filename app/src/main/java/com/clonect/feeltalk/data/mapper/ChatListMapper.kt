package com.clonect.feeltalk.data.mapper

import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.model.data.chat.Chat2
import com.clonect.feeltalk.domain.model.dto.chat.ChatListItemDto2

suspend fun List<ChatListItemDto2>.toChatList(
    accessToken: String,
    questionString: String,
    userLevelEncryptHelper: UserLevelEncryptHelper,
): List<Chat2> {
    var isMyAnswerExist = false
    var isPartnerAnswerExist = false

    return mapIndexed { index, itemDto ->
//        val id = (index + 1).toLong()
        val isMine = accessToken == itemDto.accessToken
        val owner = if (isMine) "mine" else "partner"
        val message = userLevelEncryptHelper.run {
            if (isMine) decryptMyText(itemDto.message)
            else decryptPartnerText(itemDto.message)
        }
        val date = itemDto.time
            .replace("T", " ")

        var isAnswer = false
        if (!isMyAnswerExist && isMine) {
            isMyAnswerExist = true
            isAnswer = true
        }
        if (!isPartnerAnswerExist && !isMine) {
            isPartnerAnswerExist = true
            isAnswer = true
        }

        Chat2(
            id = 0,
            question = questionString,
            owner = owner,
            message = message,
            date = date,
            isAnswer = isAnswer
        )
    }
}