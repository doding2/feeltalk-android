package com.clonect.feeltalk.new_presentation.ui.chatNavigation.contentsShare.questionShare

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemQuestionShareBinding
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx

class QuestionShareAdapter: PagingDataAdapter<Question, QuestionShareAdapter.QuestionViewHolder>(callback) {

    companion object {
        private val callback = object: DiffUtil.ItemCallback<Question>() {
            override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem.index == newItem.index
            }

            override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var selectedIndex: Long? = null
    private var onItemSelect: ((Question?) -> Unit) = {}
    private val viewHolders = mutableListOf<QuestionViewHolder>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionShareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = QuestionViewHolder(binding)
        viewHolders.add(holder)
        return holder
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    fun setOnItemSelectListener(listener: (Question?) -> Unit) {
        onItemSelect = listener
    }

    fun unselectAllItems() {
        for (holder in viewHolders) {
            holder.setViewUnselected()
        }
    }


    inner class QuestionViewHolder(
        val binding: ItemQuestionShareBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Question) = binding.run {
            val existAllAnswers = item.myAnswer != null && item.partnerAnswer != null
            if (existAllAnswers) {
                mcvIndex.setCardBackgroundColor(binding.root.context.getColor(R.color.gray_200))
                tvIndex.setTextColor(Color.BLACK)
                tvNew.visibility = View.GONE
            } else {
                mcvIndex.setCardBackgroundColor(binding.root.context.getColor(R.color.main_300))
                tvIndex.setTextColor(binding.root.context.getColor(R.color.main_500))
                tvNew.visibility = View.VISIBLE
            }

            tvIndex.text = item.index.toString()
            tvQuestionBody.text = item.body


            if (selectedIndex == item.index) {
                setViewSelected()
            } else {
                setViewUnselected()
            }

            root.setOnClickListener {
                if (selectedIndex == item.index) {
                    unselect()
                } else {
                    select(item)
                }
            }
        }

        private fun select(item: Question) = binding.run {
            onItemSelect(item)
            selectedIndex = item.index
            unselectAllItems()
            setViewSelected()
        }

        private fun unselect() = binding.run {
            onItemSelect(null)
            selectedIndex = null
            unselectAllItems()
            setViewUnselected()
        }


        fun setViewSelected() = binding.run {
            root.strokeWidth = root.context.dpToPx(2f).toInt()
        }

        fun setViewUnselected() = binding.run {
            root.strokeWidth = 0
        }
    }
}