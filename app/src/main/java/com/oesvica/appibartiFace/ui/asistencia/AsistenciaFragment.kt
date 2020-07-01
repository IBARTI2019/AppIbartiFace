package com.oesvica.appibartiFace.ui.asistencia

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import com.oesvica.appibartiFace.data.model.Asistencia
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.currentDay
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import distinc
import kotlinx.android.synthetic.main.fragment_asistencia_list.*
import kotlinx.android.synthetic.main.fragment_asistencia_list.fieldEditText
import kotlinx.android.synthetic.main.fragment_asistencia_list.fieldSpinner
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class AsistenciaFragment : DaggerFragment() {

    companion object {
        const val KEY_INI_DATE = "iniDate"
        const val KEY_END_DATE = "endDate"
        const val KEY_RECYCLER_STATE = "AsistenciaRecyclerViewState"
    }

    private val asistenciasViewModel by lazy { getViewModel<AsistenciaViewModel>() }
    private var datePickerDialog: DatePickerDialog? = null
    private var iniDate: CustomDate? = null
        set(value) {
            field = value
            value?.let { iniDateTextView.text = value.toDisplayFormat() }
        }
    private var endDate: CustomDate? = null
        set(value) {
            field = value
            value?.let { endDateTextView.text = value.toDisplayFormat() }
        }
    private val asistenciasAdapter by lazy { AsistenciaAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_asistencia_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val savedIniDate = savedInstanceState?.getParcelable<CustomDate?>(KEY_INI_DATE)
        val savedEndDate = savedInstanceState?.getParcelable<CustomDate?>(KEY_END_DATE)
        if (savedIniDate != null && savedEndDate != null) {
            iniDate = savedIniDate
            endDate = savedEndDate
        } else {
            iniDate = currentDay()
            endDate = currentDay()
        }
        setUpRecyclerView(savedInstanceState)
        setUpDateTextViews()
        setUpFieldSpinner()
        setUpLocationSpinner()
        observeAsistencias()
        searchAsistencias()
        searchAsistenciasIcon.setOnClickListener { searchAsistencias() }
        super.onActivityCreated(savedInstanceState)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_INI_DATE, iniDate)
        outState.putParcelable(KEY_END_DATE, endDate)
        asistenciasRecyclerView?.layoutManager?.onSaveInstanceState()?.let { state ->
            outState.putParcelable(KEY_RECYCLER_STATE, state)
        }
    }

    private fun searchAsistencias() {
        asistenciasViewModel.refreshAsistencias(iniDate ?: return, endDate ?: return)
    }

    private fun setUpRecyclerView(savedInstanceState: Bundle?) {
        with(asistenciasRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = asistenciasAdapter
        }
        asistenciasRecyclerView.post {
            savedInstanceState?.let {
                val listState = it.getParcelable<Parcelable>(KEY_RECYCLER_STATE)
                asistenciasRecyclerView.layoutManager?.onRestoreInstanceState(listState)
            }
        }
    }

    private fun setUpDateTextViews() {
        iniDateTextView.setOnClickListener { showDatePickerDialog(true) }
        endDateTextView.setOnClickListener { showDatePickerDialog(false) }
    }

    private fun setUpFieldSpinner() {
        fieldSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Seleccione campo:", "Cedula", "Ficha", "Nombres", "Apellidos")
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
                updateAsistenciasFilter()
            }
        }
        fieldEditText.addTextChangedListener { updateAsistenciasFilter() }
    }

    private fun setUpLocationSpinner() {
        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                fieldEditText.clearFocus()
                updateAsistenciasFilter()
            }
        }
    }

    private fun updateAsistenciasFilter() {
        debug("updateAsistenciasFilter")
        val query = fieldEditText.text.toString().trim().toLowerCase(Locale.getDefault())
        asistenciasAdapter.asistenciasFilter = { asistencia ->
            (when (fieldSpinner.selectedItemPosition) {
                1 -> asistencia.docId.indexOf(query) == 0
                2 -> asistencia.codFicha.indexOf(query) == 0
                3 -> asistencia.names?.toLowerCase(Locale.getDefault())?.indexOf(query) == 0
                4 -> asistencia.surnames?.toLowerCase(Locale.getDefault())?.indexOf(query) == 0
                else -> true
            }) && asistencia.location.equals(locationSpinner.selectedItem?.toString() ?: "", true)
        }
    }

    private fun showDatePickerDialog(isInitialDate: Boolean) {
        val date = (if (isInitialDate) iniDate else endDate) ?: currentDay()
        debug("showDatePickerDialog  $date")
        datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                setDate(year, month, dayOfMonth, isInitialDate)
            },
            date.year,
            date.month,
            date.day
        )
        datePickerDialog?.show()
    }

    private fun setDate(year: Int, month: Int, dayOfMonth: Int, isInitialDate: Boolean) {
        debug("onDateSet($year: Int, $month: Int, $dayOfMonth: Int, $isInitialDate: Boolean)")
        val customDate = CustomDate(year, month, dayOfMonth)
        if (isInitialDate) iniDate = customDate else endDate = customDate
    }

    @SuppressLint("InflateParams")
    private fun observeAsistencias() {
        asistenciasViewModel.asistencias.distinc().observe(viewLifecycleOwner, Observer { list ->
            debug("observe asistencias ${list.take(2)}")
            if (list == null) return@Observer
            updateLocationSpinnerAdapter(list)
            asistenciasAdapter.allAsistencias = list
        })
    }

    private fun updateLocationSpinnerAdapter(list: List<Asistencia>) {
        locationSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            list.map { it.location.capitalize() }.distinct()
        )
    }

    private fun hideDatePickerDialog() {
        datePickerDialog?.dismiss()
        datePickerDialog = null
    }

    override fun onDestroy() {
        hideDatePickerDialog()
        super.onDestroy()
    }

}