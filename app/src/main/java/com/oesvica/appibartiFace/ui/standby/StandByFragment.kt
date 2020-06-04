package com.oesvica.appibartiFace.ui.standby

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.StandBy
import com.oesvica.appibartiFace.ui.addPerson.AddPersonActivity
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.screenWidth
import kotlinx.android.synthetic.main.fragment_stand_by_list.*
import java.util.*

/**
 * A fragment representing a list of StandBys.
 */
class StandByFragment : DaggerFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val COLUMNS_COUNT = 3
    }

    private val standByViewModel by lazy { getViewModel<StandByViewModel>() }

    private val items = arrayOf("Registrar persona", "Eliminar standby")
    private var builder: AlertDialog.Builder? = null
    private var itemsDialog: AlertDialog? = null
    private var datePickerDialog: DatePickerDialog? = null
    private var currentDate: String? = null

    private val standByAdapter by lazy {
        StandByAdapter(requireActivity().screenWidth(), onStandBySelected = { standBy ->
            showOptionsDialog(standBy)
        })
    }

    private fun showOptionsDialog(standBy: StandBy) {
        builder = AlertDialog.Builder(requireContext())
        builder?.setTitle("Hora foto: ${standBy.time}")
        builder?.setItems(items) { _, position ->
            debug("clicked $position $standBy")
            when (position) {
                0 -> startActivity(
                    AddPersonActivity.starterIntent(
                        requireContext(),
                        standBy.client,
                        standBy.device,
                        standBy.date,
                        standBy.url
                    )
                )
                1 -> standByViewModel.deleteStandBy(standBy)
            }
        }
        itemsDialog = builder?.create()
        itemsDialog?.show()
    }

    private fun hideOptionsDialog() {
        itemsDialog?.dismiss()
        itemsDialog = null
        builder = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stand_by_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        searchStandBysIcon.setOnClickListener { searchStandBys() }
        setUpRecyclerView()
        setUpDateTextView()
        observeStandBys()
        standByViewModel.loadTodayStandBys()
        super.onActivityCreated(savedInstanceState)
    }

    private fun searchStandBys() {
        debug("searchStandBys")
        currentDate?.let {
            if (clientEditText.text.toString().isEmpty()) {
                Toast.makeText(context, "Debe ingresar un valor en el campo cliente", Toast.LENGTH_SHORT).show()
                return@let
            }
            standByViewModel.searchStandBys(clientEditText.text.toString(), it)
        }
    }

    private fun setUpDateTextView() {
        Calendar.getInstance().apply {
            setDate(
                get(Calendar.YEAR),
                get(Calendar.MONTH) + 1,
                get(Calendar.DAY_OF_MONTH)
            )
        }
        dateTextView.setOnClickListener { showDatePickerDialog() }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog?.show()
    }

    private fun hideDatePickerDialog() {
        datePickerDialog?.dismiss()
        datePickerDialog = null
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        setDate(year, month + 1, dayOfMonth)
    }

    private fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        debug("onDateSet($year: Int, $month: Int, $dayOfMonth: Int)")
        val monthStr = if (month < 10) "0$month" else month.toString()
        val dayStr = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
        val dateFormatted = "$year-$monthStr-$dayStr"
        dateTextView.text = "Fecha: $dateFormatted"
        currentDate = dateFormatted
    }

    private fun setUpRecyclerView() {
        standBysRecyclerView.layoutManager = GridLayoutManager(context, COLUMNS_COUNT)
        standBysRecyclerView.adapter = standByAdapter
    }

    private fun observeStandBys() {
        standByViewModel.standBys.observe(viewLifecycleOwner, Observer {
            debug("standBys.observe $it")
            it?.let {
                standByAdapter.standBys = it
            }
        })
    }

    override fun onDestroy() {
        hideOptionsDialog()
        hideDatePickerDialog()
        super.onDestroy()
    }

}
