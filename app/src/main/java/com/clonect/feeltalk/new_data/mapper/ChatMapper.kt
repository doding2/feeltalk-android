package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.chat.AddChallengeChat
import com.clonect.feeltalk.new_domain.model.chat.AnswerChat
import com.clonect.feeltalk.new_domain.model.chat.ChallengeChat
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.CompleteChallengeChat
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_domain.model.chat.PokeChat
import com.clonect.feeltalk.new_domain.model.chat.QuestionChat
import com.clonect.feeltalk.new_domain.model.chat.ResetPartnerPasswordChat
import com.clonect.feeltalk.new_domain.model.chat.SignalChat
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.model.chat.VoiceChat
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.model.signal.Signal
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

suspend fun ChatListDto.toChatList(
    loadQuestion: suspend (Long) -> Question,
    loadChallenge: suspend (Long) -> Challenge,
    loadImage: suspend (Long, String) -> Triple<File?, Int, Int>
): List<Chat> {
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
            "image", "imageChatting" -> {
                if (chatDto.url == null) {
                    continue
                } else {
                    val imageBundle = loadImage(chatDto.index, chatDto.url)
                    chatDto.run {
                        ImageChat(
                            index = index,
                            pageNo = page,
                            chatSender = if (mine) "me" else "partner",
                            isRead = isRead,
                            createAt = createAt,
                            url = url ?: "",
                            file = imageBundle.first,
                            width = imageBundle.second,
                            height = imageBundle.third
                        )
                    }
                }
            }
            "signal", "signalChatting" -> {
                if (chatDto.signal == null) {
                    continue
                } else {
                    SignalChat(
                        index = chatDto.index,
                        pageNo = page,
                        chatSender = if (chatDto.mine) "me" else "partner",
                        isRead = chatDto.isRead,
                        createAt = chatDto.createAt,
                        signal = when (chatDto.signal.toInt()) {
                            0 -> Signal.Zero
                            25 -> Signal.Quarter
                            50 -> Signal.Half
                            75 -> Signal.ThreeFourth
                            100 -> Signal.One
                            else -> continue
                        }
                    )
                }
            }
            "challenge", "challengeChatting" -> {
                if (chatDto.coupleChallenge == null) {
                    continue
                } else {
                    val challenge = loadChallenge(chatDto.coupleChallenge.index)
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    ChallengeChat(
                        index = chatDto.index,
                        pageNo = page,
                        chatSender = if (chatDto.mine) "me" else "partner",
                        isRead = chatDto.isRead,
                        createAt = chatDto.createAt,
                        challenge = Challenge(
                            index = chatDto.coupleChallenge.index,
                            title = chatDto.coupleChallenge.challengeTitle,
                            body = chatDto.coupleChallenge.challengeBody ?: "",
                            deadline = format.parse(chatDto.coupleChallenge.deadline) ?: continue,
                            owner = chatDto.coupleChallenge.creator,
                            isCompleted = challenge.isCompleted,
                            completeDate = challenge.completeDate,
                            isNew = false
                        )
                    )
                }
            }
            "addChallenge", "addChallengeChatting" -> {
                if (chatDto.coupleChallenge == null) {
                    continue
                } else {
                    val challenge = loadChallenge(chatDto.coupleChallenge.index)
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    AddChallengeChat(
                        index = chatDto.index,
                        pageNo = page,
                        chatSender = if (chatDto.mine) "me" else "partner",
                        isRead = chatDto.isRead,
                        createAt = chatDto.createAt,
                        challenge = Challenge(
                            index = chatDto.coupleChallenge.index,
                            title = chatDto.coupleChallenge.challengeTitle,
                            body = chatDto.coupleChallenge.challengeBody ?: "",
                            deadline = format.parse(chatDto.coupleChallenge.deadline) ?: continue,
                            owner = chatDto.coupleChallenge.creator,
                            isCompleted = challenge.isCompleted,
                            isNew = false
                        )
                    )
                }
            }
            "completeChallenge", "completeChallengeChatting" -> {
                if (chatDto.coupleChallenge == null) {
                    continue
                } else {
                    val challenge = loadChallenge(chatDto.coupleChallenge.index)
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    CompleteChallengeChat(
                        index = chatDto.index,
                        pageNo = page,
                        chatSender = if (chatDto.mine) "me" else "partner",
                        isRead = chatDto.isRead,
                        createAt = chatDto.createAt,
                        challenge = Challenge(
                            index = chatDto.coupleChallenge.index,
                            title = chatDto.coupleChallenge.challengeTitle,
                            body = chatDto.coupleChallenge.challengeBody ?: "",
                            deadline = format.parse(chatDto.coupleChallenge.deadline) ?: continue,
                            owner = chatDto.coupleChallenge.creator,
                            isCompleted = challenge.isCompleted,
                            completeDate = challenge.completeDate,
                            isNew = false
                        )
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
                            question = loadQuestion(coupleQuestion!!)
                        )
                    }
                }
            }
            "answer", "answerChatting" -> {
                if (chatDto.coupleQuestion == null) {
                    continue
                } else {
                    chatDto.run {
                        AnswerChat(
                            index = index,
                            pageNo = page,
                            chatSender = if (mine) "me" else "partner",
                            isRead = isRead,
                            createAt = createAt,
                            question = loadQuestion(coupleQuestion!!)
                        )
                    }
                }
            }
            "pressForAnswer", "pressForAnswerChatting" -> {
                if (chatDto.coupleQuestion == null) {
                    continue
                } else {
                    chatDto.run {
                        PokeChat(
                            index = index,
                            pageNo = page,
                            chatSender = if (mine) "me" else "partner",
                            isRead = isRead,
                            createAt = createAt,
                            questionIndex = coupleQuestion!!
                        )
                    }
                }
            }
            "resetPartnerPassword", "resetPartnerPasswordChatting" -> {
                chatDto.run {
                    ResetPartnerPasswordChat(
                        index = index,
                        pageNo = page,
                        chatSender = if (mine) "me" else "partner",
                        isRead = isRead,
                        createAt = createAt,
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