package com.oesvica.appibartiFace.ui.categories

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.utils.debug

import kotlinx.android.synthetic.main.fragment_category.view.*

/**
 * [RecyclerView.Adapter] that can display a [Category]
 */
class CategoriesAdapter(val context: Context) :
    RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    var categories: List<Category> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_category, parent, false)
        ).apply {

        }
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val description: TextView by lazy { view.description }

        fun bind(category: Category) {
            description.text = category.description
            view.edit.setOnClickListener {
                debug("edit clicked")
            }
            view.delete.setOnClickListener {
                debug("delete clicked")
            }
        }
    }
}
