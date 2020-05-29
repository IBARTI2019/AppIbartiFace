package com.oesvica.appibartiFace.ui.categories

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.utils.base.DaggerFragment
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.dialogs.EditTextDialog
import kotlinx.android.synthetic.main.fragment_category_list.*

/**
 * A fragment representing a list of ca.
 */
class CategoriesFragment : DaggerFragment(), EditTextDialog.EditTextListener {

    private val categoriesViewModel by lazy { getViewModel<CategoriesViewModel>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    private val categoriesAdapter by lazy {
        CategoriesAdapter(requireContext())
    }

    override fun onTextTyped(textTyped: String) {
        debug("typed $textTyped")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpRecyclerView()
        addCategoryButton.setOnClickListener {
            debug("adding category")
            val nf = EditTextDialog.newInstance("", "Descripcion", "Agregar categoria")
            nf.setTargetFragment(this, 54)
            nf.show(requireFragmentManager(), "")
        }
        observeCategories()
        categoriesViewModel.loadCategories()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        debug("onActivityResult $requestCode $resultCode ${data?.getStringExtra("DESCRIPTION")}")
    }

    private fun setUpRecyclerView() {
        with(categoriesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = categoriesAdapter
        }
    }

    private fun observeCategories() {
        categoriesViewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let {
                categoriesAdapter.categories = it
            }
        })
    }
}
