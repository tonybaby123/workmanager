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
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_admin_sites.*
import kotlinx.android.synthetic.main.activity_create_site.*
import net.appitiza.moderno.BuildConfig
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.SiteTypesAdapter
import net.appitiza.moderno.ui.activities.services.LocationUpdatesService
import net.appitiza.moderno.ui.activities.utils.Utils
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.set

class CreateSiteActivity : BaseActivity() {

    private var type_list = ArrayList<String>()
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    private lateinit var mTypeAdapter: SiteTypesAdapter

    var createClicked: Boolean = false
    val LOCATION_SETTINGS = 2
    private val TAG = "LOCATION"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private var mGpsLocation: Location? = null
    // Monitors the state of the connection to the service.
    private lateinit var myReceiver: BroadcastReceiver

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
        setContentView(R.layout.activity_create_site)
        initializeFireBase()
        setSiteType()
        setClick()
    }

    override fun onResume() {
        super.onResume()
        myReceiver = MyReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                IntentFilter(LocationUpdatesService.ACTION_BROADCAST))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!checkPermissions()) {
                requestPermissions()
            } else {
                displayLocationSettingsRequest()
            }

        } else {
            displayLocationSettingsRequest()
        }
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
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

    }

    fun setSiteType() {

        type_list.add("Interior")
        type_list.add("Exterior")
        mTypeAdapter = SiteTypesAdapter(this, type_list)
        spnr_admin_create_type.adapter = mTypeAdapter
    }

    private fun setClick() {
        tv_admin_create_create.setOnClickListener {
            if (validation()) {
                createSite()
            }

        }
    }

    private fun createSite() {
        if (mGpsLocation != null) {
            if (mGpsLocation!!.latitude != 0.0 && mGpsLocation!!.longitude != 0.0) {
                createClicked = false
                addToFirestore()
            } else {
                Toast.makeText(applicationContext, "Location not recieved", Toast.LENGTH_SHORT).show()
            }
        } else {
            createClicked = true
        }
    }

    fun addToFirestore() {

        val map = createMap()
        db.collection(Constants.COLLECTION_SITE)
                .document()
                .set(map, SetOptions.merge())
                .addOnCompleteListener { add_task ->
                    if (add_task.isSuccessful) {
                        mProgress!!.dismiss()
                        Toast.makeText(this@CreateSiteActivity, "Added",
                                Toast.LENGTH_SHORT).show()
                        finish()

                    } else {
                        mProgress!!.hide()
                        Toast.makeText(this@CreateSiteActivity, add_task.exception?.message.toString(),
                                Toast.LENGTH_SHORT).show()
                    }
                }


    }

    private fun validation(): Boolean {
        if (TextUtils.isEmpty(et_admin_create_site_name.text.toString())) {
            showValidationWarning(getString(R.string.site_name_missing))
            return false
        } else if (TextUtils.isEmpty(et_admin_create_start_date.text.toString())) {
            showValidationWarning(getString(R.string.site_date_missing))
            return false
        } else if (TextUtils.isEmpty(et_admin_create_project_cost.text.toString())) {
            showValidationWarning(getString(R.string.site_cost_missing))
            return false
        } else if (TextUtils.isEmpty(et_admin_create_contact_number.text.toString())) {
            showValidationWarning(getString(R.string.site_contact_number_missing))
            return false
        } else if (TextUtils.isEmpty(et_admin_create_contact_person.text.toString())) {
            showValidationWarning(getString(R.string.site_name_missing))
            return false
        } else {
            return true
        }
    }

    private fun createMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map[Constants.SITE_NAME] = et_admin_create_site_name.text.toString()
        map[Constants.SITE_TYPE] = spnr_admin_create_type.selectedItem.toString()
        map[Constants.SITE_DATE] = et_admin_create_start_date.text.toString()
        map[Constants.SITE_COST] = et_admin_create_project_cost.text.toString()
        map[Constants.SITE_CONTACT] = et_admin_create_contact_number.text.toString()
        map[Constants.SITE_PERSON] = et_admin_create_contact_person.text.toString()
        map[Constants.SITE_LAT] = mGpsLocation!!.latitude
        map[Constants.SITE_LON] = mGpsLocation!!.longitude
        map[Constants.SITE_STATUS] = "undergoing"
        return map
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
                val intent = Intent(this@CreateSiteActivity, CreateSiteActivity::class.java)
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

    private fun displayLocationSettingsRequest() {
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
                if (validation()) {
                    createSite()
                }
            }
        }
    }
}
