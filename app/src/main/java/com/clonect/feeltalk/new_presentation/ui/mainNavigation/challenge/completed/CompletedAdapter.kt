package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.completed

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.ItemChallengeCompletedBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getScreenWidth
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.ceil

class CompletedAdapter: PagingDataAdapter<Challenge, CompletedAdapter.CompletedChallengeViewHolder>(callback) {

    companion object {
        private val callback = object: DiffUtil.ItemCallback<Challenge>() {
            override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
                return oldItem.index == newItem.index
            }

            override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var onItemClick: ((Challenge) -> Unit) = {}
    private var itemSize: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedChallengeViewHolder {
        val binding = ItemChallengeCompletedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompletedChallengeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompletedChallengeViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }


    fun setOnItemClickListener(listener: (Challenge) -> Unit) {
        onItemClick = listener
    }

    fun calculateDDay(from: Date, target: Date): Int {
        return ceil(((from.time - target.time).toDouble() / Constants.ONE_DAY).absoluteValue).toInt()
    }

    fun calculateItemSize(activity: Activity) {
        val screenWidth = activity.getScreenWidth()
        // 12.5 * 2 + 7.5 * 4 = 55
        itemSize = (screenWidth - activity.applicationContext.dpToPx(56f).toInt()) / 2
    }


    inner class CompletedChallengeViewHolder(
        val binding: ItemChallengeCompletedBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Challenge) {
            binding.run {
                root.setOnClickListener { onItemClick(item) }
                root.layoutParams.width = itemSize

                tvChallengeTitle.text = item.title
                tvNickname.text = item.owner
            }
        }
    }
}