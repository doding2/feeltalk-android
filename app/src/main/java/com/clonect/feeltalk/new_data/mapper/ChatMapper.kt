package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.chat.*

fun ChatListDto.toChatList(): List<Chat> {
    val newChatList = mutableListOf<Chat>()
    for (chatDto in chatting) {
        val chat = when (chatDto.type) {
            "text", "textChatting" -> {
                chatDto.run {
                    TextChat(
                        index = index,
                        pageNo = page,
                        chatSender = if (mine) "me" else "partner",
                        isRead = isRead,
                        createAt = createAt,
                        message = message ?: ""
                    )
                }
            }
            "voice", "voiceChatting" -> {
                chatDto.run {
                    VoiceChat(
                        index = index,
                        pageNo = page,
                        chatSender = if (mine) "me" else "partner",
                        isRead = isRead,
                        createAt = createAt,
                        url = url ?: ""
                    )
                }
            }
            "question", "questionChatting" -> {
                if (chatDto.coupleQuestion == null) {
                    continue
                } else {
                    chatDto.run {
                        QuestionChat(
                            index = index,
                            pageNo = page,
                            chatSender = if (mine) "me" else "partner",
                            isRead = isRead,
                            createAt = createAt,
                            question = coupleQuestion!!
                        )
                    }
                }
            }
            else -> {
                continue
            }
        }
        newChatList.add(chat)
    }
    return newChatList
}