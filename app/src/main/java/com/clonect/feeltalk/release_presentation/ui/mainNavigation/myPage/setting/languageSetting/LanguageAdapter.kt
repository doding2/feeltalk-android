package com.clonect.feeltalk.release_presentation.ui.mainNavigation.myPage.setting.languageSetting

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemLanguageBinding
import com.clonect.feeltalk.release_domain.model.appSettings.Language

class LanguageAdapter(
    private var selectedItem: Language,
    private val onSelectItem: (item: Language) -> Unit
): RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private val items = listOf(
        Language.Korean,
        Language.English,
        Language.Japanese,
        Language.Chinese
    )
    private val viewHolders = mutableListOf<LanguageViewHolder>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = LanguageViewHolder(binding)
        viewHolders.add(holder)
        return holder
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size


    fun unselectAllItems() {
        for (holder in viewHolders) {
            holder.setViewSelected(false)
        }
    }

    inner class LanguageViewHolder(
        private val binding: ItemLanguageBinding
    ): ViewHolder(binding.root) {

        fun bind(item: Language) {
            binding.run {
                setViewSelected(item == selectedItem)
                root.setOnClickListener {
                    select(item)
                }
                tvLanguage.text = item.nativeName
            }
        }

        private fun select(item: Language) {
            selectedItem = item
            onSelectItem(item)
            unselectAllItems()
            setViewSelected(true)
        }

        fun setViewSelected(isSelected: Boolean) {
            binding.run {
                val backColor = if (isSelected) {
                    root.context.getColor(R.color.gray_200)
                } else {
                    Color.WHITE
                }
                root.setCardBackgroundColor(backColor)
            }
        }
    }
}