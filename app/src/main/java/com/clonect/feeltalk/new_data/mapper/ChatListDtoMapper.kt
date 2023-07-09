package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.TextChat

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
            else -> {
                continue
            }
        }
        newChatList.add(chat)
    }
    return newChatList
}