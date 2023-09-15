package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.lockQuestionSetting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemLockQuestionBinding
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerInterface
import com.skydoves.powerspinner.PowerSpinnerView

class LockQuestionAdapter(
    override val spinnerView: PowerSpinnerView,
    private var items: List<String>,
    override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<String>? = null
): RecyclerView.Adapter<LockQuestionAdapter.LockQuestionViewHolder>(), PowerSpinnerInterface<String> {

    override var index: Int = spinnerView.selectedIndex

    companion object {
        const val NO_SELECTED_INDEX = -1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LockQuestionViewHolder {
        val binding = ItemLockQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LockQuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LockQuestionViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size


    override fun notifyItemSelected(index: Int) {
        if (index == NO_SELECTED_INDEX) return
        val oldIndex = this.index
        val newItem = items[index]
        this.index = index
        this.spinnerView.notifyItemSelected(index, newItem)
        this.onSpinnerItemSelectedListener?.onItemSelected(
            oldIndex = oldIndex,
            oldItem = oldIndex.takeIf { it != NO_SELECTED_INDEX }?.let { items[oldIndex] },
            newIndex = index,
            newItem = newItem
        )
    }

    override fun setItems(itemList: List<String>) {
        items = itemList
    }


    inner class LockQuestionViewHolder(
        val binding: ItemLockQuestionBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            val index = items.indexOf(item)
            binding.run {
                root.setOnClickListener {
                    notifyItemSelected(index)
                }
                if (index == 1) {
                    root.setBackgroundColor(root.context.getColor(R.color.gray_100))
                } else {
                    root.setBackgroundColor(root.context.getColor(android.R.color.transparent))
                }
                tvLockQuestion.text = item
            }
        }
    }
}