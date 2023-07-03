package com.clonect.feeltalk.new_presentation.ui.chatNavigation.contentsShare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.clonect.feeltalk.R

class ContentsShareAdapter(
    private val context: Context,
    private val items: List<String>
): PagerAdapter() {

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getCount(): Int = items.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)

        // question
        val view = if (position == 0) {
            (inflater.inflate(R.layout.tab_layout_contents_share, container, false) as RecyclerView).apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }
        // challenge
        else {
            (inflater.inflate(R.layout.tab_layout_contents_share, container, false) as RecyclerView).apply {
                layoutManager = GridLayoutManager(context, 2)
            }
        }

        container.addView(view)
        return view
    }
}