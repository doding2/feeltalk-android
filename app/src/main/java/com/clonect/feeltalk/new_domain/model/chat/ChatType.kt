package com.clonect.feeltalk.new_domain.model.chat

import java.io.Serializable

enum class ChatType(val raw: String) : Serializable {
    TimeDivider("timeDivider"),
    TextChatting("textChatting"),
    VoiceChatting("voiceChatting"),
    EmojiChatting("emojiChatting"),
    ImageChatting("imageChatting"),
    VideoChatting("videoChatting"),
    ChallengeChatting("challengeChatting"),
    QuestionChatting("questionChatting")
}