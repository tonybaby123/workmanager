package net.appitiza.moderno.ui.activities.admin

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_site_reports.*
import kotlinx.android.synthetic.main.activity_user_report.*
import kotlinx.android.synthetic.main.users_daily_layout.*
import kotlinx.android.synthetic.main.users_monthly_layout.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.AdminSprSiteAdapter
import net.appitiza.moderno.ui.activities.adapter.UserCheckSiteAdapter
import net.appitiza.moderno.ui.activities.interfaces.UserSiteClick
import net.appitiza.moderno.ui.model.CurrentCheckIndata
import net.appitiza.moderno.ui.model.SiteListdata
import net.appitiza.moderno.utils.PreferenceHelper
import java.text.SimpleDateFormat
import java.util.*

class AdminSiteReportsActivity : BaseActivity(), UserSiteClick {


    private var isLoggedIn by PreferenceHelper(Constants.PREF_KEY_IS_USER_LOGGED_IN, false)
    private var displayName by PreferenceHelper(Constants.PREF_KEY_IS_USER_DISPLAY_NAME, "")
    private var useremail by PreferenceHelper(Constants.PREF_KEY_IS_USER_EMAIL, "")
    private var userpassword by PreferenceHelper(Constants.PREF_KEY_IS_USER_PASSWORD, "")
    private var usertype by PreferenceHelper(Constants.PREF_KEY_IS_USER_USER_TYPE, "")
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    private lateinit var mSiteList: ArrayList<SiteListdata>
    private lateinit var siteAdapter: AdminSprSiteAdapter
    private var selectedSite: SiteListdata = SiteListdata()
    private lateinit var mHistory: ArrayList<CurrentCheckIndata>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_site_reports)
        initializeFireBase()
        setClick()
        getSites()
    }

    private fun initializeFireBase() {

        mSiteList = arrayListOf()
        mHistory = arrayListOf()
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


    }
    private fun setClick() {

        spnr_admin_site_report.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                selectedSite = mSiteList[position]
                tv_admin_sitereport_completed.text = selectedSite.status.toString()

                loadSiteDetails()
            }

        }

    }

    private fun getSites() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.getting_site))
        mProgress?.setCancelable(false)
        mProgress?.show()

        db.collection(Constants.COLLECTION_SITE)
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
                            data.lat = document.data[Constants.SITE_LAT].toString().toDouble()
                            data.lon = document.data[Constants.SITE_LON].toString().toDouble()
                            data.status = document.data[Constants.SITE_STATUS].toString()
                            mSiteList.add(data)

                        }
                        siteAdapter = AdminSprSiteAdapter(this, mSiteList, this)
                        spnr_admin_site_report.adapter = siteAdapter
                        mProgress?.dismiss()

                    } else {
                        Toast.makeText(this@AdminSiteReportsActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()

                    }
                }


    }

    private fun loadSiteDetails() {


        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.fetching_data))
        mProgress?.setCancelable(false)
        mProgress?.show()
        mHistory.clear()
        db.collection(Constants.COLLECTION_CHECKIN_HISTORY)
                .whereEqualTo(Constants.CHECKIN_SITE,selectedSite.siteid.toString())
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()

                    if (fetchall_task.isSuccessful) {
                        var total_payment = 0
                        var total_hours : Long = 0
                        for (document in fetchall_task.result) {
                            Log.d(" data", document.id + " => " + document.getData())
                            val mCheckInData = CurrentCheckIndata()
                            mCheckInData.documentid = document.id
                            mCheckInData.siteid = document.data[Constants.CHECKIN_SITE].toString()
                            mCheckInData.sitename = document.data[Constants.CHECKIN_SITENAME].toString()

                            if (!TextUtils.isEmpty(document.data[Constants.CHECKIN_CHECKIN].toString()) && !document.data[Constants.CHECKIN_CHECKIN].toString().equals("null")) {
                                mCheckInData.checkintime = getDate(document.data[Constants.CHECKIN_CHECKIN].toString()).time.toLong()   }
                            if (!TextUtils.isEmpty(document.data[Constants.CHECKIN_CHECKOUT].toString()) && !document.data[Constants.CHECKIN_CHECKOUT].toString().equals("null")) {
                                mCheckInData.checkouttime = getDate(document.data[Constants.CHECKIN_CHECKOUT].toString()).time.toLong()
                            }

                            mCheckInData.useremail = document.data[Constants.CHECKIN_USEREMAIL].toString()
                            mCheckInData.payment = document.data[Constants.CHECKIN_PAYMENT].toString()
                            if (!document.data[Constants.CHECKIN_PAYMENT].toString().equals("null") && !document.data[Constants.CHECKIN_PAYMENT].toString().equals("")) {
                                val mPayment = Integer.parseInt(document.data[Constants.CHECKIN_PAYMENT].toString())
                                total_payment += mPayment
                            }
                            val mHours = getDate(document.data[Constants.CHECKIN_CHECKOUT].toString()).time - getDate(document.data[Constants.CHECKIN_CHECKIN].toString()).time
                            total_hours += (mHours)

                            mHistory.add(mCheckInData)

                        }
                        tv_admin_sitereport_payment.text = getString(R.string.rupees,  total_payment)
                        if(total_hours > 0) {
                            total_hours /= (3600 * 1000)
                            tv_admin_sitereport_total_hours.text = getString(R.string.hours_symbl, total_hours)
                        }

                    } else {
                        Toast.makeText(this@AdminSiteReportsActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()
                        Log.e("With time", fetchall_task.exception.toString())
                    }
                }
    }
    private fun getDate(date: String): Date {
        val format = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.ENGLISH)
        val value: Date = format.parse(date)
        return value
    }
    override fun onSiteClick(data: SiteListdata) {


    }
}
