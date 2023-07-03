package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.*
import com.clonect.feeltalk.new_domain.model.chat.*
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer.RecordingReplayer
import com.clonect.feeltalk.presentation.utils.infoLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
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

    private val differ = AsyncListDiffer(this, callback)
    private val viewHolders = mutableListOf<ChatViewHolder>()
    private val voiceViewHolders = mutableListOf<ChatViewHolder>()


    private var onQuestionAnswerButtonClick: ((QuestionChat) -> Unit)? = null

    private var myNickname: String? = null
    private var partnerNickname: String? = null
    private var partnerProfileUrl: String? = null


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
            else -> {
                val binding = ItemChatDividerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChatDividerViewHolder(binding)
            }
        }
        voiceViewHolders.add(viewHolder)
        return viewHolder
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

    fun submitList(newList: List<Chat>) {
        differ.submitList(newList)
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

class VoiceChatMineViewHolder(
    val binding: ItemVoiceChatMineBinding,
) : ChatViewHolder(binding.root) {

    var audioDuration: Long = 0
    var audioFile: File? = null
    var replayer: RecordingReplayer? = null
    var timer: Timer? = null
    var replayTime: Long = 0
    var isPaused = false

    override fun bind(item: Chat) {
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

            tvTime.text = getFormatted(chat.createAt)

            ivReplay.setOnClickListener {
                if (isPaused) resume()
                else replay()
            }
            ivPause.setOnClickListener { pause() }
        }
    }

    private fun init(chat: VoiceChat) = binding.run {
        if (audioFile == null) {
            // TODO 나중에 오디오 파일 다운로드 해야댐
            audioFile = File(root.context.cacheDir, chat.url)
            audioFile = audioFile?.copyTo(
                target = File(root.context.cacheDir, chat.index.toString() + "" + chat.url),
                overwrite = true
            )

            vvVoiceVisualizer.visibility = View.VISIBLE
            vvVoiceVisualizer.numColumns = 8
            vvVoiceVisualizer.reset()
            vvVoiceVisualizer.drawDefaultView()

            audioDuration = 0
            replayTime = 0
            setAudioDuration()
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

        replayer = RecordingReplayer(root.context, audioFile!!, vvVoiceVisualizer)
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
}

class VoiceChatPartnerViewHolder(
    val binding: ItemVoiceChatPartnerBinding,
) : ChatViewHolder(binding.root) {

    var audioDuration: Long = 0
    var audioFile: File? = null
    var replayer: RecordingReplayer? = null
    var timer: Timer? = null
    var replayTime: Long = 0
    var isPaused = false

    override fun bind(item: Chat) {
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

            tvTime.text = getFormatted(chat.createAt)

            ivReplay.setOnClickListener {
                if (isPaused) resume()
                else replay()
            }
            ivPause.setOnClickListener { pause() }
        }
    }

    private fun init(chat: VoiceChat) = binding.run {
        if (audioFile == null) {
            // TODO 나중에 오디오 파일 다운로드 해야댐
            audioFile = File(root.context.cacheDir, chat.url)
            audioFile = audioFile?.copyTo(
                target = File(root.context.cacheDir, chat.index.toString() + "" + chat.url),
                overwrite = true
            )

            vvVoiceVisualizer.visibility = View.VISIBLE
            vvVoiceVisualizer.numColumns = 8
            vvVoiceVisualizer.reset()
            vvVoiceVisualizer.drawDefaultView()

            audioDuration = 0
            replayTime = 0
            setAudioDuration()
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

        replayer = RecordingReplayer(root.context, audioFile!!, vvVoiceVisualizer)
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
}