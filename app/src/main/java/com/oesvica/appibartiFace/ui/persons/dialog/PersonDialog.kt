package com.oesvica.appibartiFace.ui.persons.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.person.Person
import com.oesvica.appibartiFace.data.model.person.fullPhotoUrl

class PersonDialog: DialogFragment() {

    companion object {

        const val ARG_PERSON = "ARG_PERSON"
        const val ARG_IS_DELETE = "ARG_IS_DELETE"

    }

    private val args by navArgs<PersonDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val person = args.person

        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_person, null)

        view.findViewById<Button>(R.id.editPersonButton).setOnClickListener {
            exitDialogWithResponse(person, false)
        }
        view.findViewById<Button>(R.id.deletePersonButton).setOnClickListener {
            exitDialogWithResponse(person, true)
        }

        val standByImageView = view.findViewById<ImageView>(R.id.personImageView)
        Glide.with(this)
            .load(person.fullPhotoUrl())
            .placeholder(R.drawable.photo_placeholder)
            .centerCrop()
            .into(standByImageView)
        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .create()
    }

    private fun exitDialogWithResponse(person: Person, isDelete: Boolean) {
        with(findNavController().previousBackStackEntry?.savedStateHandle) {
            this?.set(ARG_PERSON, person)
            this?.set(ARG_IS_DELETE, isDelete)
        }
        dialog?.dismiss()
    }
}