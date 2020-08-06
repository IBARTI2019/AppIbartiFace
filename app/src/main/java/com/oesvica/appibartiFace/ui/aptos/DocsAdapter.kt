package com.oesvica.appibartiFace.ui.aptos

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.api.Doc
import com.oesvica.appibartiFace.data.api.fullPhotoUrl
import kotlinx.android.synthetic.main.fragment_doc.view.*
import kotlin.properties.Delegates

class DocsAdapter() : RecyclerView.Adapter<DocsAdapter.DocViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_doc, parent, false)
        return DocViewHolder(view)
    }

    var docs: List<Doc> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        holder.bind(docs[position])
    }

    override fun getItemCount(): Int = docs.size

    inner class DocViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(doc: Doc) {
            with(doc) {
                view.nameTextView.text = "Nombre Apellido"
                view.idTextView.text = "CI: " + cedula
                view.dateTextView.text = date
                view.timeTextView.text = time
                Glide.with(view)
                    .load(fullPhotoUrl())
                    .placeholder(R.drawable.photo_placeholder)
                    .centerCrop()
                    .into(view.docImageView)
            }
        }
    }
}