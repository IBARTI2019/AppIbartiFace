package com.oesvica.appibartiFace.ui.persons

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.ui.editPerson.EditPersonActivity
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import distinc
import kotlinx.android.synthetic.main.fragment_person_list.*
import java.util.*


/**
 * A fragment representing a list of ca.
 */
class PersonsFragment : DaggerFragment() {

    companion object {
        const val KEY_RECYCLER_STATE = "PersonsRecyclerViewState"
    }

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_person_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpRecyclerView(savedInstanceState)
        setUpFieldSpinner()
        observePersons()
        personsViewModel.refreshPersons()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        personsRecyclerView?.layoutManager?.onSaveInstanceState()
            ?.let { outState.putParcelable(KEY_RECYCLER_STATE, it) }
    }

    private fun setUpRecyclerView(savedInstanceState: Bundle?) {
        with(personsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = personsAdapter
        }
        personsRecyclerView.post {
            savedInstanceState?.let {
                val listState = it.getParcelable<Parcelable>(KEY_RECYCLER_STATE)
                personsRecyclerView.layoutManager?.onRestoreInstanceState(listState)
            }
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
                fieldEditText.clearFocus()
                updatePersonsFilter()
            }
        }
        fieldEditText.addTextChangedListener { updatePersonsFilter() }
    }

    private fun updatePersonsFilter() {
        val query = fieldEditText.text.toString().trim().toLowerCase(Locale.getDefault())
        personsAdapter.personsFilter = { person ->
            when (fieldSpinner.selectedItemPosition) {
                1 -> person.doc_id.indexOf(query) == 0
                2 -> person.client.indexOf(query) == 0
                3 -> person.category.contains(query, ignoreCase = true)
                4 -> person.status.contains(query, ignoreCase = true)
                else -> true
            }
        }
    }

    private fun observePersons() {
        personsViewModel.persons.distinc().observe(viewLifecycleOwner, Observer { persons ->
            debug("observe persons = ${persons.take(3)}")
            persons?.let { personsAdapter.allPersons = it }
        })
        personsViewModel.personsNetworkRequest.observe(viewLifecycleOwner, Observer {
            personsRefreshLayout.isRefreshing = it.isOngoing
        })
    }
}
