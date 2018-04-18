package net.appitiza.moderno.ui.activities.users

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_admin_sites.*
import kotlinx.android.synthetic.main.activity_users.*
import net.appitiza.moderno.BuildConfig
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.StartUpActivity
import net.appitiza.moderno.utils.PreferenceHelper
import java.util.*


class UsersActivity : BaseActivity() {
    private var isLoggedIn by PreferenceHelper(Constants.PREF_KEY_IS_USER_LOGGED_IN, false)
    private var displayName by PreferenceHelper(Constants.PREF_KEY_IS_USER_DISPLAY_NAME, "")
    private var useremail by PreferenceHelper(Constants.PREF_KEY_IS_USER_EMAIL, "")
    private var userpassword by PreferenceHelper(Constants.PREF_KEY_IS_USER_PASSWORD, "")
    private var usertype by PreferenceHelper(Constants.PREF_KEY_IS_USER_USER_TYPE, "")

    private val TAG = "LOCATION"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        initialize()
        setClick()
        hideKeyboard()

    }

    fun initialize() {
        db = FirebaseFirestore.getInstance()
        updateFcm()
    }

    fun setClick() {
        ll_users_home_report.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!checkPermissions()) {
                    requestPermissions()
                } else {
                    loadReport()
                }

            } else {
                loadReport()
            }


        }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!checkPermissions()) {
                requestPermissions()
            } else {
                val intent = Intent(this@UsersActivity, UserReportActivity::class.java)

                val p1 = Pair(tv_users_home_reports as View, getString(R.string.txt_usershome_wrkreport))
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@UsersActivity, p1)
                startActivity(intent, options.toBundle())
            }

        } else {
            val intent = Intent(this@UsersActivity, UserReportActivity::class.java)

            val p1 = Pair(tv_users_home_reports as View, getString(R.string.txt_usershome_wrkreport))
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@UsersActivity, p1)
            startActivity(intent, options.toBundle())
        }


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

    private fun updateFcm() {
        val deviceToken: String? = FirebaseInstanceId.getInstance().token
        val map = HashMap<String, Any>()
        map[Constants.USER_TOKEN] = deviceToken.toString()
        db.collection(Constants.COLLECTION_USER)
                .document(useremail)
                .set(map, SetOptions.merge())
        FirebaseMessaging.getInstance().subscribeToTopic("notification");
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            Snackbar.make(
                    fab_admin_add_site,
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, View.OnClickListener {
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_PERMISSIONS_REQUEST_CODE)
                    })
                    .show()
        } else {
            Log.i(TAG, "Requesting permission")

            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                loadReport()
            } else {
                // Permission denied.
                Snackbar.make(fab_admin_add_site,
                        R.string.permission_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, View.OnClickListener {
                            var intent: Intent = Intent()
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            var uri: Uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null)
                            intent.setData(uri)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        })
                        .show()

            }
        }

    }

    override fun onBackPressed() {

        showExitWarning()
        // super.onBackPressed()
    }


}
