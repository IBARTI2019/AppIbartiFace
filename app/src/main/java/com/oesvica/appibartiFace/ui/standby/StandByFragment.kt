package com.oesvica.appibartiFace.ui.standby

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.StandBy
import com.oesvica.appibartiFace.data.model.currentDay
import com.oesvica.appibartiFace.ui.addPerson.AddPersonActivity
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.dialogs.StandByDialog
import com.oesvica.appibartiFace.utils.screenWidth
import distinc
import kotlinx.android.synthetic.main.fragment_stand_by_list.*
import java.util.*

/**
 * A fragment representing a list of StandBys.
 */
class StandByFragment : DaggerFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val COLUMNS_COUNT = 3
        const val STAND_BY_REQUEST_CODE = 101
        const val KEY_SELECTED_DATE = "selectedDate"
        const val KEY_LAST_QUERY_TRIGGERED = "lastQueryTriggered"
        const val KEY_RECYCLER_STATE = "StandBysRecyclerViewState"
    }

    private val standByViewModel by lazy { getViewModel<StandByViewModel>() }

    private var standByDialog: StandByDialog? = null
    private var datePickerDialog: DatePickerDialog? = null
    private var selectedDate: CustomDate? = null
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            value?.let { dateTextView.text = "Fecha: $it" }
        }
    private var lastQueryTriggered: StandByQuery? = null

    private val standByAdapter by lazy {
        StandByAdapter(requireActivity().screenWidth(), onStandBySelected = { standBy ->
            showOptionsDialog(standBy)
        })
    }

    private fun showOptionsDialog(standBy: StandBy) {
        standByDialog = StandByDialog.newInstance(standBy)
        standByDialog?.setTargetFragment(this, STAND_BY_REQUEST_CODE)
        standByDialog?.show(requireFragmentManager(), "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        debug(
            "onActivityResult $requestCode $resultCode ${data?.getParcelableExtra<StandBy>(
                StandByDialog.ARG_STAND_BY
            )}"
        )
        if (resultCode == Activity.RESULT_OK && requestCode == STAND_BY_REQUEST_CODE) {
            val standBy = data?.getParcelableExtra<StandBy>(StandByDialog.ARG_STAND_BY) ?: return
            val isDeleteSelected = data.getBooleanExtra(StandByDialog.ARG_IS_DELETE, false)
            if (isDeleteSelected) {
                standByViewModel.deleteStandBy(standBy)
            } else {
                startActivity(
                    AddPersonActivity.starterIntent(
                        requireContext(),
                        standBy.client,
                        standBy.device,
                        standBy.date,
                        standBy.url
                    )
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stand_by_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpRecyclerView(savedInstanceState)
        val tempDate = savedInstanceState?.getParcelable<CustomDate?>(KEY_SELECTED_DATE)
        if (tempDate != null) {
            selectedDate = tempDate
        } else {
            initSelectedDateAsToday()
        }
        dateTextView.setOnClickListener { showDatePickerDialog() }
        searchStandBysIcon.setOnClickListener {
            val query = getQueryForStandBys(displayErrorMessages = true)
            if (query != null) {
                clientEditText.clearFocus()
                standByViewModel.searchStandBys(query)
                lastQueryTriggered = query
            }
        }
        observeStandBys()
        val tempLastQueryTriggered =
            savedInstanceState?.getParcelable<StandByQuery?>(KEY_LAST_QUERY_TRIGGERED)
        if (tempLastQueryTriggered != null) {
            lastQueryTriggered = tempLastQueryTriggered
            standByViewModel.searchStandBys(tempLastQueryTriggered)
        } else {
            standByViewModel.loadTodayStandBys()
        }
        super.onActivityCreated(savedInstanceState)
    }

    private fun initSelectedDateAsToday() {
        Calendar.getInstance().apply {
            setDate(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedDate?.let { outState.putParcelable(KEY_SELECTED_DATE, it) }
        standBysRecyclerView?.layoutManager?.onSaveInstanceState()
            ?.let { outState.putParcelable(KEY_RECYCLER_STATE, it) }
        lastQueryTriggered?.let { outState.putParcelable(KEY_LAST_QUERY_TRIGGERED, it) }
    }

    private fun getQueryForStandBys(displayErrorMessages: Boolean = true): StandByQuery? {
        val client = clientEditText.text.toString()
        if (client.isEmpty()) {
            if (displayErrorMessages) {
                Toast.makeText(
                    context,
                    "Debe ingresar un valor en el campo cliente",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return null
        }
        return StandByQuery(client, selectedDate ?: return null)
    }

    private fun showDatePickerDialog() {
        val date = selectedDate ?: currentDay()
        datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            date.year,
            date.month,
            date.day
        )
        datePickerDialog?.show()
    }

    private fun hideDatePickerDialog() {
        datePickerDialog?.dismiss()
        datePickerDialog = null
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        setDate(year, month, dayOfMonth)
    }

    private fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        debug("onDateSet($year: Int, $month: Int, $dayOfMonth: Int)")
        selectedDate = CustomDate(year, month, dayOfMonth)
    }

    private fun setUpRecyclerView(savedInstanceState: Bundle?) {
        standBysRecyclerView.layoutManager = GridLayoutManager(context, COLUMNS_COUNT)
        standBysRecyclerView.adapter = standByAdapter
        standBysRecyclerView.post {
            savedInstanceState?.let {
                val listState = it.getParcelable<Parcelable>(KEY_RECYCLER_STATE)
                standBysRecyclerView.layoutManager?.onRestoreInstanceState(listState)
            }
        }
        standBysRefreshLayout.setOnRefreshListener {
            val query = getQueryForStandBys(displayErrorMessages = false)
            if (query == null) standByViewModel.loadTodayStandBys()
            else standByViewModel.searchStandBys(query)
        }
    }

    private fun observeStandBys() {
        standByViewModel.standBys.distinc().observe(viewLifecycleOwner, Observer {
            debug("standBys.observe ${it?.take(10)}")
            it?.let {
                standByAdapter.standBys = it
            }
        })
        standByViewModel.fetchStandBysNetworkRequest.observe(viewLifecycleOwner, Observer {
            standBysRefreshLayout.isRefreshing = it.isOngoing
        })
    }

    override fun onDestroy() {
        hideDatePickerDialog()
        super.onDestroy()
    }

}
