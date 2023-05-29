package com.clonect.feeltalk.new_presentation.ui.main_navigation.question

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemQuestionBinding
import com.clonect.feeltalk.new_domain.model.question.Question

class QuestionAdapter(): RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    private val callback = object: DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem.index == newItem.index
        }

        override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    private var onItemClick: ((Question) -> Unit) = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.onBind(item)
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnItemClickListener(listener: (Question) -> Unit) {
        onItemClick = listener
    }


    inner class QuestionViewHolder(
        val binding: ItemQuestionBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: Question) = binding.run {
            root.setOnClickListener { onItemClick(item) }

            tvQuestionIndex.text = item.index.toString().padStart(3, '0') + "."
            tvQuestion.text = item.header + " " + item.body

            val isUserAnswered = item.myAnswer != null
            val questionColor = binding.root.context.getColor(
                if (isUserAnswered) R.color.gray_500
                else R.color.system_black
            )
            tvQuestion.setTextColor(questionColor)

        }
    }
}