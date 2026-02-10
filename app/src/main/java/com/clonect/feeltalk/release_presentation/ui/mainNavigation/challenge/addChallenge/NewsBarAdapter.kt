package com.clonect.feeltalk.release_presentation.ui.mainNavigation.challenge.addChallenge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.databinding.ItemNewsBarBinding
import com.clonect.feeltalk.release_domain.model.challenge.NewsBarItem

class NewsBarAdapter(private val items: List<NewsBarItem>): RecyclerView.Adapter<NewsBarAdapter.NewsBarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsBarViewHolder {
        val binding = ItemNewsBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsBarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsBarViewHolder, position: Int) {
        val pos = position % items.size
        val item = items[pos]
        holder.bind(item)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE


    inner class NewsBarViewHolder(
        private val binding: ItemNewsBarBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewsBarItem) {
            binding.run {
                root.isClickable = false
                root.isFocusable = false
                root.setOnTouchListener { _, _ -> true }
                tvHighlight.text = item.highlight
                tvNormal.text = item.normal
            }
        }
    }
}