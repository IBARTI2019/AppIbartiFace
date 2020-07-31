package com.oesvica.appibartiFace.utils.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.standby.properUrl
import com.squareup.picasso.Picasso


class StandByDialog : DialogFragment() {

    companion object {

        const val ARG_STAND_BY = "ARG_STAND_BY"
        const val ARG_IS_DELETE = "ARG_IS_DELETE"

    }

    private val args by navArgs<StandByDialogArgs>()

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val standBy = args.standby
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
        with(findNavController().previousBackStackEntry?.savedStateHandle) {
            this?.set(ARG_STAND_BY, standBy)
            this?.set(ARG_IS_DELETE, isDelete)
        }
        dialog?.dismiss()
    }

}