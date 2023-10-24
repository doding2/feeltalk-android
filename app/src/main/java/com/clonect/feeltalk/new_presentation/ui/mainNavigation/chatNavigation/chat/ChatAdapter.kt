package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.chat

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
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemChatDividerBinding
import com.clonect.feeltalk.databinding.ItemQuestionChatMineBinding
import com.clonect.feeltalk.databinding.ItemQuestionChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemTextChatMineBinding
import com.clonect.feeltalk.databinding.ItemTextChatPartnerBinding
import com.clonect.feeltalk.databinding.ItemVoiceChatMineBinding
import com.clonect.feeltalk.databinding.ItemVoiceChatPartnerBinding
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatType
import com.clonect.feeltalk.new_domain.model.chat.DividerChat
import com.clonect.feeltalk.new_domain.model.chat.QuestionChat
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.model.chat.VoiceChat
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.chat.audioVisualizer.RecordingReplayer
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.presentation.utils.infoLog
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
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ChatAdapter: PagingDataAdapter<Chat, ChatAdapter.ChatViewHolder>(diffCallback) {

    private var myNickname: String? = null
    private var partnerNickname: String? = null
    private var partnerProfileUrl: String? = null

    private val viewHolders = mutableMapOf<Chat, ChatViewHolder>()
    private val voiceViewHolders = mutableListOf<ChatViewHolder>()

    private var isPartnerInChat = false

    private var onQuestionAnswerButtonClick: ((QuestionChat) -> Unit)? = null


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
            TYPE_QUESTION_MINE -> {
                val binding = ItemQuestionChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                QuestionChatMineViewHolder(binding)
            }
            TYPE_QUESTION_PARTNER -> {
                val binding = ItemQuestionChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                QuestionChatPartnerViewHolder(binding)
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
            else -> TYPE_DIVIDER
        }
    }

    fun resetVoiceChats() {
        for (holder in voiceViewHolders) {
            if (holder is VoiceChatMineViewHolder) holder.reset()
            if (holder is VoiceChatPartnerViewHolder) holder.reset()
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
                return oldItem.index == newItem.index && oldItem.createAt == newItem.createAt && oldItem.isSending == newItem.isSending
            }

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.index == newItem.index
                        && oldItem.type == newItem.type
                        && oldItem.chatSender == newItem.chatSender
                        && oldItem.isRead == newItem.isRead
                        && oldItem.createAt == newItem.createAt
                        && oldItem.isSending == newItem.isSending
            }
        }


        private const val TYPE_DIVIDER = 0

        const val TYPE_TEXT_MINE = 1
        const val TYPE_TEXT_PARTNER = 2

        const val TYPE_VOICE_MINE = 3
        const val TYPE_VOICE_PARTNER = 4

        const val TYPE_EMOJI_MINE = 5
        const val TYPE_EMOJI_PARTNER = 6

        const val TYPE_IMAGE_MINE = 7
        const val TYPE_IMAGE_PARTNER = 8

        const val TYPE_VIDEO_MINE = 9
        const val TYPE_VIDEO_PARTNER = 10

        const val TYPE_CHALLENGE_MINE = 11
        const val TYPE_CHALLENGE_PARTNER = 12

        const val TYPE_QUESTION_MINE = 13
        const val TYPE_QUESTION_PARTNER = 14
    }



    abstract class ChatViewHolder(val root: View): RecyclerView.ViewHolder(root) {

        val defaultVerticalMargin = root.context.dpToPx(8f).toInt()
        val continuousVerticalMargin = root.context.dpToPx(2f).toInt()

        abstract fun bind(prevItem: Chat?, item: Chat, nextItem: Chat?)

        open fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) { }

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

                if (chat.isSending) {
                    tvRead.visibility = View.GONE
                    tvTime.visibility = View.GONE
                } else {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }

                root.setOnLongClickListener {
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

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE

            if (item.isSending) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return
            }

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

//           TODO
//            tvPartnerNickname.text = "연인 닉네임"
//            ivPartnerProfile.setImageResource()

                root.setOnLongClickListener {
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

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            if (item.isSending)
                return

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
                    if (chat.isSending) return@setOnClickListener
                    if (isPaused) resume()
                    else replay()
                }
                ivPause.setOnClickListener { pause() }

                if (chat.isSending) {
                    tvRead.visibility = View.GONE
                    tvTime.visibility = View.GONE
                } else {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }

                CoroutineScope(Dispatchers.IO).launch {
                    vvCount = 5
                    while (vvCount > 0) {
                        vvVoiceVisualizer.reset()
                        vvVoiceVisualizer.drawDefaultView()
                        vvCount--
                        infoLog(vvCount.toString())
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
                mmr.setDataSource(binding.root.context, Uri.fromFile(audioFile))
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



        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE

            if (item.isSending) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return
            }

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
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(binding.root.context, Uri.fromFile(audioFile))
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

            if (item.isSending)
                return

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

                val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())
                val date = serverFormat.parse(chat.question.createAt)
                val questionFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                tvQuestionDate.text = date?.let { questionFormat.format(it) }

                if (chat.isSending) {
                    tvRead.visibility = View.GONE
                    tvTime.visibility = View.GONE
                } else {
                    tvRead.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }

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

        override fun makeContinuous(prevItem: Chat?, item: Chat, nextItem: Chat?) = binding.run {
            root.updateLayoutParams<RecyclerView.LayoutParams> {
                topMargin = defaultVerticalMargin
                bottomMargin = defaultVerticalMargin
            }

            tvRead.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE

            if (item.isSending) {
                tvRead.visibility = View.GONE
                tvTime.visibility = View.GONE
                return
            }

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

//           TODO
//            tvPartnerNickname.text = "연인 닉네임"
//            ivPartnerProfile.setImageResource()

                tvQuestionHeader.text = root.context.getString(R.string.question_chat_header_deco) + chat.question.header
                tvQuestionBody.text = chat.question.body
                tvMyAnswer.text = chat.question.myAnswer
                tvPartnerAnswer.text = chat.question.partnerAnswer

                val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())
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

            if (item.isSending)
                return

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