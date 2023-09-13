package com.teamtechnojam.campusconnect.ui.customUIComponents

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.teamtechnojam.campusconnect.R

object LoadingDialog {

    private lateinit var dialog: Dialog

    fun getLoadingDialog(context: Context): Dialog {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.loading_pop_up)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        return dialog
    }
}