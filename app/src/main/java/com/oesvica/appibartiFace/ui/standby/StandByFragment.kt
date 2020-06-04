package com.oesvica.appibartiFace.ui.standby

import android.app.AlertDialog
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.StandBy
import com.oesvica.appibartiFace.ui.addPerson.AddPersonActivity
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.screenWidth

/**
 * A fragment representing a list of StandBys.
 */
class StandByFragment : DaggerFragment() {

    companion object {
        const val COLUMNS_COUNT = 3
    }

    private val standByViewModel by lazy { getViewModel<StandByViewModel>() }

    private val items = arrayOf("Registrar persona", "Eliminar standby")
    private var standBySelected: StandBy? = null
    private val builder: AlertDialog.Builder by lazy { AlertDialog.Builder(requireContext()) }
    private val itemsDialog by lazy { builder.create() }

    private val standByAdapter by lazy {
        StandByAdapter(requireActivity().screenWidth(), onStandBySelected = {
            showOptionsDialog(it)
        })
    }

    private fun showOptionsDialog(standBy: StandBy) {
        standBySelected = standBy
        itemsDialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stand_by_list, container, false)
        if (view is RecyclerView) {
            with(view) {
                layoutManager = GridLayoutManager(context, COLUMNS_COUNT)
                adapter = standByAdapter
            }
        }
        builder.setItems(items) { _, position ->
            debug("clicked $position $standBySelected")
            standBySelected?.let {
                when (position) {
                    0 -> startActivity(
                        AddPersonActivity.starterIntent(
                            requireContext(),
                            it.client,
                            it.device,
                            it.date,
                            it.url
                        )
                    )
                    1 -> standByViewModel.deleteStandBy(it)
                }
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        observeStandBys()
        standByViewModel.loadTodayStandBys()
        super.onActivityCreated(savedInstanceState)
    }

    private fun observeStandBys() {
        standByViewModel.standBys.observe(viewLifecycleOwner, Observer {
            it?.let {
                standByAdapter.standBys = it
            }
        })
    }

    override fun onDestroy() {
        itemsDialog.dismiss()
        super.onDestroy()
    }

}
