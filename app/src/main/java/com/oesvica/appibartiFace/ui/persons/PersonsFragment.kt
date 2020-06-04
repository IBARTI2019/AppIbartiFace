package com.oesvica.appibartiFace.ui.persons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.ui.addPerson.AddPersonActivity
import com.oesvica.appibartiFace.ui.editPerson.EditPersonActivity
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import kotlinx.android.synthetic.main.fragment_person.view.*
import kotlinx.android.synthetic.main.fragment_person_list.*


/**
 * A fragment representing a list of ca.
 */
class PersonsFragment : DaggerFragment() {

    private val personsViewModel by lazy { getViewModel<PersonsViewModel>() }

    private val personsAdapter by lazy {
        PersonsAdapter(onEditPerson = {
            startActivity(EditPersonActivity.starterIntent(requireContext(), it.doc_id, it.category, it.status))
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_person_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpRecyclerView()
        observeCategories()
        personsViewModel.loadPersons()
        super.onActivityCreated(savedInstanceState)
    }

    private fun setUpRecyclerView() {
        with(personsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = personsAdapter
        }
    }

    private fun observeCategories() {
        personsViewModel.persons.observe(viewLifecycleOwner, Observer { persons ->
            persons?.let {
                personsAdapter.persons = it
            }
        })
    }
}
