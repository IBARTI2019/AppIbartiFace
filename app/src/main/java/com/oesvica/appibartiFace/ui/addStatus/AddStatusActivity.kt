package com.oesvica.appibartiFace.ui.addStatus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.Category
import com.oesvica.appibartiFace.data.model.Status
import com.oesvica.appibartiFace.utils.base.DaggerActivity
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.dialogs.ProgressDialog
import kotlinx.android.synthetic.main.activity_add_status.*
import observeJustOnce

class AddStatusActivity : DaggerActivity() {

    companion object {

        private const val EXTRA_STATUS = "EXTRA_STATUS"

        fun starterIntent(context: Context, status: Status? = null): Intent {
            val starter = Intent(context, AddStatusActivity::class.java)
            if (status != null) starter.putExtra(EXTRA_STATUS, status)
            return starter
        }

    }

    private val statusToEdit: Status? by lazy { intent.getParcelableExtra<Status>(EXTRA_STATUS) }
    private val isInEditMode: Boolean
        get() = statusToEdit != null
    private val addStatusViewModel by lazy { getViewModel<AddStatusViewModel>() }
    private var categories: List<Category>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_status)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        observeCategories()
        if (isInEditMode) {
            supportActionBar?.setTitle(R.string.edit_status)
            statusDescriptionEditText.setText(statusToEdit!!.description)
        }
    }

    private fun observeCategories() {
        addStatusViewModel.categories.observeJustOnce(this, Observer {
            it?.let { categoriesList ->
                categories = categoriesList
                statusCategorySpinner.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    categoriesList.map { cat -> cat.description })
                if (isInEditMode) {
                    val category = statusToEdit!!.category.trim()
                    statusCategorySpinner.setSelection(categoriesList.map { cat -> cat.description.trim() }
                        .indexOf(category))
                }
            }
        })
        addStatusViewModel.addStatusNetworkRequest.observe(this, Observer {
            debug("networkRequestCategories.observe $it")
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

    private fun saveStatus() {
        debug("saveStatus categories=$categories selected=${statusCategorySpinner.selectedItemPosition}")
        if (categories == null) return
        addStatusViewModel.addStatus(
            statusId = statusToEdit?.id,
            categoryId = categories!![statusCategorySpinner.selectedItemPosition].id,
            description = statusDescriptionEditText.text.toString()
        )
    }

    private var loadingDialog: ProgressDialog? = null

    private fun showLoadingDialog() {
        debug("showLoadingDialog")
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
        debug("hideLoadingDialog")
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
                saveStatus()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
