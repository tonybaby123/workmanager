package net.appitiza.moderno.ui.activities.users

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_users.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.StartUpActivity
import net.appitiza.moderno.utils.PreferenceHelper


class UsersActivity : BaseActivity() {
    private var isLoggedIn by PreferenceHelper(Constants.PREF_KEY_IS_USER_LOGGED_IN, false)
    private var displayName by PreferenceHelper(Constants.PREF_KEY_IS_USER_DISPLAY_NAME, "")
    private var useremail by PreferenceHelper(Constants.PREF_KEY_IS_USER_EMAIL, "")
    private var userpassword by PreferenceHelper(Constants.PREF_KEY_IS_USER_PASSWORD, "")
    private var usertype by PreferenceHelper(Constants.PREF_KEY_IS_USER_USER_TYPE, "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        setClick()
        hideKeyboard()

    }

    fun setClick() {
        ll_users_home_report.setOnClickListener { loadReport() }
        ll_users_home_history.setOnClickListener { loadHistory() }
        ll_users_home_notification.setOnClickListener { loadNotification() }
    }


    private fun showExitWarning() {
        val mAlert = AlertDialog.Builder(this).create()
        mAlert.setTitle(getString(R.string.app_name))
        mAlert.setMessage(getString(R.string.exit_message))
        mAlert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
            isLoggedIn = false
            displayName = ""
            useremail = ""
            userpassword = ""
            usertype = ""
            mAlert.dismiss()
            startActivity(Intent(this@UsersActivity, StartUpActivity::class.java))
        })
        mAlert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), DialogInterface.OnClickListener { dialog, which ->
            mAlert.dismiss()
            finish()
        })
        mAlert.show()

    }

    fun loadReport() {
        val intent = Intent(this@UsersActivity, UserReportActivity::class.java)

        val p1 = Pair(tv_users_home_reports as View, getString(R.string.txt_usershome_wrkreport))
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@UsersActivity, p1)
        startActivity(intent, options.toBundle())
    }

    fun loadHistory() {
        val intent = Intent(this@UsersActivity, UserHistoryActivity::class.java)

        val p1 = Pair(tv_users_home_history as View, getString(R.string.txt_usershome_history))
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@UsersActivity, p1)
        startActivity(intent, options.toBundle())
    }

    fun loadNotification() {
        val intent = Intent(this@UsersActivity, UserNotificationsActivity::class.java)

        val p1 = Pair(tv_users_home_notification as View, getString(R.string.txt_usershome_notification))
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@UsersActivity, p1)
        startActivity(intent, options.toBundle())
    }

    override fun onBackPressed() {
        isLoggedIn = false
        displayName = ""
        useremail = ""
        userpassword = ""
        usertype = ""
        finish()
        //showExitWarning()
        // super.onBackPressed()
    }
}
