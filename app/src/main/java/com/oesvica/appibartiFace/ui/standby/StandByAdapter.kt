package com.oesvica.appibartiFace.ui.standby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.StandBy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_stand_by.view.*

/**
 * [RecyclerView.Adapter] that can display a [StandBy]
 */
class StandByAdapter(private val screenWidth: Int, private val onStandBySelected: (StandBy) -> Unit) :
    RecyclerView.Adapter<StandByAdapter.StandByViewHolder>() {

    var standBys: List<StandBy> = ArrayList()
        set(value) {
            field = value
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
                Picasso.get()
                    .load(standBy.properUrl)
                    .placeholder(R.drawable.photo_placeholder)
                    .resize(screenWidth.div(3), screenWidth.div(3))
                    .centerCrop()
                    .into(urlImageView)
                urlImageView.setOnClickListener {
                    onStandBySelected(standBy)
                }
            }
        }
    }
}