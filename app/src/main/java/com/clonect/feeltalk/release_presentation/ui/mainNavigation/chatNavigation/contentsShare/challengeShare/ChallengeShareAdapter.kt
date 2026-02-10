package com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.contentsShare.challengeShare

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.ItemChallengeShareBinding
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_presentation.ui.util.dpToPx
import java.util.Date
import kotlin.math.absoluteValue
import kotlin.math.ceil

class ChallengeShareAdapter : PagingDataAdapter<Challenge, ChallengeShareAdapter.ChallengeViewHolder>(callback) {

    private var selectedIndex: Long? = null
    private var onItemSelect: ((Challenge?) -> Unit) = {}
    private val viewHolders = mutableListOf<ChallengeViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ItemChallengeShareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ChallengeViewHolder(binding)
        viewHolders.add(holder)
        return holder
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    fun setOnItemSelectListener(listener: (Challenge?) -> Unit) {
        onItemSelect = listener
    }

    fun unselectAllItems() {
        for (holder in viewHolders) {
            holder.setViewUnselected()
        }
    }


    inner class ChallengeViewHolder(
        val binding: ItemChallengeShareBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Challenge) = binding.run {

            val now = Date()
            val dDay = calculateDDay(now, item.deadline)
            tvDDay.text = if (dDay >= 999) {
                root.context.getString(R.string.add_challenge_d_day_over)
            } else if (dDay == 0) {
                root.context.getString(R.string.add_challenge_d_day_today)
            } else if (dDay < 0){
                root.context.getString(R.string.add_challenge_d_day_past) + dDay.absoluteValue
            } else {
                root.context.getString(R.string.add_challenge_d_day_normal) + dDay
            }
            tvChallengeTitle.text = item.title
            tvNickname.text = item.owner

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

        fun calculateDDay(from: Date, target: Date): Int {
            return ceil((target.time - from.time).toDouble() / Constants.ONE_DAY).toInt()
        }


        private fun select(item: Challenge) = binding.run {
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
            mcvChallenge.strokeWidth = root.context.dpToPx(2f).toInt()
        }

        fun setViewUnselected() = binding.run {
            mcvChallenge.strokeWidth = 0
        }
    }



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
}