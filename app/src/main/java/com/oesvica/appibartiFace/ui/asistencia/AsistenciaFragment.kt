package com.oesvica.appibartiFace.ui.asistencia

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        observeAsistencias()
        initializeDatesRangeWithCurrentDay()
        asistenciasViewModel.refreshAsistencias(iniDate, endDate)
        searchAsistenciasIcon.setOnClickListener { searchAsistencias() }
        super.onActivityCreated(savedInstanceState)
    }

    private fun searchAsistencias() {
        asistenciasViewModel.refreshAsistencias(
            iniDate ?: return, endDate ?: return
        )
    }

    private fun setUpDateTextViews() {
        iniDateTextView.setOnClickListener { showDatePickerDialog(true) }
        endDateTextView.setOnClickListener { showDatePickerDialog(false) }
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

    private fun observeAsistencias() {
        asistenciasViewModel.asistencias.distinc().observe(viewLifecycleOwner, Observer {
            debug("observe asistencias ${it.take(3)}")
            it?.let {
                asistenciasTableLayout.removeViews(1, asistenciasTableLayout.childCount-1)
            }
            it?.forEachIndexed{ index,asistencia ->
                val view = layoutInflater.inflate(R.layout.fragment_asistencia, null)
                if(index %2 != 0) view.setBackgroundColor(Color.rgb(223, 251, 255))
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