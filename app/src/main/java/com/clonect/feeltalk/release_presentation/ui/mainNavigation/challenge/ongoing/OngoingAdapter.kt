package com.clonect.feeltalk.release_presentation.ui.mainNavigation.challenge.ongoing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.plusDayBy
import com.clonect.feeltalk.databinding.ItemChallengeOngoingBinding
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_presentation.ui.util.dpToPx
import java.util.Date
import kotlin.math.absoluteValue
import kotlin.math.ceil

class OngoingAdapter: PagingDataAdapter<Challenge, OngoingAdapter.OngoingChallengeViewHolder>(callback) {

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

    private var isFirstImminentItem = true
    private var onFirstImminentItemShow: () -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingChallengeViewHolder {
        val binding = ItemChallengeOngoingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OngoingChallengeViewHolder((binding))
    }

    override fun onBindViewHolder(holder: OngoingChallengeViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }


    fun setOnItemClickListener(listener: (Challenge) -> Unit) {
        onItemClick = listener
    }

    fun setOnFirstImminentItemListener(listener: () -> Unit) {
        onFirstImminentItemShow = listener
    }


    fun calculateDDay(from: Date, target: Date): Int {
        return ceil((target.time - from.time).toDouble() / Constants.ONE_DAY).toInt()
    }


    inner class OngoingChallengeViewHolder(
        val binding: ItemChallengeOngoingBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Challenge) {
            binding.run {
                root.setOnClickListener { onItemClick(item) }

                tvChallengeTitle.text = item.title
                tvNickname.text = item.owner

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

                if (item.isNew) {
                    ivNew.visibility = View.VISIBLE
                } else {
                    ivNew.visibility = View.GONE
                }

                if (item.deadline <= Date().plusDayBy(7)) {
                    mcvDDay.setCardBackgroundColor(root.context.getColor(R.color.main_300))
                    tvDDay.setTextColor(root.context.getColor(R.color.main_500))
                    tvDDay.typeface = ResourcesCompat.getFont(root.context, R.font.pretendard_semi_bold)
                    mcvOngoing.stroke_Width = root.context.dpToPx(2f).toFloat()

                    if (isFirstImminentItem) {
                        isFirstImminentItem = false
                        onFirstImminentItemShow()
                    }
                } else {
                    mcvDDay.setCardBackgroundColor(root.context.getColor(R.color.gray_200))
                    tvDDay.setTextColor(root.context.getColor(R.color.gray_600))
                    tvDDay.typeface = ResourcesCompat.getFont(root.context, R.font.pretendard_regular)
                    mcvOngoing.stroke_Width = 0f
                }
            }
        }
    }
}