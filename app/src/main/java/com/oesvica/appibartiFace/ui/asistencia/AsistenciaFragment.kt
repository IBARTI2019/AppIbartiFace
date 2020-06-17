package com.oesvica.appibartiFace.ui.asistencia

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.currentDay
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import distinc
import kotlinx.android.synthetic.main.fragment_asistencia.view.*
import kotlinx.android.synthetic.main.fragment_asistencia_list.*
import java.util.*

/**
 * A fragment representing a list of Items.
 */
class AsistenciaFragment : DaggerFragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_asistencia_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpDateTextViews()
        setUpFieldSpinner()
        observeAsistencias()
        initializeDatesRangeWithCurrentDay()
        searchAsistencias { true }
        searchAsistenciasIcon.setOnClickListener {
            searchAsistencias(getAsistenciaFilterForCurrentInputs())
        }
        super.onActivityCreated(savedInstanceState)
    }

    private fun searchAsistencias(asistenciasFilter: AsistenciaFieldFilter) {
        asistenciasViewModel.refreshAsistencias(
            iniDate ?: return, endDate ?: return, asistenciasFilter
        )
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
            }
        }
    }

    private fun getAsistenciaFilterForCurrentInputs(): AsistenciaFieldFilter {
        val query = fieldEditText.text.toString().trim().toLowerCase(Locale.getDefault())
        return if (query.isEmpty()) {
            { true }
        } else {
            {
                when (fieldSpinner.selectedItemPosition) {
                    1 -> it.docId.indexOf(query) == 0
                    2 -> it.codFicha.indexOf(query) == 0
                    3 -> it.names?.toLowerCase(Locale.getDefault())?.contains(query) ?: false
                    4 -> it.surnames?.toLowerCase(Locale.getDefault())?.contains(query) ?: false
                    else -> true
                }
            }
        }
    }

    private fun initializeDatesRangeWithCurrentDay() {
        Calendar.getInstance().apply {
            setDate(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), true)
            setDate(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH), false)
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
            asistenciasTableLayout.removeViews(1, asistenciasTableLayout.childCount - 1)
            list.forEachIndexed { index, asistencia ->
                val view = layoutInflater.inflate(R.layout.fragment_asistencia, null)
                if (index % 2 != 0) view.setBackgroundColor(Color.rgb(223, 251, 255))
                with(view) {
                    cedulaTextView.text = asistencia.docId
                    fichaTextView.text = asistencia.codFicha
                    nombresTextView.text = asistencia.names
                    apellidosTextView.text = asistencia.surnames
                    fechaTextView.text = asistencia.date
                    horaTextView.text = asistencia.time
                }
                asistenciasTableLayout.addView(view)
            }
        })
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