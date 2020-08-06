package com.oesvica.appibartiFace.ui.persons

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.person.Person
import com.oesvica.appibartiFace.data.model.person.fullPhotoUrl
import com.oesvica.appibartiFace.ui.editPerson.EditPersonActivity
import com.oesvica.appibartiFace.ui.persons.dialog.PersonDialog
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import kotlinx.android.synthetic.main.fragment_person_list.*
import java.util.*


/**
 * A fragment representing a list of ca.
 */
class PersonsFragment : DaggerFragment() {

    companion object {
        const val KEY_RECYCLER_STATE = "PersonsRecyclerViewState"
        const val COLUMNS_COUNT = 3
    }

    private val personsViewModel: PersonsViewModel by viewModels { viewModelFactory }

    private val personsAdapter by lazy {
        PersonsAdapter(onPersonSelected = {
            showOptionsDialog(it)
        })
    }

    private fun showOptionsDialog(person: Person) {
        val action = PersonsFragmentDirections.actionNavPersonasToDialogPerson(person)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_person_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView(savedInstanceState)
        setUpFieldSpinner()
        observePersons()
        personsViewModel.refreshPersons()
        handleDialogResponse()
    }

    private fun handleDialogResponse() {
        val navController = findNavController()
        // After a configuration change or process death, the currentBackStackEntry
        // points to the dialog destination, so you must use getBackStackEntry()
        // with the specific ID of your destination to ensure we always
        // get the right NavBackStackEntry
        val navBackStackEntry = navController.getBackStackEntry(R.id.nav_personas)

        // Create our observer and add it to the NavBackStackEntry's lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (navBackStackEntry.savedStateHandle.contains(PersonDialog.ARG_PERSON) && navBackStackEntry.savedStateHandle.contains(
                        PersonDialog.ARG_IS_DELETE
                    )
                ) {
                    val person =
                        navBackStackEntry.savedStateHandle.get<Person>(PersonDialog.ARG_PERSON)
                            ?: return@LifecycleEventObserver
                    val isDelete =
                        navBackStackEntry.savedStateHandle.get<Boolean>(PersonDialog.ARG_IS_DELETE)
                            ?: return@LifecycleEventObserver
                    navBackStackEntry.savedStateHandle.remove<Person>(PersonDialog.ARG_PERSON)
                    navBackStackEntry.savedStateHandle.remove<Boolean>(PersonDialog.ARG_IS_DELETE)
                    debug("person $person $isDelete")
                    if (isDelete) {
                        personsViewModel.deletePerson(person)
                    } else {
                        openEditPersonActivity(person)
                    }
                }
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        // As addObserver() does not automatically remove the observer, we
        // call removeObserver() manually when the view lifecycle is destroyed
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    private fun openEditPersonActivity(person: Person) {
        startActivity(
            EditPersonActivity.starterIntent(
                requireContext(),
                id = person.id,
                cedula = person.doc_id,
                category = person.category,
                status =  person.status,
                photo =  person.fullPhotoUrl()
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        personsRecyclerView?.layoutManager?.onSaveInstanceState()
            ?.let { outState.putParcelable(KEY_RECYCLER_STATE, it) }
    }

    private fun setUpRecyclerView(savedInstanceState: Bundle?) {
        with(personsRecyclerView) {
            layoutManager = GridLayoutManager(context, COLUMNS_COUNT)
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

    private val filters = arrayOf(
        FilterItem("Seleccione campo:") { _, _ -> true },
        FilterItem("Nombre") { person, query ->
            person.names?.contains(query, ignoreCase = true) ?: false
        },
        FilterItem("Cedula") { person, query -> person.doc_id.indexOf(query) == 0 },
        FilterItem("Cliente") { person, query -> person.client.indexOf(query) == 0 },
        FilterItem("Categoria") { person, query ->
            person.category.contains(
                query,
                ignoreCase = true
            )
        },
        FilterItem("Estatus") { person, query -> person.status.contains(query, ignoreCase = true) }
    )

    data class FilterItem(var name: String, var condition: (Person, String) -> Boolean)

    private fun setUpFieldSpinner() {
        fieldSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            filters.map { it.name }
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
            filters[fieldSpinner.selectedItemPosition].condition(person, query)
        }
    }

    private fun observePersons() {
        personsViewModel.persons.distinctUntilChanged().observe(viewLifecycleOwner) { persons ->
            debug("observe persons = ${persons.take(3)}")
            personsAdapter.allPersons = persons
        }
        personsViewModel.personsNetworkRequest.observe(viewLifecycleOwner) {
            personsRefreshLayout.isRefreshing = it.isOngoing
        }
    }
}
