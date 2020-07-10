package com.oesvica.appibartiFace.utils.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.standby.properUrl
import com.squareup.picasso.Picasso


class StandByDialog: DialogFragment() {

    companion object {

        const val ARG_STAND_BY = "ARG_STAND_BY"
        const val ARG_IS_DELETE = "ARG_IS_DELETE"

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

        view.findViewById<Button>(R.id.addPersonButton).setOnClickListener {
            exitDialogWithResponse(standBy, false)
        }
        view.findViewById<Button>(R.id.deleteStandByButton).setOnClickListener {
            exitDialogWithResponse(standBy, true)
        }

        val standByImageView = view.findViewById<ImageView>(R.id.standByImageView)
        Picasso.get()
            .load(standBy.properUrl())
            .placeholder(R.drawable.photo_placeholder)
            .into(standByImageView)
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    private fun exitDialogWithResponse(standBy: StandBy, isDelete: Boolean) {
        targetFragment?.onActivityResult(
            targetRequestCode,
            Activity.RESULT_OK,
            Intent().apply {
                putExtra(ARG_STAND_BY, standBy)
                putExtra(ARG_IS_DELETE, isDelete)
            }
        )
        dialog?.dismiss()
    }

}