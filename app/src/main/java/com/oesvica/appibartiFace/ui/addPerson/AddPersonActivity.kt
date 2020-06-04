package com.oesvica.appibartiFace.ui.addPerson

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.Status
import com.oesvica.appibartiFace.data.remote.AppIbartiFaceApi.Companion.properUrl
import com.oesvica.appibartiFace.utils.base.DaggerActivity
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.dialogs.ProgressDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_person.*
import observeJustOnce

class AddPersonActivity : DaggerActivity() {

    companion object {
        private const val EXTRA_CLIENT = "EXTRA_CLIENT"
        private const val EXTRA_DEVICE = "EXTRA_DEVICE"
        private const val EXTRA_DATE = "EXTRA_DATE"
        private const val EXTRA_PHOTO = "EXTRA_PHOTO"
        private const val EXTRA_CEDULA = "EXTRA_CEDULA"
        private const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private const val EXTRA_STATUS = "EXTRA_STATUS"

        fun starterIntent(
            context: Context,
            client: String,
            device: String,
            date: String,
            photo: String,
            cedula: String? = null,
            category: String? = null,
            status: String? = null
        ): Intent {
            val starter = Intent(context, AddPersonActivity::class.java)
            starter.putExtra(EXTRA_CLIENT, client)
            starter.putExtra(EXTRA_DEVICE, device)
            starter.putExtra(EXTRA_DATE, date)
            starter.putExtra(EXTRA_PHOTO, photo)
            if (cedula != null) {
                starter.putExtra(EXTRA_CEDULA, cedula)
                starter.putExtra(EXTRA_CATEGORY, category)
                starter.putExtra(EXTRA_STATUS, status)
            }
            return starter
        }

    }

    private val addPersonViewModel by lazy { getViewModel<AddPersonViewModel>() }
    private var categories: List<Category>? = null
    private var statuses: List<Status>? = null

    private val client by lazy { intent.getStringExtra(EXTRA_CLIENT) }
    private val device by lazy { intent.getStringExtra(EXTRA_DEVICE) }
    private val date by lazy { intent.getStringExtra(EXTRA_DATE) }
    private val photo by lazy { intent.getStringExtra(EXTRA_PHOTO) }

    private val cedula by lazy { intent.getStringExtra(EXTRA_CEDULA) }
    private val category by lazy { intent.getStringExtra(EXTRA_CATEGORY) }
    private val status by lazy { intent.getStringExtra(EXTRA_STATUS) }

    private val isInEditMode: Boolean
        get() = cedula != null && category != null && status != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_person)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Picasso.get()
            .load(properUrl(client, date, photo))
            .placeholder(R.drawable.photo_placeholder)
            .into(urlImageView)
        observeStatuses()
        observeCategories()
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

    private fun savePerson() {
        debug("savePerson categories=$categories selected=${categorySpinner.selectedItemPosition}")
        debug("savePerson statuses=$statuses selected=${statusSpinner.selectedItemPosition}")
        if (categories == null) return
        if (statuses == null) return

        if (cedulaEditText.text.isNullOrEmpty()) {
            Toast.makeText(this, "Debe ingresar datos en el campo cedula", Toast.LENGTH_SHORT).show()
            return
        }
        if (categorySpinner.selectedItemPosition == 0) {
            Toast.makeText(this, "Debe seleccionar un valor valido en el campo categoria", Toast.LENGTH_SHORT).show()
            return
        }
        if (statusSpinner.selectedItemPosition == 0) {
            Toast.makeText(this, "Debe seleccionar un valor valido en el campo status", Toast.LENGTH_SHORT).show()
            return
        }
        //showLoadingDialog()
        addPersonViewModel.addPerson(
            cedula = cedulaEditText.text.toString(),
            category = categories!![categorySpinner.selectedItemPosition].id,
            status = statuses!![statusSpinner.selectedItemPosition].id,
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
