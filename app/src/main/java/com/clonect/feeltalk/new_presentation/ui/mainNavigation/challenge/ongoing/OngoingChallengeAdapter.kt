package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoing

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.databinding.ItemChallengeOngoingEnoughBinding
import com.clonect.feeltalk.databinding.ItemChallengeOngoingImminentBinding
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.getScreenWidth
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.ceil

class OngoingChallengeAdapter(): RecyclerView.Adapter<OngoingChallengeAdapter.OngoingChallengeViewHolder>() {

    companion object {
        const val CHALLENGE_IMMINENT = 0
        const val CHALLENGE_ENOUGH = 1
    }

    private val callback = object: DiffUtil.ItemCallback<Challenge>() {
        override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    private var onItemClick: ((Challenge) -> Unit) = {}
    private var enoughItemSize: Int = 0
    private var imminentItemSize: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingChallengeViewHolder {
        return if (viewType == CHALLENGE_IMMINENT) {
            val binding = ItemChallengeOngoingImminentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ImminentOngoingChallengeViewHolder((binding))
        } else {
            val binding = ItemChallengeOngoingEnoughBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EnoughOngoingChallengeViewHolder((binding))
        }
    }

    override fun onBindViewHolder(holder: OngoingChallengeViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.onBind(item)
    }

    override fun getItemCount() = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        val item = differ.currentList[position]
        val isImminentItem = differ.currentList.all { item.deadline <= it.deadline }
        return if (isImminentItem)
            CHALLENGE_IMMINENT
        else
            CHALLENGE_ENOUGH
    }


    fun setOnItemClickListener(listener: (Challenge) -> Unit) {
        onItemClick = listener
    }

    fun calculateDDay(from: Date, target: Date): Int {
        return ceil(((from.time - target.time).toDouble() / Constants.ONE_DAY).absoluteValue).toInt()
    }

    fun calculateItemSize(activity: Activity) {
        val screenWidth = activity.getScreenWidth()
        // 12.5 * 2 + 7.5 * 2 = 40
        imminentItemSize = screenWidth - activity.dpToPx(40f).toInt()
        // 12.5 * 2 + 7.5 * 4 = 55
        enoughItemSize = (screenWidth - activity.dpToPx(56f).toInt()) / 2
    }


    inner class ImminentOngoingChallengeViewHolder(
        val binding: ItemChallengeOngoingImminentBinding
    ): OngoingChallengeViewHolder(binding.root) {

        override fun onBind(item: Challenge) {
            binding.run {
                root.layoutParams.width = imminentItemSize

                tvChallengeTitle.text = item.title
                tvNickname.text = item.owner

                val now = Date()
                val dDay = calculateDDay(now, item.deadline)

                tvDDay.text = binding.root.context.getString(R.string.ongoing_challenge_d_day_deco) + dDay.toString()
            }
        }
    }

    inner class EnoughOngoingChallengeViewHolder(
        val binding: ItemChallengeOngoingEnoughBinding
    ): OngoingChallengeViewHolder(binding.root) {

        override fun onBind(item: Challenge) {
            binding.run {
                root.layoutParams.width = enoughItemSize

                tvChallengeTitle.text = item.title
                tvNickname.text = item.owner

                val now = Date()
                val dDay = calculateDDay(now, item.deadline)

                tvDDay.text = binding.root.context.getString(R.string.ongoing_challenge_d_day_deco) + dDay.toString()
            }
        }
    }

    abstract class OngoingChallengeViewHolder(root: View): RecyclerView.ViewHolder(root)  {
        abstract fun onBind(item: Challenge)
    }
}