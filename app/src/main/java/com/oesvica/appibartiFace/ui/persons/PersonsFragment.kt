package com.oesvica.appibartiFace.ui.persons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.Person
import com.oesvica.appibartiFace.ui.editPerson.EditPersonActivity
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import distinc
import kotlinx.android.synthetic.main.fragment_person_list.*
import kotlinx.android.synthetic.main.fragment_person_list.fieldEditText
import kotlinx.android.synthetic.main.fragment_person_list.fieldSpinner
import java.util.*


/**
 * A fragment representing a list of ca.
 */
class PersonsFragment : DaggerFragment() {

    private val personsViewModel by lazy { getViewModel<PersonsViewModel>() }

    private val personsAdapter by lazy {
        PersonsAdapter(onEditPerson = {
            startActivity(
                EditPersonActivity.starterIntent(
                    requireContext(),
                    it.id,
                    it.doc_id,
                    it.category,
                    it.status
                )
            )
        })
    }

    private var personsFilter: (Person) -> Boolean = { true }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_person_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpRecyclerView()
        setUpFieldSpinner()
        observePersons()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        personsViewModel.refreshPersons()
        super.onResume()
    }

    private fun setUpRecyclerView() {
        with(personsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = personsAdapter
        }
        personsRefreshLayout.setOnRefreshListener { personsViewModel.refreshPersons() }
    }

    private fun setUpFieldSpinner() {
        fieldSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Seleccione campo:", "Cedula", "Cliente", "Categoria", "Estatus")
        )
        fieldSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    fieldEditText.isEnabled = true
                    fieldEditText.hint = fieldSpinner.selectedItem.toString()
                } else {
                    fieldEditText.isEnabled = false
                    fieldEditText.setText("")
                    fieldEditText.hint = ""
                }
                updatePersonsFilter()
            }
        }
        fieldEditText.addTextChangedListener { updatePersonsFilter() }
    }

    private fun updatePersonsFilter() {
        val query = fieldEditText.text.toString().trim().toLowerCase(Locale.getDefault())
        personsFilter = { person ->
            when (fieldSpinner.selectedItemPosition) {
                1 -> person.doc_id.indexOf(query) == 0
                2 -> person.client.indexOf(query) == 0
                3 -> person.category.contains(query, ignoreCase = true)
                4 -> person.status.contains(query, ignoreCase = true)
                else -> true
            }
        }
        personsAdapter.setData(filter = personsFilter)
    }

    private fun observePersons() {
        personsViewModel.persons.distinc().observe(viewLifecycleOwner, Observer { persons ->
            debug("observe persons = ${persons.take(3)}")
            persons?.let { personsAdapter.setData(it, personsFilter) }
        })
        personsViewModel.personsNetworkRequest.observe(viewLifecycleOwner, Observer {
            personsRefreshLayout.isRefreshing = it.isOngoing
        })
    }
}
