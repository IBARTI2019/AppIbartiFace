package com.oesvica.appibartiFace.ui.standby

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
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
    }

    private val standByViewModel by lazy { getViewModel<StandByViewModel>() }

    private var standByDialog: StandByDialog? = null
    private var datePickerDialog: DatePickerDialog? = null
    private var selectedDate: CustomDate? = null
        set(value) {
            field = value
            value?.let { dateTextView.text = "Fecha: $it" }
        }

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

    private fun hideOptionsDialog() {
        //standByDialog?.dismiss()
        //standByDialog = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stand_by_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        searchStandBysIcon.setOnClickListener { searchStandBysByClientAndDate() }
        setUpRecyclerView()
        setUpDateTextView()
        observeStandBys()
        refreshTodayStandBys()
        super.onActivityCreated(savedInstanceState)
    }

    private fun refreshTodayStandBys() {
        standByViewModel.loadTodayStandBys()
    }

    private fun searchStandBysByClientAndDate(showToastMsg: Boolean = true): Boolean {
        debug("searchStandBys")
        if (clientEditText.text.toString().isEmpty()) {
            if (showToastMsg) {
                Toast.makeText(
                    context,
                    "Debe ingresar un valor en el campo cliente",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return false
        }
        standByViewModel.searchStandBys(
            clientEditText.text.toString(),
            selectedDate?.toString() ?: return false
        )
        return true
    }

    private fun setUpDateTextView() {
        Calendar.getInstance().apply {
            setDate(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))
        }
        dateTextView.setOnClickListener { showDatePickerDialog() }
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

    private fun setUpRecyclerView() {
        standBysRecyclerView.layoutManager = GridLayoutManager(context, COLUMNS_COUNT)
        standBysRecyclerView.adapter = standByAdapter
        standBysRefreshLayout.setOnRefreshListener {
            if (!searchStandBysByClientAndDate(false)) refreshTodayStandBys()
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
        hideOptionsDialog()
        hideDatePickerDialog()
        super.onDestroy()
    }

}
