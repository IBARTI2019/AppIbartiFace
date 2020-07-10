package com.oesvica.appibartiFace.ui.statuses

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.utils.debug
import kotlinx.android.synthetic.main.fragment_status.view.*

/**
 * [RecyclerView.Adapter] that can display a [Status]
 */
class StatusesAdapter(private val onEdit: (status: Status) -> Unit, private val onDelete: (status: Status) -> Unit) :
    RecyclerView.Adapter<StatusesAdapter.CategoryViewHolder>() {

    var statuses: List<Status> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_status, parent, false)
        ).apply {
        }
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(statuses[position])
    }

    override fun getItemCount(): Int = statuses.size

    inner class CategoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val categoria: TextView by lazy { view.categoria }
        private val description: TextView by lazy { view.description }

        fun bind(status: Status) {
            description.text = status.description
            categoria.text = status.category

            view.edit.setOnClickListener {
                debug("edit clicked on $status")
                onEdit(status)
            }
            view.delete.setOnClickListener {
                debug("delete clicked $status")
                onDelete(status)
            }
        }
    }
}
