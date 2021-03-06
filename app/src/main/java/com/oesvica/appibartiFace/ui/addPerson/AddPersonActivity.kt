package com.oesvica.appibartiFace.ui.addPerson

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi.Companion.imgUrlForStandBy
import com.oesvica.appibartiFace.ui.standby.StandByFragment
import com.oesvica.appibartiFace.utils.base.DaggerActivity
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.dialogs.ProgressDialog
import kotlinx.android.synthetic.main.activity_add_person.*
import observeJustOnce

class AddPersonActivity : DaggerActivity() {

    companion object {
        private const val EXTRA_CLIENT = "EXTRA_CLIENT"
        private const val EXTRA_DEVICE = "EXTRA_DEVICE"
        private const val EXTRA_DATE = "EXTRA_DATE"
        private const val EXTRA_PHOTO = "EXTRA_PHOTO"

        fun starterIntent(
            context: Context,
            client: String,
            device: String,
            date: String,
            photo: String
        ): Intent {
            val starter = Intent(context, AddPersonActivity::class.java)
            starter.putExtra(EXTRA_CLIENT, client)
            starter.putExtra(EXTRA_DEVICE, device)
            starter.putExtra(EXTRA_DATE, date)
            starter.putExtra(EXTRA_PHOTO, photo)
            return starter
        }

    }

    private val addPersonViewModel: AddPersonViewModel by viewModels { viewModelFactory }
    private var categories: List<Category>? = null
    private var statuses: List<Status>? = null

    private val client by lazy { intent.getStringExtra(EXTRA_CLIENT) }
    private val device by lazy { intent.getStringExtra(EXTRA_DEVICE) }
    private val date by lazy { intent.getStringExtra(EXTRA_DATE) }
    private val photo by lazy { intent.getStringExtra(EXTRA_PHOTO) }

    private val predictionsAdapter by lazy {
        PredictionsAdapter(onImageSelected = { cedula ->
            cedulaEditText.setText(cedula)
            debug("cedula $cedula")
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_person)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpPredictionsRecyclerView()
        Glide.with(this)
            .load(imgUrlForStandBy(client, date, photo))
            .placeholder(R.drawable.photo_placeholder)
            .centerCrop()
            .into(urlImageView)
        observeStatuses()
        observeCategories()
        observeAddPersonNetworkRequest()
        observePredictions()
        loadPredictions()
        debug("$client $device $date $photo")
    }

    private fun loadPredictions() {
        addPersonViewModel.loadPredictionsForStandBy(
            StandBy(
                client = client,
                date = date,
                url = photo
            )
        )
        //predictionsRefresh.isRefreshing = true
    }

    private fun setUpPredictionsRecyclerView() {
        with(predictionsRecyclerView) {
            layoutManager = GridLayoutManager(context, StandByFragment.COLUMNS_COUNT)
            adapter = predictionsAdapter
        }
        //predictionsRefresh.isEnabled = false
    }

    private fun observeCategories() {
        addPersonViewModel.categories.observeJustOnce(this, Observer {
            debug("observeCategories $it")
            it?.let { list ->
                categories = list
                categorySpinner.adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Seleccione categoria:") + list.map { cat -> cat.description })
            }
        })
    }

    private fun observeStatuses() {
        addPersonViewModel.statuses.observeJustOnce(this, Observer {
            debug("observeStatuses $it")
            it?.let { list ->
                statuses = list
                statusSpinner.adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Seleccione status:") + list.map { status -> status.description })
            }
        })
    }

    private fun observeAddPersonNetworkRequest() {
        addPersonViewModel.addPersonNetworkRequest.observe(this, Observer {
            if (it.isOngoing) {
                showLoadingDialog()
            } else {
                if (it.error == null) {
                    hideLoadingDialog()
                    finish()
                } else {
                    hideLoadingDialog()
                }
            }
        })
    }

    private fun observePredictions() {
        addPersonViewModel.predictions.observe(this, Observer {
            it?.success?.let { predictions ->
                debug("predictions found=$predictions")
                predictionsAdapter.predictions = predictions
            }
            //predictionsRefresh.isRefreshing = false
        })
    }

    private fun savePerson() {
        if (categories == null) return
        if (statuses == null) return

        if (cedulaEditText.text.isNullOrEmpty()) {
            Toast.makeText(this, "Debe ingresar datos en el campo cedula", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val category = if (categorySpinner.selectedItemPosition == 0) {
            ""
        } else {
            categories!![categorySpinner.selectedItemPosition - 1].id
        }
        val status = if (statusSpinner.selectedItemPosition == 0) {
            ""
        } else {
            statuses!![statusSpinner.selectedItemPosition - 1].id
        }
        addPersonViewModel.addPerson(
            cedula = cedulaEditText.text.toString(),
            category = category,
            status = status,
            client = client,
            device = device,
            date = date,
            photo = photo
        )
    }

    private var loadingDialog: ProgressDialog? = null

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = ProgressDialog.newInstance(
                getString(R.string.add_status_message_loading),
                getString(R.string.add_status_message_saving_changes)
            )
            loadingDialog?.isCancelable = false
            loadingDialog?.show(supportFragmentManager, "")
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_save -> {
                savePerson()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
