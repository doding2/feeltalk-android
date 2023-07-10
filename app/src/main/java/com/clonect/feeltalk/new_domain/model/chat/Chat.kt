package com.clonect.feeltalk.new_domain.model.chat

abstract class Chat(
    open val index: Long,
    open var pageNo: Long,
    open val type: ChatType,
    open val chatSender: String,
    open var isRead: Boolean,
    open val createAt: String,
    open var isSending: Boolean = false
) {
    fun copy(chat: Chat): Chat {
        return when (this) {
            is DividerChat -> {
                (chat as DividerChat).run {
                    this.copy(createAt = createAt)
                }
            }
            is TextChat -> {
                (chat as TextChat).run {
                    this.copy(
                        index = index,
                        pageNo = pageNo,
                        chatSender = chatSender,
                        isRead = isRead,
                        createAt = createAt,
                        message = message
                    )
                }
            }
            is VoiceChat -> {
                (chat as VoiceChat).run {
                    this.copy(
                        index = index,
                        pageNo = pageNo,
                        chatSender = chatSender,
                        isRead = isRead,
                        createAt = createAt,
                        url = url
                    )
                }
            }
            is EmojiChat -> {
                (chat as EmojiChat).run {
                    this.copy(
                        index = index,
                        pageNo = pageNo,
                        chatSender = chatSender,
                        isRead = isRead,
                        createAt = createAt,
                        emoji = emoji
                    )
                }
            }
            is ImageChat -> {
                (chat as ImageChat).run {
                    this.copy(
                        index = index,
                        pageNo = pageNo,
                        chatSender = chatSender,
                        isRead = isRead,
                        createAt = createAt,
                        url = url
                    )
                }
            }
            is VideoChat -> {
                (chat as VideoChat).run {
                    this.copy(
                        index = index,
                        pageNo = pageNo,
                        chatSender = chatSender,
                        isRead = isRead,
                        createAt = createAt,
                        url = url
                    )
                }
            }
            is ChallengeChat -> {
                (chat as ChallengeChat).run {
                    this.copy(
                        index = index,
                        pageNo = pageNo,
                        chatSender = chatSender,
                        isRead = isRead,
                        createAt = createAt,
                        challenge = challenge
                    )
                }
            }
            is QuestionChat -> {
                (chat as QuestionChat).run {
                    this.copy(
                        index = index,
                        pageNo = pageNo,
                        chatSender = chatSender,
                        isRead = isRead,
                        createAt = createAt,
                        question = question
                    )
                }
            }
            else -> chat
        }
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
): Chat(index, pageNo, ChatType.TextChatting, chatSender, isRead, createAt)

data class VoiceChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val url: String
): Chat(index, pageNo, ChatType.VoiceChatting, chatSender, isRead, createAt)

data class EmojiChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val emoji: String
): Chat(index, pageNo, ChatType.EmojiChatting, chatSender, isRead, createAt)

data class ImageChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val url: String
): Chat(index, pageNo, ChatType.ImageChatting, chatSender, isRead, createAt)


data class VideoChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val url: String
): Chat(index, pageNo, ChatType.VideoChatting, chatSender, isRead, createAt)


data class ChallengeChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val challenge: ChatChallengeDto
): Chat(index, pageNo, ChatType.ChallengeChatting, chatSender, isRead, createAt)

data class QuestionChat(
    override val index: Long,
    override var pageNo: Long,
    override val chatSender: String,
    override var isRead: Boolean,
    override val createAt: String,
    override var isSending: Boolean = false,
    val question: ChatQuestionDto
): Chat(index, pageNo, ChatType.QuestionChatting, chatSender, isRead, createAt)
