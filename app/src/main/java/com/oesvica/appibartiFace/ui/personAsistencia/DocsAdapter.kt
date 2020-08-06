package com.oesvica.appibartiFace.ui.personAsistencia

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.personAsistencia.PersonAsistencia
import com.oesvica.appibartiFace.data.model.personAsistencia.fullName
import com.oesvica.appibartiFace.data.model.personAsistencia.fullPhotoUrl
import kotlinx.android.synthetic.main.fragment_doc.view.*
import kotlin.properties.Delegates

class DocsAdapter() : RecyclerView.Adapter<DocsAdapter.DocViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_doc, parent, false)
        return DocViewHolder(view)
    }

    var allAsistenciasPersons: List<PersonAsistencia> by Delegates.observable(emptyList()) { _, _, _ ->
        updateFilteredAsistenciasPersons()
    }
    var asistenciasPersonsFilter: (PersonAsistencia) -> Boolean = { true }
        set(value) {
            field = value
            updateFilteredAsistenciasPersons()
        }
    private var filteredAsistenciasPersons = allAsistenciasPersons

    private fun updateFilteredAsistenciasPersons() {
        filteredAsistenciasPersons = allAsistenciasPersons.filter(asistenciasPersonsFilter)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        holder.bind(filteredAsistenciasPersons[position])
    }

    override fun getItemCount(): Int = filteredAsistenciasPersons.size

    inner class DocViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(doc: PersonAsistencia) {
            with(doc) {
                view.nameTextView.text = fullName()
                view.idTextView.text = "CI: " + cedula
                view.dateTextView.text = dateEntry
                view.timeTextView.text = timeEntry
                Glide.with(view)
                    .load(fullPhotoUrl())
                    .placeholder(R.drawable.photo_placeholder)
                    .centerCrop()
                    .into(view.docImageView)
            }
        }
    }
}