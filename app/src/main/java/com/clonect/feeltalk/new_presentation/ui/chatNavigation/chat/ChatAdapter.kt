package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemChatDividerBinding
import com.clonect.feeltalk.databinding.ItemTextChatMineBinding
import com.clonect.feeltalk.databinding.ItemTextChatPartnerBinding
import com.clonect.feeltalk.new_domain.model.chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter: RecyclerView.Adapter<ChatViewHolder>() {

    companion object {
        private const val TYPE_DIVIDER = 0

        private const val TYPE_TEXT_MINE = 1
        private const val TYPE_TEXT_PARTNER = 2

        private const val TYPE_VOICE_MINE = 3
        private const val TYPE_VOICE_PARTNER = 4

        private const val TYPE_EMOJI_MINE = 5
        private const val TYPE_EMOJI_PARTNER = 6

        private const val TYPE_IMAGE_MINE = 7
        private const val TYPE_IMAGE_PARTNER = 8

        private const val TYPE_VIDEO_MINE = 9
        private const val TYPE_VIDEO_PARTNER = 10

        private const val TYPE_CHALLENGE_MINE = 11
        private const val TYPE_CHALLENGE_PARTNER = 12

        private const val TYPE_QUESTION_MINE = 13
        private const val TYPE_QUESTION_PARTNER = 14
    }


    private val callback = object: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.index == newItem.index && oldItem.createAt == newItem.createAt
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.index == newItem.index
                    && oldItem.pageNo == newItem.pageNo
                    && oldItem.type == newItem.type
                    && oldItem.chatSender == newItem.chatSender
                    && oldItem.isRead == newItem.isRead
                    && oldItem.createAt == newItem.createAt
        }
    }

    val differ = AsyncListDiffer(this, callback)


    private var onQuestionAnswerButtonClick: ((QuestionChat) -> Unit)? = null

    private var myNickname: String? = null
    private var partnerNickname: String? = null
    private var partnerProfileUrl: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return when (viewType) {
            TYPE_DIVIDER -> {
                val binding = ItemChatDividerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChatDividerViewHolder(binding)
            }
            TYPE_TEXT_MINE -> {
                val binding = ItemTextChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TextChatMineViewHolder(binding)
            }
            TYPE_TEXT_PARTNER -> {
                val binding = ItemTextChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TextChatPartnerViewHolder(binding)
            }
            else -> {
                val binding = ItemChatDividerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChatDividerViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        val item = differ.currentList[position]
        return when (item.type) {
            ChatType.TimeDivider -> TYPE_DIVIDER
            ChatType.TextChatting -> {
                if (item.chatSender == myNickname) TYPE_TEXT_MINE
                else TYPE_TEXT_PARTNER
            }
            ChatType.VoiceChatting -> {
                if (item.chatSender == myNickname) TYPE_VOICE_MINE
                else TYPE_VOICE_PARTNER
            }
            ChatType.EmojiChatting -> {
                if (item.chatSender == myNickname) TYPE_EMOJI_MINE
                else TYPE_EMOJI_PARTNER
            }
            ChatType.ImageChatting -> {
                if (item.chatSender == myNickname) TYPE_IMAGE_MINE
                else TYPE_IMAGE_PARTNER
            }
            ChatType.VideoChatting -> {
                if (item.chatSender == myNickname) TYPE_VIDEO_MINE
                else TYPE_VIDEO_PARTNER
            }
            ChatType.ChallengeChatting -> {
                if (item.chatSender == myNickname) TYPE_CHALLENGE_MINE
                else TYPE_CHALLENGE_PARTNER
            }
            ChatType.QuestionChatting -> {
                if (item.chatSender == myNickname) TYPE_QUESTION_MINE
                else TYPE_QUESTION_PARTNER
            }
        }
    }


    fun setOnQuestionAnswerButtonClick(onClick: ((QuestionChat) -> Unit)) {
        onQuestionAnswerButtonClick = onClick
    }

    fun setMyNickname(nickname: String) {
        myNickname = nickname
    }

    fun setPartnerNickname(nickname: String) {
        partnerNickname = nickname
    }

    fun setPartnerProfileUrl(url: String) {
        partnerProfileUrl = url
    }
}



abstract class ChatViewHolder(root: View): RecyclerView.ViewHolder(root) {

    abstract fun bind(item: Chat)

    fun getFormatted(date: String): String {
        return date.substringAfter("T").substringBeforeLast(":")
    }
}

class ChatDividerViewHolder(
    val binding: ItemChatDividerBinding
): ChatViewHolder(binding.root) {
    override fun bind(item: Chat) {
        val chat = item as DividerChat
        val itemFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dividerFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        val date = itemFormat.parse(chat.createAt)
        binding.tvDate.text = dividerFormat.format(date)
    }
}

class TextChatMineViewHolder(
    val binding: ItemTextChatMineBinding,
) : ChatViewHolder(binding.root) {

    override fun bind(item: Chat) {
        val chat = item as TextChat
        binding.run {
            tvRead.text = root.context.getString(
                if (chat.isRead) R.string.chat_read
                else R.string.chat_unread
            )
            tvTime.text = getFormatted(chat.createAt)
            tvMessage.text = chat.message
        }
    }
}

class TextChatPartnerViewHolder(
    val binding: ItemTextChatPartnerBinding,
) : ChatViewHolder(binding.root) {

    override fun bind(item: Chat) {
        val chat = item as TextChat
        binding.run {
            tvRead.text = root.context.getString(
                if (chat.isRead) R.string.chat_read
                else R.string.chat_unread
            )
            tvTime.text = getFormatted(chat.createAt)
            tvMessage.text = chat.message

//           TODO
//            tvPartnerNickname.text = "연인 닉네임"
//            ivPartnerProfile.setImageResource()
        }
    }
}

