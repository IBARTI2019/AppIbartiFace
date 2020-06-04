package com.oesvica.appibartiFace.utils.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

class ProgressDialog : DialogFragment() {

    companion object {

        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"

        fun newInstance(title: String, message: String): com.oesvica.appibartiFace.utils.dialogs.ProgressDialog {
            return com.oesvica.appibartiFace.utils.dialogs.ProgressDialog().apply {
                arguments = bundleOf(ARG_TITLE to title, ARG_MESSAGE to message)
            }
        }

    }

    private var dialog: android.app.ProgressDialog? = null

    /**
     * Set a listener to be invoked when the dialog is canceled.
     *
     * @param listener The [DialogInterface.OnCancelListener] to use.
     */
    fun setOnCancelListener(listener: () -> Unit) {
        dialog?.setOnCancelListener {
            listener()
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(ARG_TITLE)
                ?: throw Exception("No value passed to argument ARG_TITLE in EditTextDialog")
        val message = arguments?.getString(ARG_MESSAGE)
                ?: throw Exception("No value passed to argument ARG_MESSAGE in EditTextDialog")

        dialog = android.app.ProgressDialog(context).apply {
            setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER)
            setCancelable(false)
            setTitle(title)
            setMessage(message)
        }
        return dialog!!
    }

    fun hide() {
        dialog?.hide()
    }

}