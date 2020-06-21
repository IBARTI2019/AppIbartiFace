package com.oesvica.appibartiFace.ui.persons

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.Person
import kotlinx.android.synthetic.main.fragment_person.view.*

/**
 * [RecyclerView.Adapter] that can display a [Person]
 */
class PersonsAdapter(private val onEditPerson: (person: Person) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_HEADER = 1001
        const val ITEM_TYPE_NORMAL = 1002
    }

    private var allPersons: List<Person> = ArrayList()
    private var filteredPersons: List<Person> = allPersons

    fun setData(persons: List<Person>? = null, filter: (Person) -> Boolean) {
        persons?.let { allPersons = it }
        filteredPersons = allPersons.filter(filter)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_NORMAL) {
            PersonViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_person, parent, false)
            )
        } else {
            PersonHeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_person_header, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PersonViewHolder) holder.bind(position - 1, filteredPersons[position - 1])
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ITEM_TYPE_HEADER else ITEM_TYPE_NORMAL
    }

    override fun getItemCount(): Int = filteredPersons.size + 1

    inner class PersonViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val cedula: TextView by lazy { view.cedula }
        private val categoria: TextView by lazy { view.categoria }
        private val cliente: TextView by lazy { view.cliente }
        private val status: TextView by lazy { view.status }

        fun bind(index: Int, person: Person) {
            if (index % 2 != 0) {
                view.setBackgroundColor(Color.rgb(223, 251, 255))
            } else {
                view.setBackgroundColor(Color.WHITE)
            }
            cedula.text = person.doc_id
            categoria.text = person.category
            cliente.text = person.client
            status.text = person.status
            view.edit.setOnClickListener {
                onEditPerson(person)
            }
        }
    }

    inner class PersonHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
