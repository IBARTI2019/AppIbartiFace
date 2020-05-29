package com.oesvica.appibartiFace.ui.persons

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.Person
import kotlinx.android.synthetic.main.fragment_person.view.*

/**
 * [RecyclerView.Adapter] that can display a [Category]
 */
class PersonsAdapter() : RecyclerView.Adapter<PersonsAdapter.PersonViewHolder>() {

    var persons: List<Person> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        return PersonViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_person, parent, false)).apply {

        }
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(persons[position])
    }

    override fun getItemCount(): Int = persons.size

    inner class PersonViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val cedula: TextView by lazy { view.cedula }
        private val categoria: TextView by lazy { view.categoria }
        private val cliente: TextView by lazy { view.cliente }
        private val status: TextView by lazy { view.status }

        fun bind(person: Person){
            cedula.text = person.doc_id
            categoria.text = person.category
            cliente.text = person.client
            status.text = person.status
        }
    }
}
