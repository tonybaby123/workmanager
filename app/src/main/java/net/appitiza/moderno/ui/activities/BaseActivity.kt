package net.appitiza.moderno.ui.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import net.appitiza.moderno.R

open class BaseActivity : AppCompatActivity() {

    fun showValidationWarning(message: String) {
        val mAlert = AlertDialog.Builder(this).create()
        mAlert.setTitle(getString(R.string.app_name))
        mAlert.setMessage(message)
        mAlert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->

            mAlert.dismiss()
        })

        mAlert.show()

    }
}
