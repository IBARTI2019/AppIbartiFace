package com.oesvica.appibartiFace.ui.aptos

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.currentDay
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import kotlinx.android.synthetic.main.fragment_docs_list.*
import kotlinx.android.synthetic.main.fragment_docs_list.endDateTextView
import kotlinx.android.synthetic.main.fragment_docs_list.iniDateTextView
import kotlin.properties.Delegates

class DocsFragment : DaggerFragment() {

    companion object {
        const val COLUMNS_COUNT = 3
        const val KEY_INI_DATE = "iniDate"
        const val KEY_END_DATE = "endDate"
        const val KEY_RECYCLER_STATE = "AsistenciaRecyclerViewState"
    }

    private val docsViewModel: DocsViewModel by viewModels { viewModelFactory }
    private var datePickerDialog: DatePickerDialog? = null
    private var iniDate by Delegates.observable<CustomDate?>(null) { _, _, newValue ->
        newValue?.let { iniDateTextView.text = it.toDisplayFormat() }
    }
    private var endDate by Delegates.observable<CustomDate?>(null) { _, _, newValue ->
        newValue?.let { endDateTextView.text = it.toDisplayFormat() }
    }

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
        setUpDateTextViews()
        observeDocs()
        refreshDocs()
        searchDocsButton.setOnClickListener {
            refreshDocs()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_INI_DATE, iniDate)
        outState.putParcelable(KEY_END_DATE, endDate)
        docsRecyclerView?.layoutManager?.onSaveInstanceState()?.let { state ->
            outState.putParcelable(KEY_RECYCLER_STATE, state)
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

    private fun observeDocs() {
        docsViewModel.docs.observe(viewLifecycleOwner) { list ->
            list?.let { docsAdapter.docs = it }
        }

    }

    private fun refreshDocs() {
        docsViewModel.refreshDocs(
            iniDate = iniDate ?: return,
            endDate = endDate ?: return,
            aptos = isAptos
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