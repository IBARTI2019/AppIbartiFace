package com.oesvica.appibartiFace.ui.statuses

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.ui.addStatus.AddStatusActivity
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import kotlinx.android.synthetic.main.fragment_person_list.*
import kotlinx.android.synthetic.main.fragment_status_list.*

/**
 * A fragment representing a list of ca.
 */
class StatusesFragment : DaggerFragment() {

    private val statusesViewModel by lazy { getViewModel<StatusesViewModel>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_status_list, container, false)
    }

    private val personsAdapter by lazy {
        StatusesAdapter(onEdit = {
            startActivity(AddStatusActivity.starterIntent(requireContext(), it))
        }, onDelete = {
            statusesViewModel.deleteStatus(it)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpRecyclerView()
        addStatusButton.setOnClickListener {
            startActivity(AddStatusActivity.starterIntent(requireContext()))
        }
        observeStatuses()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        statusesViewModel.loadStatuses()
    }

    private fun setUpRecyclerView() {
        with(statusesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = personsAdapter
        }
    }

    private fun observeStatuses() {
        statusesViewModel.statuses.observe(viewLifecycleOwner, Observer { statuses ->
            statuses?.let {
                personsAdapter.statuses = it
            }
        })
    }
}
