package com.oesvica.appibartiFace.ui.standby

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.standby.properUrl
import kotlinx.android.synthetic.main.fragment_stand_by.view.*
import kotlin.properties.Delegates

/**
 * [RecyclerView.Adapter] that can display a [StandBy]
 */
class StandByAdapter(
    private val context: Context,
    private val onStandBySelected: (StandBy) -> Unit
) :
    RecyclerView.Adapter<StandByAdapter.StandByViewHolder>() {

    var standBys: List<StandBy> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandByViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_stand_by, parent, false)
        return StandByViewHolder(view)
    }

    override fun onBindViewHolder(holderStandBy: StandByViewHolder, position: Int) {
        holderStandBy.bind(standBys[position])
    }

    override fun getItemCount(): Int = standBys.size

    inner class StandByViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val urlImageView by lazy { view.urlImageView }
        private val clientTextView by lazy { view.clientTextView }
        private val dateTextView by lazy { view.dateTextView }
        private val timeTextView by lazy { view.timeTextView }

        fun bind(standBy: StandBy) {
            with(standBy) {
                clientTextView.text = client
                dateTextView.text = date
                timeTextView.text = time
                Glide.with(context)
                    .load(standBy.properUrl())
                    .placeholder(R.drawable.photo_placeholder)
                    .centerCrop()
                    .into(urlImageView)
                urlImageView.setOnClickListener {
                    onStandBySelected(standBy)
                }
            }
        }
    }
}