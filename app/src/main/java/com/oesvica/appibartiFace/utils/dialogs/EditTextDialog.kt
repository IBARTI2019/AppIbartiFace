package com.oesvica.appibartiFace.utils.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oesvica.appibartiFace.R

class EditTextDialog : DialogFragment() {

    companion object {

        private const val ARG_TITLE = "title"
        private const val ARG_VALUE = "value"
        private const val ARG_HINT = "hint"
        const val ARG_ID = "ID"

        fun newInstance(title: String, value: String, hint: String, id: String = ""): EditTextDialog {
            return EditTextDialog().apply {
                arguments = bundleOf(
                    ARG_TITLE to title,
                    ARG_VALUE to value,
                    ARG_HINT to hint,
                    ARG_ID to id
                )
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(ARG_TITLE)
            ?: throw Exception("No value passed to argument ARG_TITLE in EditTextDialog")
        val value = arguments?.getString(ARG_VALUE)
            ?: throw Exception("No value passed to argument ARG_VALUE in EditTextDialog")
        val hint = arguments?.getString(ARG_HINT)
            ?: throw Exception("No value passed to argument ARG_HINT in EditTextDialog")
        val id = arguments?.getString(ARG_ID)
            ?: throw Exception("No value passed to argument ARG_ID in EditTextDialog")

        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null)

        val inputEditText = view.findViewById<EditText>(R.id.inputEditText)
        inputEditText.setText(value)
        if (value.isNotEmpty()) inputEditText.setSelection(value.length)
        inputEditText.hint = hint

        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val textTyped = inputEditText.text.toString().trim()
                targetFragment?.onActivityResult(
                    targetRequestCode,
                    Activity.RESULT_OK,
                    Intent().apply {
                        putExtra("DESCRIPTION", textTyped)
                        putExtra(ARG_ID, id)
                    })
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                targetFragment?.onActivityResult(
                    targetRequestCode,
                    Activity.RESULT_CANCELED,
                    Intent())
                dialog.cancel()
            }
            .create()
    }
}