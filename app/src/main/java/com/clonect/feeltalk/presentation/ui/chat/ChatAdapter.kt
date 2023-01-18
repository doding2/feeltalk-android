package com.clonect.feeltalk.presentation.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.databinding.ItemChatMineBinding
import com.clonect.feeltalk.databinding.ItemChatPartnerBinding
import com.clonect.feeltalk.domain.model.chat.Chat
import com.clonect.feeltalk.domain.model.question.Question
import com.clonect.feeltalk.presentation.ui.question_list.QuestionListAdapter

class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MINE = 0
        private const val TYPE_PARTNER = 1
    }

    private val callback = object: DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.date == newItem.date && oldItem.ownerEmail == newItem.ownerEmail && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    private var onItemClickListener: ((Chat) -> Unit)? = null


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
        return if (question.ownerEmail == "mine") TYPE_MINE     // TODO 내 이메일 바꾸기
        else TYPE_PARTNER
    }

    fun setOnItemClickListener(listener: (Chat) -> Unit) {
        onItemClickListener = listener
    }


    inner class ChatMineViewHolder(
        val binding: ItemChatMineBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            binding.apply {
                if (chat.isAnswer)
                    layoutChatOwner.visibility = View.VISIBLE
                else
                    layoutChatOwner.visibility = View.GONE

                textChat.text = chat.content
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
                if (chat.isAnswer)
                    layoutChatOwner.visibility = View.VISIBLE
                else
                    layoutChatOwner.visibility = View.GONE

                textPartnerName.text = chat.ownerEmail
                textChat.text = chat.content
                textDate.text = chat.date

                root.setOnClickListener { _ ->
                    onItemClickListener?.let { it(chat) }
                }
            }
        }
    }

}