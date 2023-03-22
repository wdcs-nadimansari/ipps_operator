package com.webclues.IPPSOperator.utility

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.ProgressBar
import com.webclues.IPPSOperator.R


class CustomProgress {
    private var mDialog: Dialog? = null

    fun showProgress(context: Context, message: String, cancelable: Boolean) {
        if (mDialog == null) {

            mDialog = Dialog(context)
            // no tile for the dialog
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setContentView(R.layout.progressbar)
            val mProgressBar = mDialog!!.findViewById(R.id.progressBar) as ProgressBar

            val progressText = mDialog!!.findViewById(R.id.ProgressBarTitle) as TextView
            progressText.text = "" + message
            progressText.visibility = View.VISIBLE
            mProgressBar.visibility = View.VISIBLE

            mProgressBar.isIndeterminate = true
            mDialog!!.setCancelable(cancelable)
            mDialog!!.setCanceledOnTouchOutside(cancelable)
            mDialog!!.show()
        }
    }

    fun hideProgress() {
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
            mDialog = null
        }
    }

    companion object {

        var customProgress: CustomProgress? = null

        val instance: CustomProgress
            get() {
                if (customProgress == null) {
                    customProgress = CustomProgress()
                }
                return customProgress!!
            }
    }
}