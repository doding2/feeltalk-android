package com.clonect.feeltalk.new_presentation.ui.mainNavigation.question

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemQuestionBinding
import com.clonect.feeltalk.databinding.ItemQuestionTodayBinding
import com.clonect.feeltalk.new_domain.model.question.Question

class QuestionAdapter: PagingDataAdapter<Question, QuestionAdapter.QuestionViewHolder>(callback) {

    companion object {
        private val callback = object: DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem.index == newItem.index
            }

            override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem == newItem
            }
        }

        const val TYPE_NORMAL_QUESTION = 0
        const val TYPE_TODAY_QUESTION = 1
    }


    private var onItemClick: ((Question) -> Unit) = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        return when (viewType) {
            TYPE_TODAY_QUESTION -> {
                val binding = ItemQuestionTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TodayQuestionViewHolder(binding)
            }
            TYPE_NORMAL_QUESTION -> {
                val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NormalQuestionViewHolder(binding)
            }
            else -> {
                val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NormalQuestionViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            TYPE_TODAY_QUESTION
        else
            TYPE_NORMAL_QUESTION
    }


    fun setOnItemClickListener(listener: (Question) -> Unit) {
        onItemClick = listener
    }



    abstract class QuestionViewHolder(
        val root: View
    ): RecyclerView.ViewHolder(root) {
        abstract fun bind(item: Question)
    }

    inner class TodayQuestionViewHolder(
        val binding: ItemQuestionTodayBinding
    ): QuestionViewHolder(binding.root) {

        override fun bind(item: Question) = binding.run {
            tvIndex.text = item.index.toString()
            tvTodayQuestionHeader.text = item.header
            tvTodayQuestionBody.text = item.body

            mcvAnswer.setOnClickListener { onItemClick(item) }

            val isUserAnswered = item.myAnswer != null
            if (isUserAnswered) {
                mcvAnswer.setCardBackgroundColor(Color.WHITE)
                mcvAnswer.setRippleColorResource(R.color.main_300)
                tvAnswerOrChat.setText(R.string.question_today_button_answer_2)
                tvAnswerOrChat.setTextColor(root.context.getColor(R.color.main_500))
            } else {
                mcvAnswer.setCardBackgroundColor(Color.BLACK)
                mcvAnswer.setRippleColorResource(R.color.system_alert)
                tvAnswerOrChat.setText(R.string.question_today_button_answer)
                tvAnswerOrChat.setTextColor(root.context.getColor(R.color.main_100))
            }
        }
    }

    inner class NormalQuestionViewHolder(
        val binding: ItemQuestionBinding
    ): QuestionViewHolder(binding.root) {

        override fun bind(item: Question) = binding.run {
            root.setOnClickListener { onItemClick(item) }

            tvIndex.text = item.index.toString()
            tvQuestionBody.text = item.body

//            val isUserAnswered = item.myAnswer != null
//            val questionColor = binding.root.context.getColor(
//                if (isUserAnswered) R.color.gray_500
//                else R.color.system_black
//            )
//            tvQuestionBody.setTextColor(questionColor)
        }
    }
}