package com.oesvica.appibartiFace.ui.personAsistencia.personImage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oesvica.appibartiFace.R

class PersonImgDialog: DialogFragment() {

    private val args by navArgs<PersonImgDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_person_img, null)

        val standByImageView = view.findViewById<ImageView>(R.id.personImageView)
        Glide.with(this)
//            .load("http://oesvica.ddns.net:5005/view/reconocidos/001/21479824/2020-07-03/001+0001+2020-07-03+06:38:50:714970.jpg/") // TODO
            .load(args.photoUrl) // TODO
            .placeholder(R.drawable.photo_placeholder)
            .centerCrop()
            .into(standByImageView)
        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setCancelable(true)
            .setOnCancelListener {
                dialog?.dismiss()
            }
            .create()
    }

}