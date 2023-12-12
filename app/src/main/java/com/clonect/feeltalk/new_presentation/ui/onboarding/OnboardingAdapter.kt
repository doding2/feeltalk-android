package com.clonect.feeltalk.new_presentation.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ItemOnboardingBinding

class OnboardingAdapter(
    private val items: List<Int>
): RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size


    inner class OnboardingViewHolder(
        val binding: ItemOnboardingBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Int) {
            binding.imageTooltip.setImageResource(
                when (item) {
                    0 -> R.drawable.n_image_onboarding_1
                    1 -> R.drawable.n_image_onboarding_2
                    2 -> R.drawable.n_image_onboarding_3
                    else -> R.drawable.n_image_onboarding_1
                }
            )
        }
    }
}