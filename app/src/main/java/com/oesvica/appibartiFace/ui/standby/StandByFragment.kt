package com.oesvica.appibartiFace.ui.standby

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.currentDay
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.standby.StandByQuery
import com.oesvica.appibartiFace.ui.addPerson.AddPersonActivity
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.dialogs.StandByDialog
import com.oesvica.appibartiFace.utils.hideSoftInput
import distinct
import kotlinx.android.synthetic.main.fragment_stand_by_list.*


/**
 * A fragment representing a list of StandBys.
 */
class StandByFragment : DaggerFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val COLUMNS_COUNT = 3
        const val KEY_SELECTED_DATE = "selectedDate"
        const val KEY_LAST_QUERY_TRIGGERED = "lastQueryTriggered"
        const val KEY_RECYCLER_STATE = "StandBysRecyclerViewState"
    }

    private val standByViewModel: StandByViewModel by viewModels { viewModelFactory }

    private var datePickerDialog: DatePickerDialog? = null
    private var selectedDate: CustomDate? = null
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            value?.let { dateTextView.text = "Fecha: $it" }
        }
    private var lastQueryTriggered: StandByQuery? = null

    private val standByAdapter by lazy {
        StandByAdapter(onStandBySelected = { standBy ->
            showOptionsDialog(standBy)
        })
    }

    private fun showOptionsDialog(standBy: StandBy) {
        val action = StandByFragmentDirections.actionNavStandbyToDialogStandby(standBy)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stand_by_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView(savedInstanceState)
        selectedDate =
            savedInstanceState?.getParcelable<CustomDate?>(KEY_SELECTED_DATE) ?: currentDay()
        debug("onViewCreated ${savedInstanceState?.getParcelable<CustomDate?>(KEY_SELECTED_DATE)}")
        dateTextView.setOnClickListener { showDatePickerDialog() }
        searchStandBysIcon.setOnClickListener { queryStandBys() }
        observeStandBys()
        setUpClientAutocomplete()
        val tempLastQueryTriggered =
            savedInstanceState?.getParcelable<StandByQuery?>(KEY_LAST_QUERY_TRIGGERED)
        debug("onViewCreated tempLastQueryTriggered $tempLastQueryTriggered")
        if (tempLastQueryTriggered != null) {
            lastQueryTriggered = tempLastQueryTriggered
            standByViewModel.searchStandBys(tempLastQueryTriggered)
        }
        handleDialogResponse()
    }

    private fun handleDialogResponse() {
        val navController = findNavController()
        // After a configuration change or process death, the currentBackStackEntry
        // points to the dialog destination, so you must use getBackStackEntry()
        // with the specific ID of your destination to ensure we always
        // get the right NavBackStackEntry
        val navBackStackEntry = navController.getBackStackEntry(R.id.nav_standby)

        // Create our observer and add it to the NavBackStackEntry's lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (navBackStackEntry.savedStateHandle.contains(StandByDialog.ARG_STAND_BY) && navBackStackEntry.savedStateHandle.contains(
                        StandByDialog.ARG_IS_DELETE
                    )
                ) {
                    val standBy =
                        navBackStackEntry.savedStateHandle.get<StandBy>(StandByDialog.ARG_STAND_BY)
                            ?: return@LifecycleEventObserver
                    val isDelete =
                        navBackStackEntry.savedStateHandle.get<Boolean>(StandByDialog.ARG_IS_DELETE)
                            ?: return@LifecycleEventObserver
                    navBackStackEntry.savedStateHandle.remove<StandBy>(StandByDialog.ARG_STAND_BY)
                    navBackStackEntry.savedStateHandle.remove<Boolean>(StandByDialog.ARG_IS_DELETE)
                    debug("standBy $standBy $isDelete")
                    if (isDelete) {
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
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        // As addObserver() does not automatically remove the observer, we
        // call removeObserver() manually when the view lifecycle is destroyed
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    private fun setUpClientAutocomplete() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            standByViewModel.getClients()
        )
        clientEditText.setAdapter(adapter)
    }

    private fun queryStandBys() {
        val query = getQueryForStandBys(displayErrorMessages = true)
        if (query != null) {
            clientEditText.clearFocus()
            context?.hideSoftInput(searchContainer)
            standByViewModel.searchStandBys(query, force = true)
            lastQueryTriggered = query
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        debug("onSaveInstanceState")
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
        return StandByQuery(
            client,
            selectedDate ?: return null
        )
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
            if (query != null) standByViewModel.searchStandBys(query, force = true)
        }
    }

    private fun observeStandBys() {
        standByViewModel.standBys.distinct().observe(viewLifecycleOwner, Observer {
            debug("standBys.observe ${it?.take(3)}")
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
