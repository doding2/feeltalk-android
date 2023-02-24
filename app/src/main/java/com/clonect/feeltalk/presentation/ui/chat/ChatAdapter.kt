package com.clonect.feeltalk.presentation.ui.chat

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemChatMineBinding
import com.clonect.feeltalk.databinding.ItemChatPartnerBinding
import com.clonect.feeltalk.domain.model.data.chat.Chat

class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MINE = 0
        private const val TYPE_PARTNER = 1
    }

    private val callback = object: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.date == newItem.date && oldItem.owner == newItem.owner && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    private var onItemClickListener: ((Chat) -> Unit)? = null

    private var partnerNickname: String? = null
    private var partnerProfileUrl: String? = null
    private var myProfileUrl: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_MINE) {
            val binding = ItemChatMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ChatMineViewHolder(binding)
        } else {
            val binding = ItemChatPartnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ChatPartnerViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = differ.currentList[position]

        when (holder) {
            is ChatAdapter.ChatMineViewHolder -> holder.bind(chat)
            is ChatAdapter.ChatPartnerViewHolder -> holder.bind(chat)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        val question = differ.currentList[position]
        return if (question.owner == "mine") TYPE_MINE     // TODO 내 이메일 바꾸기
        else TYPE_PARTNER
    }

    fun setOnItemClickListener(listener: (Chat) -> Unit) {
        onItemClickListener = listener
    }

    fun setPartnerNickname(nickname: String) {
        partnerNickname = nickname
    }


    fun setMyProfileUrl(url: String?) {
        myProfileUrl = url
    }

    fun setPartnerProfileUrl(url: String?) {
        partnerProfileUrl = url
    }

    private fun ImageView.setProfileImageUrl(url: String?) {
        Glide.with(this)
            .load(url)
            .circleCrop()
            .fallback(R.drawable.image_my_default_profile)
            .error(R.drawable.image_my_default_profile)
            .into(this)
    }


    inner class ChatMineViewHolder(
        val binding: ItemChatMineBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            binding.apply {
                if (chat.isAnswer) {
                    layoutChatOwner.visibility = View.VISIBLE
                    binding.ivMyProfile.setProfileImageUrl(myProfileUrl)
                }
                else
                    layoutChatOwner.visibility = View.GONE

                textChat.text = chat.message
                textDate.text = chat.date

                root.setOnClickListener { _ ->
                    onItemClickListener?.let { it(chat) }
                }
            }
        }
    }

    inner class ChatPartnerViewHolder(
        val binding: ItemChatPartnerBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            binding.apply {
                if (chat.isAnswer) {
                    textPartnerName.text = partnerNickname
                    layoutChatOwner.visibility = View.VISIBLE
                    binding.ivMyProfile.setProfileImageUrl(partnerProfileUrl)
                }
                else
                    layoutChatOwner.visibility = View.GONE


                textDate.text = chat.date


                val isWaitingChat = chat.run {
                    id == -1L  && message == "" && date == "" && isAnswer
                }
                if (isWaitingChat) {
                    val partner = binding.root.context.getString(R.string.today_question_partner_state_not_done_prefix)
                    val answer = binding.root.context.getString(R.string.today_question_partner_state_not_done)
                    val emoji = binding.root.context.getString(R.string.today_question_partner_state_not_done_emoji)
                    val waitingMessage = "\n   $partner$answer$emoji   \n"
                    val body = SpannableString(waitingMessage).apply {
                        setSpan(StyleSpan(Typeface.BOLD), waitingMessage.indexOf(answer), waitingMessage.length, 0)
                        setSpan(UnderlineSpan(), waitingMessage.indexOf(answer), waitingMessage.indexOf(emoji), 0)
                    }
                    textChat.text = body
                } else {
                    textChat.text = chat.message
                }

                root.setOnClickListener { _ ->
                    onItemClickListener?.let { it(chat) }
                }
            }
        }
    }

}