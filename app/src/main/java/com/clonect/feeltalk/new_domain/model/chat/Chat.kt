package com.clonect.feeltalk.new_domain.model.chat

abstract class Chat(
    open val index: Long,
    open var pageNo: Long,
    open val type: ChatType,
    open val chatSender: String,
    open var isRead: Boolean,
    open val createAt: String,
    open var isSending: Boolean = false,
) {
    fun clone(): Chat {
        return when (this) {
            is DividerChat -> this.copy()
            is TextChat -> this.copy()
            is VoiceChat -> this.copy()
            is EmojiChat -> this.copy()
            is ImageChat -> this.copy()
            is VideoChat -> this.copy()
            is ChallengeChat -> this.copy()
            is QuestionChat -> this.copy()
            else -> this
        }
    }

    fun copy(chat: Chat): Chat {
        return when (this) {
            is DividerChat -> {
                (chat as? DividerChat)?.copy(createAt = createAt)
            }
            is TextChat -> {
                (chat as? TextChat)?.copy(
                    index = index,
                    pageNo = pageNo,
                    chatSender = chatSender,
                    isRead = isRead,
                    createAt = createAt,
                    isSending = isSending,
                    message = message,
                )
            }
            is VoiceChat -> {
                (chat as? VoiceChat)?.copy(
                    index = index,
                    pageNo = pageNo,
                    chatSender = chatSender,
                    isRead = isRead,
                    createAt = createAt,
                    isSending = isSending,
                    url = url
                )
            }
            is EmojiChat -> {
                (chat as? EmojiChat)?.copy(
                    index = index,
                    pageNo = pageNo,
                    chatSender = chatSender,
                    isRead = isRead,
                    createAt = createAt,
                    isSending = isSending,
                    emoji = emoji
                )
            }
            is ImageChat -> {
                (chat as? ImageChat)?.copy(
                    index = index,
                    pageNo = pageNo,
                    chatSender = chatSender,
                    isRead = isRead,
                    createAt = createAt,
                    isSending = isSending,
                    url = url
                )
            }
            is VideoChat -> {
                (chat as? VideoChat)?.copy(
                    index = index,
                    pageNo = pageNo,
                    chatSender = chatSender,
                    isRead = isRead,
                    createAt = createAt,
                    isSending = isSending,
                    url = url
                )
            }
            is ChallengeChat -> {
                (chat as? ChallengeChat)?.copy(
                    index = index,
                    pageNo = pageNo,
                    chatSender = chatSender,
                    isRead = isRead,
                    createAt = createAt,
                    isSending = isSending,
                    challenge = challenge
                )
            }
            is QuestionChat -> {
                (chat as? QuestionChat)?.copy(
                    index = index,
                    pageNo = pageNo,
                    chatSender = chatSender,
                    isRead = isRead,
                    createAt = createAt,
                    isSending = isSending,
                    question = question
                )
            }
            else -> chat
        } ?: chat
    }
}

data class DividerChat(
    override val createAt: String,
): Chat(-1, -1, ChatType.TimeDivider, "feeltalk", false, createAt)

data class TextChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val message: String
): Chat(index, pageNo, ChatType.TextChatting, chatSender, isRead, createAt, isSending)

data class VoiceChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val url: String
): Chat(index, pageNo, ChatType.VoiceChatting, chatSender, isRead, createAt, isSending)

data class EmojiChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val emoji: String
): Chat(index, pageNo, ChatType.EmojiChatting, chatSender, isRead, createAt, isSending)

data class ImageChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val url: String
): Chat(index, pageNo, ChatType.ImageChatting, chatSender, isRead, createAt, isSending)


data class VideoChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val url: String
): Chat(index, pageNo, ChatType.VideoChatting, chatSender, isRead, createAt, isSending)


data class ChallengeChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val challenge: ChatChallengeDto
): Chat(index, pageNo, ChatType.ChallengeChatting, chatSender, isRead, createAt, isSending)

data class QuestionChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val question: ChatQuestionDto
): Chat(index, pageNo, ChatType.QuestionChatting, chatSender, isRead, createAt, isSending)
