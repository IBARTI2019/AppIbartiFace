package com.oesvica.appibartiFace.ui.persons

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.person.Person
import com.oesvica.appibartiFace.data.model.person.fullName
import com.oesvica.appibartiFace.data.model.person.fullPhotoUrl
import kotlinx.android.synthetic.main.fragment_person.view.*

/**
 * [RecyclerView.Adapter] that can display a [Person]
 */
class PersonsAdapter(private val onPersonSelected: (person: Person) -> Unit) :
    RecyclerView.Adapter<PersonsAdapter.PersonViewHolder>() {

    var allPersons: List<Person> = ArrayList()
        set(value) {
            field = value
            updateFilteredPersons()
        }
    var personsFilter: (Person) -> Boolean = { true }
        set(value) {
            field = value
            updateFilteredPersons()
        }
    private var filteredPersons: List<Person> = allPersons

    private fun updateFilteredPersons() {
        filteredPersons = allPersons.filter(personsFilter)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        return PersonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_person, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(filteredPersons[position])
    }

    override fun getItemCount(): Int = filteredPersons.size

    inner class PersonViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(person: Person) {
            with(person){
                view.fullnameTextView.text = fullName()
                view.cedulaTextView.text = "CI: $doc_id"
                view.clienteTextView.text = client
                view.categoriaTextView.text = category
                view.statusTextView.text = status
                Glide.with(view)
                    .load(fullPhotoUrl())
                    .placeholder(R.drawable.photo_placeholder)
                    .centerCrop()
                    .into(view.personPhotoImageView)
            }
            view.setOnClickListener {
                onPersonSelected(person)
            }
        }
    }
}
