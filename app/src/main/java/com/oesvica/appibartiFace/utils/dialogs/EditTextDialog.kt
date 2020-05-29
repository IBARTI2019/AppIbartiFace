package com.oesvica.appibartiFace.utils.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.oesvica.appibartiFace.R

class EditTextDialog : DialogFragment() {

    companion object {

        private const val ARG_FIELD_TO_ENTER = "fieldToEnter"
        private const val ARG_TITLE = "title"
        private const val ARG_HINT = "hint"

        fun newInstance(fieldToEnter: String, hint: String, title: String): EditTextDialog {
            return EditTextDialog().apply {
                arguments = bundleOf(ARG_FIELD_TO_ENTER to fieldToEnter, ARG_HINT to hint, ARG_TITLE to title)
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fieldToEnter = arguments?.getString(ARG_FIELD_TO_ENTER)
                ?: throw Exception("No value passed to argument ARG_FIELD_TO_ENTER in EditTextDialog")
        val title = arguments?.getString(ARG_TITLE)
                ?: throw Exception("No value passed to argument ARG_TITLE in EditTextDialog")
        val hint = arguments?.getString(ARG_HINT)
                ?: throw Exception("No value passed to argument ARG_HINT in EditTextDialog")

        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null)

        val fieldToEnterEditText = view.findViewById<EditText>(R.id.fieldToEnterEditText)
        fieldToEnterEditText.setText(fieldToEnter)
        fieldToEnterEditText.hint = hint

        return AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle(title)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val textTyped = fieldToEnterEditText.text.toString().trim()
                    if (textTyped != fieldToEnter) getRegisteredListener()?.onTextTyped(textTyped)
                    val resultCode = 12
                    targetFragment?.onActivityResult(targetRequestCode, resultCode, Intent().apply { putExtra("DESCRIPTION", textTyped) })
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .create()
    }

    private fun getRegisteredListener(): EditTextListener? {
        return when {
            targetFragment is EditTextListener -> targetFragment as EditTextListener
            context is EditTextListener -> context as EditTextListener
            activity is EditTextListener -> activity as EditTextListener
            else -> null //throw Exception("You must implement EditTextListener in your activity, " + "context or targetFragment associated with this EditTextDialog")
        }
    }

    interface EditTextListener {
        fun onTextTyped(textTyped: String)
    }

}