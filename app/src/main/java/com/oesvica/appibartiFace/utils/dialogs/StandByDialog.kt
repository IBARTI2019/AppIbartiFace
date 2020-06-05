package com.oesvica.appibartiFace.utils.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.StandBy
import com.oesvica.appibartiFace.utils.debug
import com.squareup.picasso.Picasso


class StandByDialog: DialogFragment() {

    companion object {

        const val ARG_STAND_BY = "ARG_STAND_BY"
        const val ARG_IS_DELETE = "ARG_IS_DELETE"
        private val ITEMS = arrayOf("Registrar persona", "Eliminar standby")

        fun newInstance(standBy: StandBy): StandByDialog {
            return StandByDialog().apply {
                arguments = bundleOf(
                    ARG_STAND_BY to standBy
                )
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val standBy = arguments?.getParcelable<StandBy>(ARG_STAND_BY)
            ?: throw Exception("No value passed to argument ARG_STAND_BY in StandByDialog")

        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_stand_by, null)
        val standByImageView = view.findViewById<ImageView>(R.id.standByImageView)
        val optionsListView = view.findViewById<ListView>(R.id.optionsListView)
        optionsListView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ITEMS)
        optionsListView.setOnItemClickListener { _, _, position, _ ->
            debug("click position=$position")
            targetFragment?.onActivityResult(
                targetRequestCode,
                Activity.RESULT_OK,
                Intent().apply {
                    putExtra(ARG_STAND_BY, standBy)
                    putExtra(ARG_IS_DELETE, position == 1) // pass whether Delete was selected or not, delete is on index 1 in ITEMS list
                }
            )
        }

        Picasso.get()
            .load(standBy.properUrl)
            .placeholder(R.drawable.photo_placeholder)
            .into(standByImageView)
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

}