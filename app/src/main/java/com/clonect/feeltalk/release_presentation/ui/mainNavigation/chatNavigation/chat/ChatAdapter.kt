package com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.chat

import android.content.ClipData
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.ItemAddChallengeChatMineBinding
import com.clonect.feeltalk.databinding.ItemAddChallengeChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemAnswerChatMineBinding
import com.clonect.feeltalk.databinding.ItemAnswerChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemChallengeChatMineBinding
import com.clonect.feeltalk.databinding.ItemChallengeChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemChatDividerBinding
import com.clonect.feeltalk.databinding.ItemCompleteChallengeChatMineBinding
import com.clonect.feeltalk.databinding.ItemCompleteChallengeChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemImageChatMineBinding
import com.clonect.feeltalk.databinding.ItemImageChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemPokeChatMineBinding
import com.clonect.feeltalk.databinding.ItemPokeChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemQuestionChatMineBinding
import com.clonect.feeltalk.databinding.ItemQuestionChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemResetPartnerPasswordChatMineBinding
import com.clonect.feeltalk.databinding.ItemResetPartnerPasswordChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemSignalChatMineBinding
import com.clonect.feeltalk.databinding.ItemSignalChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemTextChatMineBinding
import com.clonect.feeltalk.databinding.ItemTextChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemVoiceChatMineBinding
import com.clonect.feeltalk.databinding.ItemVoiceChatPartnerBinding
import com.clonect.feeltalk.release_domain.model.chat.AddChallengeChat
import com.clonect.feeltalk.release_domain.model.chat.AnswerChat
import com.clonect.feeltalk.release_domain.model.chat.ChallengeChat
import com.clonect.feeltalk.release_domain.model.chat.Chat
import com.clonect.feeltalk.release_domain.model.chat.ChatType
import com.clonect.feeltalk.release_domain.model.chat.CompleteChallengeChat
import com.clonect.feeltalk.release_domain.model.chat.DividerChat
import com.clonect.feeltalk.release_domain.model.chat.ImageChat
import com.clonect.feeltalk.release_domain.model.chat.PokeChat
import com.clonect.feeltalk.release_domain.model.chat.QuestionChat
import com.clonect.feeltalk.release_domain.model.chat.ResetPartnerPasswordChat
import com.clonect.feeltalk.release_domain.model.chat.SignalChat
import com.clonect.feeltalk.release_domain.model.chat.TextChat
import com.clonect.feeltalk.release_domain.model.chat.VoiceChat
import com.clonect.feeltalk.release_domain.model.signal.Signal
import com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.chat.audioVisualizer.RecordingReplayer
import com.clonect.feeltalk.release_presentation.ui.util.dpToPx
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.absoluteValue
import kotlin.math.ceil


class ChatAdapter: PagingDataAdapter<Chat, ChatAdapter.ChatViewHolder>(diffCallback) {

    private var myNickname: String? = null
    private var partnerNickname: String? = null
    private var partnerSignal: Signal = Signal.One

    private val viewHolders = mutableMapOf<Chat, ChatViewHolder>()
    private val voiceViewHolders = mutableListOf<ChatViewHolder>()

    private var isPartnerInChat = false

    private var onClick: ((View, Chat) -> Unit) = { _, _ -> }
    private var onRetry: (Chat) -> Unit = { }
    private var onCancel: (Chat) -> Unit = { }

    override fun onViewRecycled(holder: ChatViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is VoiceChatMineViewHolder -> {
                holder.audioFile = null
                holder.replayer?.stop()
                holder.replayer = null
                holder.timer = null
            }
            is VoiceChatPartnerViewHolder -> {
                holder.audioFile = null
                holder.replayer?.stop()
                holder.replayer = null
                holder.timer = null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val viewHolder = when (viewType) {
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
            TYPE_VOICE_MINE -> {
                val binding = ItemVoiceChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = VoiceChatMineViewHolder(binding)
                voiceViewHolders.add(holder)
                holder
            }
            TYPE_VOICE_PARTNER -> {
                val binding = ItemVoiceChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = VoiceChatPartnerViewHolder(binding)
                voiceViewHolders.add(holder)
                holder
            }
            TYPE_IMAGE_MINE -> {
                val binding = ItemImageChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ImageChatMineViewHolder(binding)
            }
            TYPE_IMAGE_PARTNER -> {
                val binding = ItemImageChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ImageChatPartnerViewHolder(binding)
            }
            TYPE_SIGNAL_MINE -> {
                val binding = ItemSignalChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SignalChatMineViewHolder(binding)
            }
            TYPE_SIGNAL_PARTNER -> {
                val binding = ItemSignalChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SignalChatPartnerViewHolder(binding)
            }
            TYPE_CHALLENGE_MINE -> {
                val binding = ItemChallengeChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChallengeChatMineViewHolder(binding)
            }
            TYPE_CHALLENGE_PARTNER -> {
                val binding = ItemChallengeChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChallengeChatPartnerViewHolder(binding)
            }
            TYPE_ADD_CHALLENGE_MINE -> {
                val binding = ItemAddChallengeChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddChallengeChatMineViewHolder(binding)
            }
            TYPE_ADD_CHALLENGE_PARTNER -> {
                val binding = ItemAddChallengeChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddChallengeChatPartnerViewHolder(binding)
            }
            TYPE_COMPLETE_CHALLENGE_MINE -> {
                val binding = ItemCompleteChallengeChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CompleteChallengeChatMineViewHolder(binding)
            }
            TYPE_COMPLETE_CHALLENGE_PARTNER -> {
                val binding = ItemCompleteChallengeChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CompleteChallengeChatPartnerViewHolder(binding)
            }
            TYPE_QUESTION_MINE -> {
                val binding = ItemQuestionChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                QuestionChatMineViewHolder(binding)
            }
            TYPE_QUESTION_PARTNER -> {
                val binding = ItemQuestionChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                QuestionChatPartnerViewHolder(binding)
            }
            TYPE_ANSWER_MINE -> {
                val binding = ItemAnswerChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AnswerChatMineViewHolder(binding)
            }
            TYPE_ANSWER_PARTNER -> {
                val binding = ItemAnswerChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AnswerChatPartnerViewHolder(binding)
            }
            TYPE_POKE_MINE -> {
                val binding = ItemPokeChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PokeChatMineViewHolder(binding)
            }
            TYPE_POKE_PARTNER -> {
                val binding = ItemPokeChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PokeChatPartnerViewHolder(binding)
            }
            TYPE_RESET_PARTNER_PASSWORD_MINE -> {
                val binding = ItemResetPartnerPasswordChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ResetPartnerPasswordChatMineViewHolder(binding)
            }
            TYPE_RESET_PARTNER_PASSWORD_PARTNER -> {
                val binding = ItemResetPartnerPasswordChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ResetPartnerPasswordChatPartnerViewHolder(binding)
            }
            else -> {
                val binding = ItemChatDividerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChatDividerViewHolder(binding)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = getItem(position)
        val prevItem = if (position - 1 < 0) null
        else getItem(position - 1)
        val nextItem = if (itemCount <= position + 1) null
        else getItem(position + 1)
        if (item != null) {
            viewHolders[item] = holder
            holder.bind(prevItem, item, nextItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item?.type) {
            ChatType.TimeDivider -> TYPE_DIVIDER
            ChatType.TextChatting -> {
                if (item.chatSender == myNickname) TYPE_TEXT_MINE
                else TYPE_TEXT_PARTNER
            }
            ChatType.VoiceChatting -> {
                if (item.chatSender == myNickname) TYPE_VOICE_MINE
                else TYPE_VOICE_PARTNER
            }
            ChatType.ImageChatting -> {
                if (item.chatSender == myNickname) TYPE_IMAGE_MINE
                else TYPE_IMAGE_PARTNER
            }
            ChatType.SignalChatting -> {
                if (item.chatSender == myNickname) TYPE_SIGNAL_MINE
                else TYPE_SIGNAL_PARTNER
            }
            ChatType.ChallengeChatting -> {
                if (item.chatSender == myNickname) TYPE_CHALLENGE_MINE
                else TYPE_CHALLENGE_PARTNER
            }
            ChatType.AddChallengeChatting -> {
                if (item.chatSender == myNickname) TYPE_ADD_CHALLENGE_MINE
                else TYPE_ADD_CHALLENGE_PARTNER
            }
            ChatType.CompleteChallengeChatting -> {
                if (item.chatSender == myNickname) TYPE_COMPLETE_CHALLENGE_MINE
                else TYPE_COMPLETE_CHALLENGE_PARTNER
            }
            ChatType.QuestionChatting -> {
                if (item.chatSender == myNickname) TYPE_QUESTION_MINE
                else TYPE_QUESTION_PARTNER
            }
            ChatType.AnswerChatting -> {
                if (item.chatSender == myNickname) TYPE_ANSWER_MINE
                else TYPE_ANSWER_PARTNER
            }
            ChatType.PokeChatting -> {
                if (item.chatSender == myNickname) TYPE_POKE_MINE
                else TYPE_POKE_PARTNER
            }
            ChatType.ResetPartnerPasswordChatting -> {
                if (item.chatSender == myNickname) TYPE_RESET_PARTNER_PASSWORD_MINE
                else TYPE_RESET_PARTNER_PASSWORD_PARTNER
            }
            else -> TYPE_DIVIDER
        }
    }

    fun resetVoiceChats() {
        for (holder in voiceViewHolders) {
            if (holder is VoiceChatMineViewHolder) holder.reset()
            if (holder is VoiceChatPartnerViewHolder) holder.reset()
        }
    }

    fun applyPartnerInfoChanges() {
        viewHolders.filter { it.key.chatSender == "partner" }
            .forEach { it.value.setPartnerInfo(partnerNickname, partnerSignal) }
    }


    fun setOnClickItem(onClick: (View, Chat) -> Unit) {
        this.onClick = onClick
    }

    fun setOnRetry(onRetry: (Chat) -> Unit) {
        this.onRetry = onRetry
    }

    fun setOnCancel(onCancel: (Chat) -> Unit) {
        this.onCancel = onCancel
    }


    fun setMyNickname(nickname: String) {
        myNickname = nickname
    }

    fun setPartnerNickname(nickname: String?) {
        this.partnerNickname = nickname
//        applyPartnerInfoChanges()
    }

    fun setPartnerSignal(signal: Signal) {
        this.partnerSignal = signal
        applyPartnerInfoChanges()
    }

    fun setPartnerInChat(isInChat: Boolean) {
        isPartnerInChat = isInChat
        if (isPartnerInChat) {
            readAllChats()
        }
    }

    fun readAllChats() {
        if (viewHolders.isEmpty())
            return
        val readText = viewHolders.values.first().root.context.getString(R.string.chat_read)
        for ((chat, holder) in viewHolders) {
            val tvRead = holder.root.findViewById<TextView>(R.id.tv_read) ?: continue
            tvRead.text = readText
        }
    }



    companion object {
        private val diffCallback = object: DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.index == newItem.index && oldItem.createAt == newItem.createAt && oldItem.sendState == newItem.sendState
            }

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.index == newItem.index
                        && oldItem.type == newItem.type
                        && oldItem.chatSender == newItem.chatSender
                        && oldItem.isRead == newItem.isRead
                        && oldItem.createAt == newItem.createAt
                        && oldItem.sendState == newItem.sendState
            }
        }


        private const val TYPE_DIVIDER = 0

        const val TYPE_TEXT_MINE = 1
        const val TYPE_TEXT_PARTNER = 2

        const val TYPE_SIGNAL_MINE = 3
        const val TYPE_SIGNAL_PARTNER = 4

        const val TYPE_VOICE_MINE = 5
        const val TYPE_VOICE_PARTNER = 6

        const val TYPE_IMAGE_MINE = 7
        const val TYPE_IMAGE_PARTNER = 8

        const val TYPE_ADD_CHALLENGE_MINE = 9
        const val TYPE_ADD_CHALLENGE_PARTNER = 10

        const val TYPE_COMPLETE_CHALLENGE_MINE = 11
        const val TYPE_COMPLETE_CHALLENGE_PARTNER = 12

        const val TYPE_CHALLENGE_MINE = 13
        const val TYPE_CHALLENGE_PARTNER = 14

        const val TYPE_ANSWER_MINE = 15
        const val TYPE_ANSWER_PARTNER = 16

        const val TYPE_QUESTION_MINE = 17
        const val TYPE_QUESTION_PARTNER = 18

        const val TYPE_POKE_MINE = 19
        const val TYPE_POKE_PARTNER = 20

        const val TYPE_RESET_PARTNER_PASSWORD_MINE = 21
        const val TYPE_RESET_PARTNER_PASSWORD_PARTNER = 22
    }



    abstract class ChatViewHolder(val root: View): RecyclerView.ViewHolder(root) {

        val defaultVerticalMargin = root.context.dpToPx(8f)
        val continuousVerticalMargin = root.context.dpToPx(2f)

        abstract fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?)

        open fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) { }

        open fun setPartnerInfo(nickname: String?, signal: Signal) { }

        fun getFormatted(date: String): String {
            return date.substringAfter("T").substringBeforeLast(":")
        }
    }

    inner class ChatDividerViewHolder(
        val binding: ItemChatDividerBinding,
    ): ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as? DividerChat
            if (chat == null) {
                binding.root.visibility = View.GONE
                return
            } else {
                binding.root.visibility = View.VISIBLE
            }
            val itemFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dividerFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
            val date = itemFormat.parse(chat.createAt)
            binding.tvDate.text = date?.let { dividerFormat.format(it) }
        }
    }


    inner class TextChatMineViewHolder(
        val binding: ItemTextChatMineBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as TextChat
            binding.run {
                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)
                tvMessage.text = chat.message

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                tvMessage.setOnLongClickListener {
                    copyText(chat)
                    false
                }

                makeContinuous(prevItem, item, nextItem)
            }
        }


        private fun copyText(chat: TextChat) {
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("채팅", chat.message)
            clipboard.setPrimaryClip(clip)
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class TextChatPartnerViewHolder(
        val binding: ItemTextChatPartnerBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as TextChat
            binding.run {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)
                tvMessage.text = chat.message

                setPartnerInfo(partnerNickname, partnerSignal)

                tvMessage.setOnLongClickListener {
                    copyText(chat)
                    false
                }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }


        private fun copyText(chat: TextChat) {
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("채팅", chat.message)
            clipboard.setPrimaryClip(clip)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }


    inner class VoiceChatMineViewHolder(
        val binding: ItemVoiceChatMineBinding,
    ) : ChatViewHolder(binding.root) {

        var audioDuration: Long = 0
        var audioFile: File? = null
        var replayer: RecordingReplayer? = null
        var timer: Timer? = null
        var replayTime: Long = 0
        var isPaused = false
        var vvCount = 0

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as VoiceChat

            init(chat)

            binding.run {
                if (replayer?.isReplaying == true) {
                    ivReplay.visibility = View.GONE
                    ivPause.visibility = View.VISIBLE
                    vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.main_500))
                }
                else {
                    ivReplay.visibility = View.VISIBLE
                    ivPause.visibility = View.GONE
                    vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.gray_700))
                }

                vvVoiceVisualizer.visibility = View.VISIBLE

                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                ivReplay.setOnClickListener {
                    if (chat.sendState == Chat.ChatSendState.Sending) return@setOnClickListener
                    if (isPaused) resume()
                    else replay()
                }
                ivPause.setOnClickListener { pause() }

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                CoroutineScope(Dispatchers.IO).launch {
                    vvCount = 5
                    while (vvCount > 0) {
                        vvVoiceVisualizer.reset()
                        vvVoiceVisualizer.drawDefaultView()
                        vvCount--
                        delay(50)
                    }
                }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        private fun init(chat: VoiceChat) = binding.run {
            if (audioFile == null) {
                CoroutineScope(Dispatchers.IO).launch {

                    val cacheFile = File(root.context.cacheDir, "${chat.index}.wav")
                    val serverFileSize = withContext(Dispatchers.IO) {
                        getVoiceFileSize(chat)
                    }

                    runCatching {
                        if (cacheFile.length() == serverFileSize.toLong()) {
                            cacheFile
                        } else {
                            if (chat.url == "index") {
                                File(root.context.cacheDir, "${chat.index}.wav")
                            } else {
                                downloadVoiceFile(chat)
                            }
                        }
                    }.onSuccess {
                        audioFile = it
                    }.onFailure {
                        infoLog("보이스 파일 다운로드 실패: ${it.localizedMessage}\n${
                            it.stackTrace.joinToString("\n")
                        }")
                        audioFile = null
                    }
                    if (audioFile?.exists() == false || audioFile?.canRead() == false) {
                        audioFile = null
                    }

                    withContext(Dispatchers.Main) {
                        setAudioDuration()
                    }
                }

                vvVoiceVisualizer.visibility = View.VISIBLE
                vvVoiceVisualizer.numColumns = 8
                vvVoiceVisualizer.reset()
                vvVoiceVisualizer.drawDefaultView()

                audioDuration = 0
                replayTime = 0
            }
        }

        private suspend fun getVoiceFileSize(chat: VoiceChat): Int = withContext(Dispatchers.IO) {
            if (chat.url.isBlank() || chat.url == "index") return@withContext -1

            var conn: URLConnection? = null
            val size = try {
                val url = URL(chat.url)
                conn = url.openConnection()
                if (conn is HttpURLConnection) {
                    conn.requestMethod = "HEAD"
                }
                conn?.getInputStream()
                conn?.contentLength
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            } finally {
                if (conn is HttpURLConnection) {
                    conn.disconnect()
                }
            }
            return@withContext size ?: -1
        }

        private suspend fun downloadVoiceFile(chat: VoiceChat) = suspendCoroutine { continuation ->
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val file = File(binding.root.context.cacheDir, "${chat.index}.wav")
            URL(chat.url).openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                    continuation.resume(file)
                }
            }
        }


        private fun setAudioDuration() {
            try {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(binding.root.context, Uri.fromFile(audioFile ?: return))
                val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                if (duration != null) {
                    audioDuration = duration.toLong()
                }
            } catch (e: Exception) {
                infoLog("audio duration error: ${e.localizedMessage}")
                audioDuration = 0
            }
            setReplayTimeText(audioDuration)
        }

        private fun setReplayTimeText(replayTime: Long) = binding.run {
            val totalSeconds = replayTime / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            val minutesStr = if (minutes < 10) "0$minutes" else minutes.toString()
            val secondsStr = if (seconds < 10) "0$seconds" else seconds.toString()
            tvReplayTime.text = "$minutesStr:$secondsStr"
        }


        fun replay() = binding.run {
            if (audioFile == null || replayer?.isReplaying == true) return@run

            replayer = try {
                RecordingReplayer(root.context, audioFile!!, vvVoiceVisualizer)
            } catch (e: Exception) {
                infoLog("오디오 재생 실패: ${e.localizedMessage}\n${e.stackTrace.joinToString("\n")}")
                return@run
            }
            vvCount = 0
            replayer?.replay()

            ivReplay.visibility = View.GONE
            ivPause.visibility = View.VISIBLE
            vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.main_500))
            isPaused = false

            timer = Timer()
            timer?.schedule(object: TimerTask(){
                override fun run() {
                    if (replayer?.isReplaying == true) {
                        replayTime += 100

                        if (replayTime <= audioDuration) {
                            CoroutineScope(Dispatchers.Main).launch {
                                setReplayTimeText(replayTime)
                            }
                        }
                    }
                    if (replayer?.isCompleted == true) {
                        CoroutineScope(Dispatchers.Main).launch {
                            reset(isCompleted = true)
                        }
                    }
                }
            }, 100, 100)
        }

        fun pause() = binding.run {
            replayer?.pause()
            isPaused = true

            vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.gray_700))
            ivReplay.visibility = View.VISIBLE
            ivPause.visibility = View.GONE
        }

        fun resume() = binding.run {
            replayer?.resume()
            isPaused = false

            vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.main_500))
            ivReplay.visibility = View.GONE
            ivPause.visibility = View.VISIBLE
        }

        fun reset(isCompleted: Boolean = false) = binding.run {
            isPaused = false
            replayTime = 0
            setAudioDuration()

            replayer?.stop()
            replayer = null
            timer?.cancel()
            timer = null

            ivReplay.visibility = View.VISIBLE
            ivPause.visibility = View.GONE

            if (!isCompleted) {
                vvVoiceVisualizer.reset()
                vvVoiceVisualizer.drawDefaultView()
            }
            vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.gray_700))
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class VoiceChatPartnerViewHolder(
        val binding: ItemVoiceChatPartnerBinding,
    ) : ChatViewHolder(binding.root) {

        var audioDuration: Long = 0
        var audioFile: File? = null
        var replayer: RecordingReplayer? = null
        var timer: Timer? = null
        var replayTime: Long = 0
        var isPaused = false
        var vvCount = 0

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as VoiceChat

            init(chat)

            binding.run {
                if (replayer?.isReplaying == true) {
                    ivReplay.visibility = View.GONE
                    ivPause.visibility = View.VISIBLE
                    vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.main_500))
                }
                else {
                    ivReplay.visibility = View.VISIBLE
                    ivPause.visibility = View.GONE
                    vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.gray_700))
                }

                vvVoiceVisualizer.visibility = View.VISIBLE

                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE
                tvRead.text = root.context.getString(
                    if (chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                ivReplay.setOnClickListener {
                    if (isPaused) resume()
                    else replay()
                }
                ivPause.setOnClickListener { pause() }

                setPartnerInfo(partnerNickname, partnerSignal)

                CoroutineScope(Dispatchers.IO).launch {
                    vvCount = 5
                    while (vvCount > 0) {
                        vvVoiceVisualizer.reset()
                        vvVoiceVisualizer.drawDefaultView()
                        vvCount--
                        delay(50)
                    }
                }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        private fun init(chat: VoiceChat) = binding.run {
            if (audioFile == null) {
                CoroutineScope(Dispatchers.IO).launch {

                    val cacheFile = File(root.context.cacheDir, "${chat.index}.wav")
                    val serverFileSize = withContext(Dispatchers.IO) {
                        getVoiceFileSize(chat)
                    }

                    runCatching {
                        if (cacheFile.length() == serverFileSize.toLong()) {
                            cacheFile
                        } else {
                            if (chat.url == "index") {
                                File(root.context.cacheDir, "${chat.index}.wav")
                            } else {
                                downloadVoiceFile(chat)
                            }
                        }
                    }.onSuccess {
                        audioFile = it
                    }.onFailure {
                        infoLog("보이스 파일 다운로드 실패: ${it.localizedMessage}\n${
                            it.stackTrace.joinToString("\n")
                        }")
                        audioFile = null
                    }
                    if (audioFile?.exists() == false || audioFile?.canRead() == false) {
                        audioFile = null
                    }

                    withContext(Dispatchers.Main) {
                        setAudioDuration()
                    }
                }

                vvVoiceVisualizer.visibility = View.VISIBLE
                vvVoiceVisualizer.numColumns = 8
                vvVoiceVisualizer.reset()
                vvVoiceVisualizer.drawDefaultView()

                audioDuration = 0
                replayTime = 0
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        private suspend fun getVoiceFileSize(chat: VoiceChat): Int = withContext(Dispatchers.IO) {
            if (chat.url.isBlank() || chat.url == "index") return@withContext -1

            var conn: URLConnection? = null
            val size = try {
                val url = URL(chat.url)
                conn = url.openConnection()
                if (conn is HttpURLConnection) {
                    conn.requestMethod = "HEAD"
                }
                conn?.getInputStream()
                conn?.contentLength
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            } finally {
                if (conn is HttpURLConnection) {
                    conn.disconnect()
                }
            }
            return@withContext size ?: -1
        }

        private suspend fun downloadVoiceFile(chat: VoiceChat) = suspendCoroutine { continuation ->
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val file = File(binding.root.context.cacheDir, "${chat.index}.wav")
            URL(chat.url).openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                    continuation.resume(file)
                }
            }
        }

        private fun setAudioDuration() {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(binding.root.context, Uri.fromFile(audioFile ?: return))
            val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            if (duration != null) {
                audioDuration = duration.toLong()
            }
            setReplayTimeText(audioDuration)
        }

        private fun setReplayTimeText(replayTime: Long) = binding.run {
            val totalSeconds = replayTime / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            val minutesStr = if (minutes < 10) "0$minutes" else minutes.toString()
            val secondsStr = if (seconds < 10) "0$seconds" else seconds.toString()
            tvReplayTime.text = "$minutesStr:$secondsStr"
        }


        fun replay() = binding.run {
            if (audioFile == null || replayer?.isReplaying == true) return@run

            replayer = try {
                RecordingReplayer(root.context, audioFile!!, vvVoiceVisualizer)
            } catch (e: Exception) {
                infoLog("오디오 재생 실패: ${e.localizedMessage}\n${e.stackTrace.joinToString("\n")}")
                return@run
            }
            vvCount = 0
            replayer?.replay()

            ivReplay.visibility = View.GONE
            ivPause.visibility = View.VISIBLE
            vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.main_500))
            isPaused = false

            timer = Timer()
            timer?.schedule(object: TimerTask(){
                override fun run() {
                    if (replayer?.isReplaying == true) {
                        replayTime += 100

                        if (replayTime <= audioDuration) {
                            CoroutineScope(Dispatchers.Main).launch {
                                setReplayTimeText(replayTime)
                            }
                        }
                    }
                    if (replayer?.isCompleted == true) {
                        CoroutineScope(Dispatchers.Main).launch {
                            reset(isCompleted = true)
                        }
                    }
                }
            }, 100, 100)
        }

        fun pause() = binding.run {
            replayer?.pause()
            isPaused = true

            vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.gray_700))
            ivReplay.visibility = View.VISIBLE
            ivPause.visibility = View.GONE
        }

        fun resume() = binding.run {
            replayer?.resume()
            isPaused = false

            vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.main_500))
            ivReplay.visibility = View.GONE
            ivPause.visibility = View.VISIBLE
        }

        fun reset(isCompleted: Boolean = false) = binding.run {
            isPaused = false
            replayTime = 0
            setAudioDuration()

            replayer?.stop()
            replayer = null
            timer?.cancel()
            timer = null

            ivReplay.visibility = View.VISIBLE
            ivPause.visibility = View.GONE

            if (!isCompleted) {
                vvVoiceVisualizer.reset()
                vvVoiceVisualizer.drawDefaultView()
            }
            vvVoiceVisualizer.setRenderColor(root.context.getColor(R.color.gray_700))
        }


        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }


    inner class ImageChatMineViewHolder(
        val binding: ItemImageChatMineBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as ImageChat

            binding.run {
                ivImage.updateLayoutParams {
                    width = chat.width
                    height = chat.height
                }

                Glide.with(root).run {
                    if (chat.file != null) {
                        load(chat.file)
                    } else if (chat.url != null) {
                        load(chat.url)
                    } else {
                        load(chat.uri)
                    }
                }.override(chat.width, chat.height)
                    .placeholder(R.drawable.n_background_image_chat_placeholder)
                    .error(R.drawable.n_background_image_chat_placeholder)
                    .fallback(R.drawable.n_background_image_chat_placeholder)
                    .into(ivImage)

                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                tlTransformation.transitionName = chat.index.toString()
                mcvChatContainer.setOnClickListener { onClick(tlTransformation, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }



        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class ImageChatPartnerViewHolder(
        val binding: ItemImageChatPartnerBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as ImageChat

            binding.run {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)

                setPartnerInfo(partnerNickname, partnerSignal)

                ivImage.updateLayoutParams {
                    width = chat.width
                    height = chat.height
                }

                Glide.with(root).run {
                    if (chat.file != null) {
                        load(chat.file)
                    } else if (chat.url != null) {
                        load(chat.url)
                    } else {
                        load(chat.uri)
                    }
                }.override(chat.width, chat.height)
                    .placeholder(R.drawable.n_background_image_chat_placeholder)
                    .error(R.drawable.n_background_image_chat_placeholder)
                    .fallback(R.drawable.n_background_image_chat_placeholder)
                    .apply(RequestOptions().override(chat.width, chat.height))
                    .into(ivImage)

                tlTransformation.transitionName = chat.index.toString()
                mcvChatContainer.setOnClickListener { onClick(tlTransformation, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }


    inner class SignalChatMineViewHolder(
        val binding: ItemSignalChatMineBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as SignalChat
            binding.run {
                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                when (chat.signal) {
                    Signal.Zero -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_0)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_0)
                    }
                    Signal.Quarter -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_25)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_25)
                    }
                    Signal.Half -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_50)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_50)
                    }
                    Signal.ThreeFourth -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_75)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_75)
                    }
                    Signal.One -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_100)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_100)
                    }
                }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class SignalChatPartnerViewHolder(
        val binding: ItemSignalChatPartnerBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as SignalChat
            binding.run {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)

                setPartnerInfo(partnerNickname, partnerSignal)

                when (chat.signal) {
                    Signal.Zero -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_0)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_0)
                    }
                    Signal.Quarter -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_25)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_25)
                    }
                    Signal.Half -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_50)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_50)
                    }
                    Signal.ThreeFourth -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_75)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_75)
                    }
                    Signal.One -> {
                        ivSignal.setImageResource(R.drawable.n_image_home_signal_100)
                        tvSignalSubtitle.setText(R.string.signal_subtitle_100)
                    }
                }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }


    inner class ChallengeChatMineViewHolder(
        val binding: ItemChallengeChatMineBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as ChallengeChat
            binding.run {
                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                tvChallengeTitle.text = chat.challenge.title
                val dDay = ceil((chat.challenge.deadline.time - Date().time).toDouble() / Constants.ONE_DAY).toInt()
                tvDDay.text = if (dDay >= 999) {
                    root.context.getString(R.string.add_challenge_d_day_over)
                } else if (dDay == 0) {
                    root.context.getString(R.string.add_challenge_d_day_today)
                } else if (dDay < 0) {
                    root.context.getString(R.string.add_challenge_d_day_past) + dDay.absoluteValue
                }  else {
                    root.context.getString(R.string.add_challenge_d_day_normal) + dDay
                }

                mcvChallengeButton.setOnClickListener { onClick(root, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class ChallengeChatPartnerViewHolder(
        val binding: ItemChallengeChatPartnerBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as ChallengeChat
            binding.run {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)

                setPartnerInfo(partnerNickname, partnerSignal)

                tvChallengeTitle.text = chat.challenge.title
                val dDay = ceil((chat.challenge.deadline.time - Date().time).toDouble() / Constants.ONE_DAY).toInt()
                tvDDay.text = if (dDay >= 999) {
                    root.context.getString(R.string.add_challenge_d_day_over)
                } else if (dDay == 0) {
                    root.context.getString(R.string.add_challenge_d_day_today)
                } else if (dDay < 0) {
                    root.context.getString(R.string.add_challenge_d_day_past) + dDay.absoluteValue
                } else {
                    root.context.getString(R.string.add_challenge_d_day_normal) + dDay
                }

                mcvChallengeButton.setOnClickListener { onClick(root, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }
    
    
    inner class AddChallengeChatMineViewHolder(
        val binding: ItemAddChallengeChatMineBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as AddChallengeChat
            binding.run {
                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                tvChallengeTitle.text = chat.challenge.title
                val dDay = ceil((chat.challenge.deadline.time - Date().time).toDouble() / Constants.ONE_DAY).toInt()
                tvDDay.text = if (dDay >= 999) {
                    root.context.getString(R.string.add_challenge_d_day_over)
                } else if (dDay == 0) {
                    root.context.getString(R.string.add_challenge_d_day_today)
                } else if (dDay < 0) {
                    root.context.getString(R.string.add_challenge_d_day_past) + dDay.absoluteValue
                } else {
                    root.context.getString(R.string.add_challenge_d_day_normal) + dDay
                }

                mcvChallengeButton.setOnClickListener { onClick(root, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class AddChallengeChatPartnerViewHolder(
        val binding: ItemAddChallengeChatPartnerBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as AddChallengeChat
            binding.run {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)

                setPartnerInfo(partnerNickname, partnerSignal)

                tvChallengeTitle.text = chat.challenge.title
                val dDay = ceil((chat.challenge.deadline.time - Date().time).toDouble() / Constants.ONE_DAY).toInt()
                tvDDay.text = if (dDay >= 999) {
                    root.context.getString(R.string.add_challenge_d_day_over)
                } else if (dDay == 0) {
                    root.context.getString(R.string.add_challenge_d_day_today)
                } else if (dDay < 0) {
                    root.context.getString(R.string.add_challenge_d_day_past) + dDay.absoluteValue
                } else {
                    root.context.getString(R.string.add_challenge_d_day_normal) + dDay
                }

                mcvChallengeButton.setOnClickListener { onClick(root, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }
    

    inner class CompleteChallengeChatMineViewHolder(
        val binding: ItemCompleteChallengeChatMineBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as CompleteChallengeChat
            binding.run {
                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                tvChallengeTitle.text = chat.challenge.title

                val formatter = SimpleDateFormat(root.context.getString(R.string.complete_challenge_chat_date_format), Locale.getDefault())
                val str = chat.challenge.completeDate?.let { formatter.format(it) }
                tvChallengeSuccessDate.text = str

                makeContinuous(prevItem, item, nextItem)
            }
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class CompleteChallengeChatPartnerViewHolder(
        val binding: ItemCompleteChallengeChatPartnerBinding,
    ) : ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as CompleteChallengeChat
            binding.run {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)

                setPartnerInfo(partnerNickname, partnerSignal)

                tvChallengeTitle.text = chat.challenge.title

                val formatter = SimpleDateFormat(root.context.getString(R.string.complete_challenge_chat_date_format), Locale.getDefault())
                val str = chat.challenge.completeDate?.let { formatter.format(it) }
                tvChallengeSuccessDate.text = str

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }
    

    inner class QuestionChatMineViewHolder(
        private val binding: ItemQuestionChatMineBinding
    ): ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as QuestionChat
            binding.run {
                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                tvQuestionHeader.text = root.context.getString(R.string.question_chat_header_deco) + chat.question.header
                tvQuestionBody.text = chat.question.body
                tvMyAnswer.text = chat.question.myAnswer
                tvPartnerAnswer.text = chat.question.partnerAnswer

                val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = serverFormat.parse(chat.question.createAt)
                val questionFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                tvQuestionDate.text = date?.let { questionFormat.format(it) }

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                tvMyAnswer.setOnLongClickListener {
                    val myAnswer = chat.question.myAnswer ?: return@setOnLongClickListener false
                    copyText(myAnswer)
                    false
                }
                tvPartnerAnswer.setOnLongClickListener {
                    val partnerAnswer = chat.question.partnerAnswer ?: return@setOnLongClickListener false
                    copyText(partnerAnswer)
                    false
                }

                makeContinuous(prevItem, item, nextItem)
            }
        }


        private fun copyText(text: String) {
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("채팅", text)
            clipboard.setPrimaryClip(clip)
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class QuestionChatPartnerViewHolder(
        private val binding: ItemQuestionChatPartnerBinding
    ): ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as QuestionChat
            binding.run {

                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)

                setPartnerInfo(partnerNickname, partnerSignal)

                tvQuestionHeader.text = root.context.getString(R.string.question_chat_header_deco) + chat.question.header
                tvQuestionBody.text = chat.question.body
                tvMyAnswer.text = chat.question.myAnswer
                tvPartnerAnswer.text = chat.question.partnerAnswer

                val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = serverFormat.parse(chat.question.createAt)
                val questionFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                tvQuestionDate.text = date?.let { questionFormat.format(it) }

                tvMyAnswer.setOnLongClickListener {
                    val myAnswer = chat.question.myAnswer ?: return@setOnLongClickListener false
                    copyText(myAnswer)
                    false
                }
                tvPartnerAnswer.setOnLongClickListener {
                    val partnerAnswer = chat.question.partnerAnswer ?: return@setOnLongClickListener false
                    copyText(partnerAnswer)
                    false
                }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        private fun copyText(text: String) {
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("채팅", text)
            clipboard.setPrimaryClip(clip)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }


    inner class AnswerChatMineViewHolder(
        private val binding: ItemAnswerChatMineBinding
    ): ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as AnswerChat
            binding.run {
                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                tvQuestionHeader.text = root.context.getString(R.string.question_chat_header_deco) + chat.question.header
                tvQuestionBody.text = chat.question.body

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                mcvAnswer.setOnClickListener { onClick(root, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }


        private fun copyText(text: String) {
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("채팅", text)
            clipboard.setPrimaryClip(clip)
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class AnswerChatPartnerViewHolder(
        private val binding: ItemAnswerChatPartnerBinding
    ): ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as AnswerChat
            binding.run {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)

                setPartnerInfo(partnerNickname, partnerSignal)

                tvQuestionHeader.text = root.context.getString(R.string.question_chat_header_deco) + chat.question.header
                tvQuestionBody.text = chat.question.body

                mcvAnswer.setOnClickListener { onClick(root, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        private fun copyText(text: String) {
            val clipboard = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("채팅", text)
            clipboard.setPrimaryClip(clip)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }


    inner class PokeChatMineViewHolder(
        private val binding: ItemPokeChatMineBinding
    ): ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as PokeChat
            binding.run {
                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                mcvAnswer.setOnClickListener { onClick(root, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class PokeChatPartnerViewHolder(
        private val binding: ItemPokeChatPartnerBinding
    ): ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as PokeChat
            binding.run {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)

                setPartnerInfo(partnerNickname, partnerSignal)

                mcvAnswer.setOnClickListener { onClick(root, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }


    inner class ResetPartnerPasswordChatMineViewHolder(
        private val binding: ItemResetPartnerPasswordChatMineBinding
    ): ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as ResetPartnerPasswordChat
            binding.run {
                tvRead.text = root.context.getString(
                    if (isPartnerInChat || chat.isRead) R.string.chat_read
                    else R.string.chat_unread
                )
                tvTime.text = getFormatted(chat.createAt)

                applyChatSendState(chat.sendState)
                ivRetry.setOnClickListener { onRetry(chat) }
                ivCancel.setOnClickListener { onCancel(chat) }

                mcvHelp.isEnabled = false
                mcvHelp.setOnClickListener(null)

                makeContinuous(prevItem, item, nextItem)
            }
        }

        private fun applyChatSendState(state: Chat.ChatSendState) = binding.run {
            tvRead.visibility = View.GONE
            tvTime.visibility = View.GONE
            ivSending.visibility = View.GONE
            llFailed.visibility = View.GONE
            when (state) {
                is Chat.ChatSendState.Sending -> {
                    ivSending.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Failed -> {
                    llFailed.visibility = View.VISIBLE
                }
                Chat.ChatSendState.Completed -> {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }
            }
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            if (item.sendState != Chat.ChatSendState.Completed) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return@run
            }

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = (nextItem?.sendState != Chat.ChatSendState.Completed) || (isTopSame && !isBottomSame)
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvRead.visibility = View.VISIBLE
                tvTime.visibility = View.VISIBLE


                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }

    inner class ResetPartnerPasswordChatPartnerViewHolder(
        private val binding: ItemResetPartnerPasswordChatPartnerBinding
    ): ChatViewHolder(binding.root) {

        override fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?) {
            val chat = item as ResetPartnerPasswordChat
            binding.run {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.VISIBLE

                tvTime.text = getFormatted(chat.createAt)

                setPartnerInfo(partnerNickname, partnerSignal)

                mcvHelp.setOnClickListener { onClick(root, chat) }

                makeContinuous(prevItem, item, nextItem)
            }
        }

        override fun setPartnerInfo(nickname: String?, signal: Signal) = binding.run {
            tvPartnerNickname.text = partnerNickname
            val signalRes = when (partnerSignal) {
                Signal.One -> R.drawable.n_image_signal_100
                Signal.ThreeFourth -> R.drawable.n_image_signal_75
                Signal.Half -> R.drawable.n_image_signal_50
                Signal.Quarter -> R.drawable.n_image_signal_25
                Signal.Zero -> R.drawable.n_image_signal_0
            }
            ivPartnerProfile.setImageResource(signalRes)
        }

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.sendState != Chat.ChatSendState.Completed) return

            val isBottomSame = item.chatSender == nextItem?.chatSender && item.createAt.substringBeforeLast(":") == nextItem.createAt.substringBeforeLast(":")
            val isTopSame = prevItem?.chatSender == item.chatSender && prevItem.createAt.substringBeforeLast(":") == item.createAt.substringBeforeLast(":")

            val isStartChat = !isTopSame && isBottomSame
            val isEndChat = isTopSame && !isBottomSame
            val isMiddleChat = isTopSame && isBottomSame

            if (isMiddleChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
                return@run
            }

            if (isStartChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = defaultVerticalMargin
                    bottomMargin = continuousVerticalMargin
                }
                tvTime.visibility = View.GONE
                llPartnerInfo.visibility = View.VISIBLE
            }

            if (isEndChat) {
                root.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = continuousVerticalMargin
                    bottomMargin = defaultVerticalMargin
                }
                tvTime.visibility = View.VISIBLE
                llPartnerInfo.visibility = View.GONE

                val position = snapshot().items.indexOf(prevItem)
                val prevPrevItem = if (position - 1 < 0) null
                else snapshot().items[position - 1]
                if (prevItem != null && prevPrevItem != null) {
                    viewHolders[prevItem]?.makeContinuous(prevPrevItem, prevItem, item)
                }
            }
        }
    }
}