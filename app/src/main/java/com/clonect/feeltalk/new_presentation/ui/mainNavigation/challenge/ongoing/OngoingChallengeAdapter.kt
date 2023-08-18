package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoing

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.plusDayBy
import com.clonect.feeltalk.databinding.ItemChallengeOngoingEnoughBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getScreenWidth
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.ceil

class OngoingChallengeAdapter: PagingDataAdapter<Challenge, OngoingChallengeAdapter.OngoingChallengeViewHolder>(callback) {

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
    private var onCompleteChallenge: (Challenge) -> Unit = {}
    private var enoughItemSize: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingChallengeViewHolder {
        val binding = ItemChallengeOngoingEnoughBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun setOnCompleteChallengeListener(listener: (Challenge) -> Unit) {
        onCompleteChallenge = listener
    }


    fun calculateDDay(from: Date, target: Date): Int {
        return ceil(((from.time - target.time).toDouble() / Constants.ONE_DAY).absoluteValue).toInt()
    }

    fun calculateItemSize(activity: Activity) {
        val screenWidth = activity.getScreenWidth()
        // 12.5 * 2 + 7.5 * 4 = 55
        enoughItemSize = (screenWidth - activity.applicationContext.dpToPx(56f).toInt()) / 2
    }


    inner class OngoingChallengeViewHolder(
        val binding: ItemChallengeOngoingEnoughBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Challenge) {
            binding.run {
                root.setOnClickListener { onItemClick(item) }
                ivComplete.setOnClickListener { onCompleteChallenge(item) }

                root.layoutParams.width = enoughItemSize

                tvChallengeTitle.text = item.title
                tvNickname.text = item.owner

                val now = Date()
                val dDay = calculateDDay(now, item.deadline)

                tvDDay.text = binding.root.context.getString(R.string.ongoing_challenge_d_day_deco) + dDay.toString()

                if (item.deadline <= Date().plusDayBy(7)) {
                    mcvDDay.setCardBackgroundColor(root.context.getColor(R.color.main_300))
                    tvDDay.setTextColor(root.context.getColor(R.color.main_500))
                    tvDDay.typeface = ResourcesCompat.getFont(root.context, R.font.pretendard_semi_bold)
                    mcvOngoing.strokeWidth = root.context.dpToPx(2f).toInt()
                } else {
                    mcvDDay.setCardBackgroundColor(root.context.getColor(R.color.gray_200))
                    tvDDay.setTextColor(Color.BLACK)
                    tvDDay.typeface = ResourcesCompat.getFont(root.context, R.font.pretendard_regular)
                    mcvOngoing.strokeWidth = 0
                }
            }
        }
    }
}