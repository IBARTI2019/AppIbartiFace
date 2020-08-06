package com.oesvica.appibartiFace.ui.editPerson

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.utils.base.DaggerActivity
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.dialogs.ProgressDialog
import kotlinx.android.synthetic.main.activity_edit_person.*
import observeJustOnce

class EditPersonActivity : DaggerActivity() {

    companion object {

        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_CEDULA = "EXTRA_CEDULA"
        private const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private const val EXTRA_STATUS = "EXTRA_STATUS"
        private const val EXTRA_PHOTO = "EXTRA_PHOTO"

        fun starterIntent(
            context: Context,
            id: String,
            cedula: String,
            category: String,
            status: String,
            photo: String
        ): Intent {
            val starter = Intent(context, EditPersonActivity::class.java)
            starter.putExtra(EXTRA_ID, id)
            starter.putExtra(EXTRA_CEDULA, cedula)
            starter.putExtra(EXTRA_CATEGORY, category)
            starter.putExtra(EXTRA_STATUS, status)
            starter.putExtra(EXTRA_PHOTO, photo)
            return starter
        }

    }

    private val editPersonViewModel: EditPersonViewModel by viewModels { viewModelFactory }
    private var categories: List<Category>? = null
    private var statuses: List<Status>? = null

    private val id by lazy { intent.getStringExtra(EXTRA_ID) }
    private val cedula by lazy { intent.getStringExtra(EXTRA_CEDULA) }
    private val category by lazy { intent.getStringExtra(EXTRA_CATEGORY) }
    private val status by lazy { intent.getStringExtra(EXTRA_STATUS) }
    private val photo by lazy { intent.getStringExtra(EXTRA_PHOTO) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_person)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        debug("cedula=$cedula category=$category status=$status")
        cedulaEditText.setText(cedula)
        cedulaEditText.isEnabled = false
        if (photo != null) {
            Glide.with(this)
                .load(photo)
                .placeholder(R.drawable.photo_placeholder)
                .centerCrop()
                .into(personImageView)
        }
        observeStatuses()
        observeCategories()
        observeEditPersonNetworkRequest()
    }

    private fun observeCategories() {
        editPersonViewModel.categories.observeJustOnce(this, Observer {
            debug("observeCategories $it")
            it?.let { list ->
                categories = list
                categorySpinner.adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Seleccione categoria:") + list.map { cat -> cat.description })
                categorySpinner.setSelection(list.map { cat -> cat.description }
                    .indexOf(category) + 1)
            }
        })
    }

    private fun observeStatuses() {
        editPersonViewModel.statuses.observeJustOnce(this, Observer {
            debug("observeStatuses $it")
            it?.let { list ->
                statuses = list
                statusSpinner.adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Seleccione status:") + list.map { status -> status.description })
                statusSpinner.setSelection(list.map { status -> status.description }
                    .indexOf(status) + 1)
            }
        })
    }

    private fun observeEditPersonNetworkRequest() {
        editPersonViewModel.editPersonNetworkRequest.observe(this, Observer {
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

    fun editPerson() {
        debug("editPerson categories=$categories selected=${categorySpinner.selectedItemPosition}")
        debug("editPerson statuses=$statuses selected=${statusSpinner.selectedItemPosition}")
        if (categories == null) return
        if (statuses == null) return
        if (categorySpinner.selectedItemPosition == 0) {
            Toast.makeText(
                this,
                "Debe seleccionar un valor valido en el campo categoria",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (statusSpinner.selectedItemPosition == 0) {
            Toast.makeText(
                this,
                "Debe seleccionar un valor valido en el campo status",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        editPersonViewModel.editPerson(
            idPerson = id,
            idCategory = categories!![categorySpinner.selectedItemPosition - 1].id,
            idStatus = statuses!![statusSpinner.selectedItemPosition - 1].id
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
                editPerson()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
