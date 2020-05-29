package com.oesvica.appibartiFace.ui.persons

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.utils.base.DaggerFragment
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
        setUpRecyclerView()
        observeCategories()
        personsViewModel.loadStatuses()
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
