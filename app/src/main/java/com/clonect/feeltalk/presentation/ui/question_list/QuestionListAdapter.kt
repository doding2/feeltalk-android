package com.clonect.feeltalk.presentation.ui.question_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.databinding.ItemQuestionListBinding
import com.clonect.feeltalk.databinding.ItemQuestionListHeaderBinding
import com.clonect.feeltalk.domain.model.data.question.Question2

class QuestionListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private val callback = object: DiffUtil.ItemCallback<Question2>() {
        override fun areItemsTheSame(oldItem: Question2, newItem: Question2): Boolean {
            return oldItem.question == newItem.question
        }

        override fun areContentsTheSame(oldItem: Question2, newItem: Question2): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    private var onItemClickListener: ((Question2) -> Unit)? = null


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
        return when (question.viewType) {
            "header" -> TYPE_HEADER
            "item" -> TYPE_ITEM
            else -> TYPE_ITEM
        }
    }

    fun setOnItemClickListener(listener: (Question2) -> Unit) {
        onItemClickListener = listener
    }


    inner class QuestionListViewHolder(
        val binding: ItemQuestionListBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(question2: Question2) {
            binding.textQuestionTitle.text = question2.question
            binding.root.setOnClickListener { _ ->
                onItemClickListener?.let { it(question2) }
            }
        }
    }

    inner class QuestionListHeaderViewHolder(
        val binding: ItemQuestionListHeaderBinding
    ): RecyclerView.ViewHolder(binding.root)

}