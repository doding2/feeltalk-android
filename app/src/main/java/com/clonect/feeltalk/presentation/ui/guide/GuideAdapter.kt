package com.clonect.feeltalk.presentation.ui.guide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.clonect.feeltalk.databinding.ItemGuideBinding

class GuideAdapter(
    var items: List<Int>
): PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val binding = ItemGuideBinding.inflate(inflater, container, false)

        val item = items[position]
        binding.ivGuide.setImageResource(item)
        container.addView(binding.root)

        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount(): Int = items.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

}