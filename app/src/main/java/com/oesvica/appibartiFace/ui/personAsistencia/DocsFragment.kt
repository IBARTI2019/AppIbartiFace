package com.oesvica.appibartiFace.ui.personAsistencia

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.currentDay
import com.oesvica.appibartiFace.data.model.personAsistencia.PersonAsistencia
import com.oesvica.appibartiFace.data.model.personAsistencia.PersonAsistenciaQuery
import com.oesvica.appibartiFace.ui.standby.StandByFragment
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import kotlinx.android.synthetic.main.fragment_docs_list.*
import kotlinx.android.synthetic.main.fragment_docs_list.endDateTextView
import kotlinx.android.synthetic.main.fragment_docs_list.fieldEditText
import kotlinx.android.synthetic.main.fragment_docs_list.fieldSpinner
import kotlinx.android.synthetic.main.fragment_docs_list.iniDateTextView
import java.util.*
import kotlin.properties.Delegates

class DocsFragment : DaggerFragment() {

    companion object {
        const val COLUMNS_COUNT = 3
        const val KEY_INI_DATE = "iniDate"
        const val KEY_END_DATE = "endDate"
        const val KEY_RECYCLER_STATE = "AsistenciaRecyclerViewState"
        const val KEY_LAST_QUERY_TRIGGERED = "lastQueryTriggered"
    }

    private val docsViewModel: DocsViewModel by viewModels { viewModelFactory }
    private var datePickerDialog: DatePickerDialog? = null
    private var iniDate by Delegates.observable<CustomDate?>(null) { _, _, newValue ->
        newValue?.let { iniDateTextView.text = it.toDisplayFormat() }
    }
    private var endDate by Delegates.observable<CustomDate?>(null) { _, _, newValue ->
        newValue?.let { endDateTextView.text = it.toDisplayFormat() }
    }

    private var lastQueryTriggered: PersonAsistenciaQuery? = null
    private val docsAdapter by lazy { DocsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_docs_list, container, false)
    }

    private val isAptos by lazy { findNavController().currentDestination?.id == R.id.nav_aptos }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        debug("DocsFragment id = ${findNavController().currentDestination?.id}")
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
        setUpFieldSpinner()
        setUpDateTextViews()
        observePersonAsistencias()
        val tempLastQueryTriggered =
            savedInstanceState?.getParcelable<PersonAsistenciaQuery?>(KEY_LAST_QUERY_TRIGGERED)
        debug("PersonAsistencia onViewCreated tempLastQueryTriggered $tempLastQueryTriggered")
        if (tempLastQueryTriggered != null) {
            lastQueryTriggered = tempLastQueryTriggered
            docsViewModel.searchPersonAsistencias(tempLastQueryTriggered)
        } else {
            queryPersonAsistencias(force = false)
        }
        searchDocsButton.setOnClickListener { queryPersonAsistencias(force = true) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_INI_DATE, iniDate)
        outState.putParcelable(KEY_END_DATE, endDate)
        docsRecyclerView?.layoutManager?.onSaveInstanceState()?.let { state ->
            outState.putParcelable(KEY_RECYCLER_STATE, state)
        }
        lastQueryTriggered?.let {
            outState.putParcelable(
                StandByFragment.KEY_LAST_QUERY_TRIGGERED,
                it
            )
        }
    }

    private fun setUpRecyclerView(savedInstanceState: Bundle?) {
        docsRecyclerView.layoutManager = GridLayoutManager(context, COLUMNS_COUNT)
        docsRecyclerView.adapter = docsAdapter
        docsRecyclerView.post {
            savedInstanceState?.let {
                val listState = it.getParcelable<Parcelable>(KEY_RECYCLER_STATE)
                docsRecyclerView.layoutManager?.onRestoreInstanceState(listState)
            }
        }
        personAsistenciasRefreshLayout.setOnRefreshListener {
            queryPersonAsistencias(force = true)
        }
    }

    private fun setUpFieldSpinner() {
        fieldSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            filters.map { it.fieldName }
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
                updateAsistenciasPersonsFilter()
            }
        }
        fieldEditText.addTextChangedListener { updateAsistenciasPersonsFilter() }
    }

    private val filters = arrayOf(
        AsistenciaPersonFilter("Seleccione campo:") { _, _ -> true },
        AsistenciaPersonFilter("Nombre") { personAsist, query ->
            personAsist.names?.contains(
                query,
                ignoreCase = true
            ) ?: false
        },
        AsistenciaPersonFilter("Cedula") { personAsist, query -> personAsist.cedula.indexOf(query) == 0 }
    )

    data class AsistenciaPersonFilter(
        var fieldName: String,
        var condition: (PersonAsistencia, String) -> Boolean
    )

    private fun updateAsistenciasPersonsFilter() {
        val query = fieldEditText.text.toString().trim().toLowerCase(Locale.getDefault())
        docsAdapter.asistenciasPersonsFilter = {
            filters[fieldSpinner.selectedItemPosition].condition(it, query)
        }
    }

    private fun setUpDateTextViews() {
        iniDateTextView.setOnClickListener { showDatePickerDialog(true) }
        endDateTextView.setOnClickListener { showDatePickerDialog(false) }
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

    private fun observePersonAsistencias() {
        docsViewModel.personAsistencias.distinctUntilChanged().observe(viewLifecycleOwner) { list ->
            debug("observe personAsistencias = ${list.take(2)}")
            docsAdapter.allAsistenciasPersons = list
        }
        docsViewModel.fetchPersonAsistenciasNetworkRequest.observe(viewLifecycleOwner) {
            personAsistenciasRefreshLayout.isRefreshing = it.isOngoing
        }
    }

    private fun queryPersonAsistencias(force: Boolean) {
        debug("queryPersonAsistencias")
        val query = PersonAsistenciaQuery(
            iniDate = iniDate ?: return,
            endDate = endDate ?: return,
            isAptos = isAptos
        )
        debug("queryPersonAsistencias=$query")
        lastQueryTriggered = query
        docsViewModel.searchPersonAsistencias(query, force = force)
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