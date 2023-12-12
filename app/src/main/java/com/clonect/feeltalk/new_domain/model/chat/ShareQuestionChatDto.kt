package com.clonect.feeltalk.new_domain.model.chat

import com.google.gson.annotations.SerializedName
import java.io.Serial

data class ShareQuestionChatDto(
    val index: Long,
    val pageNo: Long,
    val isRead: Boolean,
    val createAt: String,
    val chatSender: String,
    val coupleQuestion: ChatQuestionDto,
): java.io.Serializable
