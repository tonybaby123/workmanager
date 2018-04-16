package net.appitiza.moderno.ui.activities.admin

import android.Manifest
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_sites.*
import net.appitiza.moderno.BuildConfig
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.AdminSiteAdapter
import net.appitiza.moderno.ui.activities.interfaces.AdminSiteClick
import net.appitiza.moderno.ui.activities.services.LocationUpdatesService
import net.appitiza.moderno.ui.activities.utils.Utils
import net.appitiza.moderno.ui.model.SiteListdata


class AdminSitesActivity : BaseActivity(), AdminSiteClick {
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    private lateinit var mSiteList: ArrayList<SiteListdata>
    private lateinit var adapter: AdminSiteAdapter
    val LOCATION_SETTINGS = 2
    private val TAG = "LOCATION"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private var mGpsLocation: Location? = null
    // Monitors the state of the connection to the service.
    private lateinit var myReceiver : BroadcastReceiver

    // A reference to the service used to get location updates.
    private var mService: LocationUpdatesService? = null

    // Tracks the bound state of the service.
    private var mBound = false
    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_sites)
        initializeFireBase()
        setClick()
        getAll()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                IntentFilter(LocationUpdatesService.ACTION_BROADCAST))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
        super.onPause()
    }

    override fun onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mBound = false
        }
        /* PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);*/
        super.onStop()
    }
    private fun initializeFireBase() {
        rv_admin_site_all.layoutManager = LinearLayoutManager(this)

        mSiteList = arrayListOf()
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        myReceiver = MyReceiver()
    }

    private fun setClick() {
        fab_admin_add_site.setOnClickListener {

            addSite()


        }
        tv_admin_site_all.setOnClickListener {
            mSiteList.clear()
            getAll()

        }
        tv_admin_site_undergoing.setOnClickListener {
            mSiteList.clear()
            getUndergoing()
        }
        tv_admin_site_completed.setOnClickListener {
            mSiteList.clear()
            getcompleted()
        }
    }

    private fun addSite() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!checkPermissions()) {
                requestPermissions()
            } else {
                val intent = Intent(this@AdminSitesActivity, CreateSiteActivity::class.java)
                startActivity(intent)
            }

        } else {
            val intent = Intent(this@AdminSitesActivity, CreateSiteActivity::class.java)
            startActivity(intent)
        }


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
                val intent = Intent(this@AdminSitesActivity, CreateSiteActivity::class.java)
                startActivity(intent)
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

    private fun getAll() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.getting_site))
        mProgress?.setCancelable(false)
        mProgress?.show()
        tv_admin_site_all.setTextColor(ContextCompat.getColor(this, R.color.text_clicked))
        tv_admin_site_completed.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_undergoing.setTextColor(ContextCompat.getColor(this, R.color.white))

        db.collection(Constants.COLLECTION_SITE)
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()
                    if (fetchall_task.isSuccessful) {
                        for (document in fetchall_task.result) {
                            // Log.d(FragmentActivity.TAG, document.id + " => " + document.getData())
                            val data: SiteListdata = SiteListdata()
                            data.siteid = document.id
                            data.sitename = document.data[Constants.SITE_NAME].toString()
                            data.type = document.data[Constants.SITE_TYPE].toString()
                            data.date = document.data[Constants.SITE_DATE].toString()
                            data.cost = document.data[Constants.SITE_COST].toString()
                            data.contact = document.data[Constants.SITE_CONTACT].toString()
                            data.person = document.data[Constants.SITE_PERSON].toString()
                            data.status = document.data[Constants.SITE_STATUS].toString()
                            mSiteList.add(data)

                        }
                        adapter = AdminSiteAdapter(mSiteList, this)
                        rv_admin_site_all.adapter = adapter

                    } else {
                        Toast.makeText(this@AdminSitesActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()

                    }
                }


    }

    private fun getUndergoing() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.getting_undergoing_site))
        mProgress?.setCancelable(false)
        mProgress?.show()
        tv_admin_site_all.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_completed.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_undergoing.setTextColor(ContextCompat.getColor(this, R.color.text_clicked))
        db.collection(Constants.COLLECTION_SITE)
                .whereEqualTo(Constants.SITE_STATUS, "undergoing")
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()
                    if (fetchall_task.isSuccessful) {
                        for (document in fetchall_task.getResult()) {
                            // Log.d(FragmentActivity.TAG, document.id + " => " + document.getData())
                            val data: SiteListdata = SiteListdata()
                            data.siteid = document.id
                            data.sitename = document.data[Constants.SITE_NAME].toString()
                            data.type = document.data[Constants.SITE_TYPE].toString()
                            data.date = document.data[Constants.SITE_DATE].toString()
                            data.cost = document.data[Constants.SITE_COST].toString()
                            data.contact = document.data[Constants.SITE_CONTACT].toString()
                            data.person = document.data[Constants.SITE_PERSON].toString()
                            data.status = document.data[Constants.SITE_STATUS].toString()
                            mSiteList.add(data)

                        }
                        adapter = AdminSiteAdapter(mSiteList, this)
                        rv_admin_site_all.adapter = adapter

                    } else {
                        Toast.makeText(this@AdminSitesActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()

                    }
                }
    }

    private fun getcompleted() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.getting_completed_site))
        mProgress?.setCancelable(false)
        mProgress?.show()
        tv_admin_site_all.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_completed.setTextColor(ContextCompat.getColor(this, R.color.text_clicked))
        tv_admin_site_undergoing.setTextColor(ContextCompat.getColor(this, R.color.white))
        db.collection(Constants.COLLECTION_SITE)
                .whereEqualTo(Constants.SITE_STATUS, "complete")
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()
                    if (fetchall_task.isSuccessful) {
                        for (document in fetchall_task.getResult()) {
                            // Log.d(FragmentActivity.TAG, document.id + " => " + document.getData())
                            val data: SiteListdata = SiteListdata()
                            data.siteid = document.id
                            data.sitename = document.data[Constants.SITE_NAME].toString()
                            data.type = document.data[Constants.SITE_TYPE].toString()
                            data.date = document.data[Constants.SITE_DATE].toString()
                            data.cost = document.data[Constants.SITE_COST].toString()
                            data.contact = document.data[Constants.SITE_CONTACT].toString()
                            data.person = document.data[Constants.SITE_PERSON].toString()
                            data.status = document.data[Constants.SITE_STATUS].toString()
                            mSiteList.add(data)

                        }
                        adapter = AdminSiteAdapter(mSiteList, this)
                        rv_admin_site_all.adapter = adapter

                    } else {
                        Toast.makeText(this@AdminSitesActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()

                    }
                }
    }

    override fun onSiteClick(data: SiteListdata) {
        val intent = Intent(this@AdminSitesActivity, AdminEditSiteActivity::class.java)
        intent.putExtra("site_data", data)
        startActivity(intent)
    }

    private fun displayLocationSettingsRequest() {
        /* PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);*/
        bindService(Intent(this, LocationUpdatesService::class.java), mServiceConnection,
                Context.BIND_AUTO_CREATE)
        val googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> initiateLocationReceiverAndService()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(this, LOCATION_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {

                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }

    private fun initiateLocationReceiverAndService() {


        if (mProgress != null && mProgress!!.isShowing()) {
            mProgress?.dismiss()
        }
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.getting_site))
        mProgress?.setCancelable(false)
        mProgress?.show()
        mService!!.requestLocationUpdates()
    }
    /**
     * Receiver for broadcasts sent by [LocationUpdatesService].
     */
    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
            if (location != null) {
                Toast.makeText(applicationContext, Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show()
                mService?.removeLocationUpdates()
                if (mProgress != null && mProgress!!.isShowing()) {
                    mProgress?.dismiss()
                }
                mGpsLocation = location
            }
        }
    }
}
