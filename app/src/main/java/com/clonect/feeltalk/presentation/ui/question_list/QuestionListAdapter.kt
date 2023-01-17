package com.clonect.feeltalk.presentation.ui.question_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.databinding.ItemQuestionListBinding
import com.clonect.feeltalk.databinding.ItemQuestionListHeaderBinding
import com.clonect.feeltalk.domain.model.question.Question

class QuestionListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private val callback = object: DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    private var onItemClickListener: ((Question) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val binding = ItemQuestionListHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            QuestionListHeaderViewHolder(binding)
        } else {
            val binding = ItemQuestionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            QuestionListViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val question = differ.currentList[position]

        if (holder is QuestionListViewHolder)
            holder.bind(question)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        val question = differ.currentList[position]
        return if (question.id == -50505L) TYPE_HEADER
        else TYPE_ITEM
    }

    fun setOnItemClickListener(listener: (Question) -> Unit) {
        onItemClickListener = listener
    }


    inner class QuestionListViewHolder(
        val binding: ItemQuestionListBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(question: Question) {
            binding.textQuestionTitle.text = question.content
            binding.root.setOnClickListener { _ ->
                onItemClickListener?.let { it(question) }
            }
        }
    }

    inner class QuestionListHeaderViewHolder(
        val binding: ItemQuestionListHeaderBinding
    ): RecyclerView.ViewHolder(binding.root)

}