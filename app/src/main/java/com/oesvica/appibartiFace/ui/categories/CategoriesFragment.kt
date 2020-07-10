package com.oesvica.appibartiFace.ui.categories

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.dialogs.EditTextDialog
import distinc
import kotlinx.android.synthetic.main.fragment_category_list.*

/**
 * A fragment representing a list of ca.
 */
class CategoriesFragment : DaggerFragment() {

    companion object {
        const val REQUEST_ADD_CATEGORY = 1001
        const val REQUEST_UPDATE_CATEGORY = 1002
        const val DESCRIPTION = "DESCRIPTION"
        const val ID = "ID"
    }

    private val categoriesViewModel by lazy { getViewModel<CategoriesViewModel>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    private val categoriesAdapter by lazy {
        CategoriesAdapter(requireContext(), onEdit = {
            showCategoryDialog(REQUEST_UPDATE_CATEGORY, "Editar categoria", it.description, it.id)
        }, onDelete = {
            categoriesViewModel.deleteCategory(it.id)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpRecyclerView()
        addCategoryButton.setOnClickListener {
            showCategoryDialog(REQUEST_ADD_CATEGORY, "Agregar categoria")
        }
        observeCategories()
        observeSnackbarMessages()
        categoriesRefreshLayout.setOnRefreshListener { categoriesViewModel.refreshCategories() }
        categoriesViewModel.refreshCategories()
        super.onActivityCreated(savedInstanceState)
    }

    private fun observeSnackbarMessages() {
        categoriesViewModel.snackBarMsg.observe(viewLifecycleOwner, Observer {
            it?.let {
                debug("snack $it")
                Snackbar.make(categoriesFrameLayout, it, Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun showCategoryDialog(
        requestCode: Int,
        title: String,
        defaultValue: String = "",
        id: String = ""
    ) {
        val addCategoryDialog = EditTextDialog.newInstance(title, defaultValue, "Descripcion", id)
        addCategoryDialog.setTargetFragment(this, requestCode)
        addCategoryDialog.isCancelable = false
        addCategoryDialog.show(requireFragmentManager(), "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        debug("onActivityResult $requestCode $resultCode ${data?.getStringExtra("DESCRIPTION")}")
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode){
                REQUEST_ADD_CATEGORY -> {
                    val desc = data?.getStringExtra(DESCRIPTION)
                    if (!desc.isNullOrEmpty()) {
                        categoriesViewModel.addCategory(desc)
                    }
                }
                REQUEST_UPDATE_CATEGORY -> {
                    val id = data?.getStringExtra(EditTextDialog.ARG_ID) ?: ""
                    val desc = data?.getStringExtra(DESCRIPTION)
                    if (id.isNotEmpty() && !desc.isNullOrEmpty()) {
                        categoriesViewModel.updateCategory(
                            Category(
                                id,
                                desc
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        with(categoriesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = categoriesAdapter
        }
    }

    private fun observeCategories() {
        categoriesViewModel.categories.distinc().observe(viewLifecycleOwner, Observer { categories ->
            debug("observe categories = $categories")
            categories?.let {
                categoriesAdapter.categories = it
            }
        })
        categoriesViewModel.statusCategories.observe(viewLifecycleOwner, Observer {
            categoriesRefreshLayout.isRefreshing = it.isOngoing
        })
    }
}
