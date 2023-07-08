package com.clonect.feeltalk.new_domain.model.chat

abstract class Chat(
    open val index: Long,
    open var pageNo: Long,
    open val type: ChatType,
    open val chatSender: String,
    open val isRead: Boolean,
    open val createAt: String,
)


data class DividerChat(
    override val createAt: String,
): Chat(-1, -1, ChatType.TimeDivider, "feeltalk", false, createAt)

data class TextChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override val isRead: Boolean,
    override val createAt: String,
    val message: String
): Chat(index, pageNo, ChatType.TextChatting, chatSender, isRead, createAt)

data class VoiceChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override val isRead: Boolean,
    override val createAt: String,
    val url: String
): Chat(index, pageNo, ChatType.VoiceChatting, chatSender, isRead, createAt)

data class EmojiChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override val isRead: Boolean,
    override val createAt: String,
    val emoji: String
): Chat(index, pageNo, ChatType.EmojiChatting, chatSender, isRead, createAt)

data class ImageChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override val isRead: Boolean,
    override val createAt: String,
    val url: String
): Chat(index, pageNo, ChatType.ImageChatting, chatSender, isRead, createAt)


data class VideoChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override val isRead: Boolean,
    override val createAt: String,
    val url: String
): Chat(index, pageNo, ChatType.VideoChatting, chatSender, isRead, createAt)


data class ChallengeChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override val isRead: Boolean,
    override val createAt: String,
    val challenge: ChatChallengeDto
): Chat(index, pageNo, ChatType.ChallengeChatting, chatSender, isRead, createAt)

data class QuestionChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override val isRead: Boolean,
    override val createAt: String,
    val question: ChatQuestionDto
): Chat(index, pageNo, ChatType.QuestionChatting, chatSender, isRead, createAt)
