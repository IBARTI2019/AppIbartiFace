package com.oesvica.appibartiFace.ui.addPerson

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.standby.Prediction
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_prediction_item.view.*

class PredictionsAdapter(
    private val screenWidth: Int,
    private val onImageSelected: (String) -> Unit
) : RecyclerView.Adapter<PredictionsAdapter.PredictionViewHolder>() {

    var predictions: List<Prediction> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        return PredictionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_prediction_item, parent, false)
        )
    }

    override fun getItemCount(): Int = predictions.count()

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        holder.bind(predictions[position])
    }

    inner class PredictionViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val predictionImageView by lazy { view.predictionImageView }

        fun bind(prediction: Prediction) {
            Picasso.get()
                .load(prediction.completeUrl)
                .placeholder(R.drawable.photo_placeholder)
                .resize(screenWidth.div(5), screenWidth.div(5))
                .centerCrop()
                .into(predictionImageView)
            predictionImageView.setOnClickListener {
                onImageSelected(prediction.cedula)
            }
        }
    }
}