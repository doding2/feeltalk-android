package com.clonect.feeltalk.new_domain.model.chat

import java.io.Serializable

enum class ChatType(val raw: String) : Serializable {
    TimeDivider("timeDivider"),
    TextChatting("textChatting"),
    VoiceChatting("voiceChatting"),
    ImageChatting("imageChatting"),
    SignalChatting("SignalChatting"),
    ChallengeChatting("challengeChatting"),
    AddChallengeChatting("addChallengeChatting"),
    CompleteChallengeChatting("completeChallengeChatting"),
    QuestionChatting("questionChatting"),
    AnswerChatting("answerChatting"),
    PokeChatting("pressForAnswerChatting"),
    ResetPartnerPasswordChatting("resetPartnerPasswordChatting"),
}