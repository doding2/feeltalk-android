package com.clonect.feeltalk.mvp_presentation.ui.news

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemNewsBinding
import com.clonect.feeltalk.mvp_domain.model.data.news.News
import kotlin.random.Random

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val callback = object: DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    private var onItemClickListener: ((News) -> Unit)? = null
    private var partnerProfileUrl: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = differ.currentList[position]
        holder.bind(news)
    }

    override fun getItemCount(): Int = differ.currentList.size


    fun setOnItemClickListener(listener: (News) -> Unit) {
        onItemClickListener = listener
    }


    fun setPartnerProfileUrl(url: String?) {
        partnerProfileUrl = url
    }

    private fun ImageView.setProfileImageUrl(url: String?) {
        Glide.with(this)
            .load(url)
            .circleCrop()
            .fallback(R.drawable.image_my_default_profile)
            .error(R.drawable.image_my_default_profile)
            .into(this)
    }


    inner class NewsViewHolder(
        val binding: ItemNewsBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.apply {
                textDate.text = news.date

                val contentSpan = SpannableStringBuilder()
//                    .bold { append(news.target) }
                    .append(news.content)
                textContent.text = contentSpan

                val isOneFourthRandom = Random.nextBoolean() && Random.nextBoolean()
                val backgroundId: Int =
                    if (isOneFourthRandom)
                        R.drawable.background_item_news
                    else R
                        .color.white

                binding.clRoot.setBackgroundResource(backgroundId)

//                val profileId: Int = when (news.type.toNewsType()) {
//                    is NewsType.News -> R.drawable.image_my_default_profile
//                    is NewsType.Chat -> R.drawable.image_partner_default_profile
//                    is NewsType.Official -> R.drawable.image_official_default_profile
//                }
//                ivProfile.setImageResource(profileId)

                ivProfile.setProfileImageUrl(partnerProfileUrl)

                root.setOnClickListener { _ ->
                    onItemClickListener?.let { it(news) }
                }
            }
        }
    }


}