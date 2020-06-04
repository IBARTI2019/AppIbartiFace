package com.oesvica.appibartiFace.ui.persons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import kotlinx.android.synthetic.main.fragment_person.view.*
import kotlinx.android.synthetic.main.fragment_person_list.*


/**
 * A fragment representing a list of ca.
 */
class PersonsFragment : DaggerFragment() {

    private val personsViewModel by lazy { getViewModel<PersonsViewModel>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_person_list, container, false)
    }

    private val personsAdapter by lazy {
        PersonsAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpTable()

        observeCategories()
        personsViewModel.loadPersons()
        super.onActivityCreated(savedInstanceState)
    }

    private fun setUpTable() {

    }

    private fun observeCategories() {
        personsViewModel.persons.observe(viewLifecycleOwner, Observer { persons ->
            persons?.let {
                debug("persons=$persons")
                it.forEach { person ->
                    val view = layoutInflater.inflate(R.layout.fragment_person, null)
                    view.cedula.text = person.doc_id
                    view.cliente.text = person.client
                    view.categoria.text = person.category
                    view.estatus.text = person.status
                    personsTableLayout.addView(view)
                }
            }
        })
    }
}
