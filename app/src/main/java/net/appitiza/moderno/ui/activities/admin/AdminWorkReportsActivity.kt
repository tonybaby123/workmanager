package net.appitiza.moderno.ui.activities.admin

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_work_reports.*
import kotlinx.android.synthetic.main.activity_user_history.*
import kotlinx.android.synthetic.main.admin_daily_layout.*
import kotlinx.android.synthetic.main.admin_monthly_layout.*
import kotlinx.android.synthetic.main.users_daily_layout.*
import kotlinx.android.synthetic.main.users_monthly_layout.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.AdminHistoryAdapter
import net.appitiza.moderno.ui.activities.adapter.AdminSpnrUserAdapter
import net.appitiza.moderno.ui.activities.adapter.UserHistoryAdapter
import net.appitiza.moderno.ui.activities.interfaces.UserClick
import net.appitiza.moderno.ui.model.CurrentCheckIndata
import net.appitiza.moderno.ui.model.UserListdata
import net.appitiza.moderno.utils.PreferenceHelper
import java.text.SimpleDateFormat
import java.util.*

class AdminWorkReportsActivity : BaseActivity(), UserClick {
    override fun onUserClick(data: UserListdata) {

    }

    private var isLoggedIn by PreferenceHelper(Constants.PREF_KEY_IS_USER_LOGGED_IN, false)
    private var displayName by PreferenceHelper(Constants.PREF_KEY_IS_USER_DISPLAY_NAME, "")
    private var useremail by PreferenceHelper(Constants.PREF_KEY_IS_USER_EMAIL, "")
    private var userpassword by PreferenceHelper(Constants.PREF_KEY_IS_USER_PASSWORD, "")
    private var usertype by PreferenceHelper(Constants.PREF_KEY_IS_USER_USER_TYPE, "")
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    private lateinit var mUserList: ArrayList<UserListdata>
    private lateinit var userAdapter: AdminSpnrUserAdapter
    private var user: UserListdata? = null

    private lateinit var mHistory: ArrayList<CurrentCheckIndata>
    private lateinit var adapter: AdminHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_work_reports)
        initializeFireBase()
        getUser()
        setClick()
    }

    private fun initializeFireBase() {
        mUserList = arrayListOf()
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun setClick() {

        spnr_admin_work_report_user.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                user = mUserList[position]
            }

        }
        tv_admin_history_daily.setOnClickListener { loadDaily() }
        tv_admin_history_monthly.setOnClickListener { loadMonthly() }
    }

    private fun getUser() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.getting_site))
        mProgress?.setCancelable(false)
        mProgress?.show()

        db.collection(Constants.COLLECTION_USER)
                .whereEqualTo(Constants.USER_TYPE, "user")
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()
                    if (fetchall_task.isSuccessful) {
                        val data = UserListdata()
                        data.emailId = "all"
                        data.username = "Send To All"
                        mUserList.add(data)
                        for (document in fetchall_task.result) {
                            // Log.d(FragmentActivity.TAG, document.id + " => " + document.getData())
                            val data = UserListdata()
                            data.emailId = document.data[Constants.USER_EMAIL].toString()
                            data.username = document.data[Constants.USER_DISPLAY_NAME].toString()
                            mUserList.add(data)

                        }

                        userAdapter = AdminSpnrUserAdapter(this, mUserList, this)
                        spnr_admin_work_report_user.setAdapter(userAdapter)
                        mProgress?.dismiss()

                    } else {
                        Toast.makeText(this@AdminWorkReportsActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()

                    }
                }


    }

    private fun loadDaily() {
        ll_admin_daily_root.visibility = View.VISIBLE
        tv_admin_history_daily.setTextColor(ContextCompat.getColor(this, R.color.text_clicked))
        ll_admin_monthly_root.visibility = View.GONE
        tv_user_history_monthly.setTextColor(ContextCompat.getColor(this, R.color.white))


        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.fetching_data))
        mProgress?.setCancelable(false)
        mProgress?.show()
        mHistory.clear()



        db.collection(Constants.COLLECTION_CHECKIN_HISTORY)
                .whereEqualTo(Constants.CHECKIN_USEREMAIL,user?.emailId.toString())
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()

                    if (fetchall_task.isSuccessful) {
                        var total = 0
                        for (document in fetchall_task.result) {
                            Log.d(" data", document.id + " => " + document.getData())
                            val mCheckInData = CurrentCheckIndata()
                            mCheckInData.documentid = document.id
                            mCheckInData.siteid = document.data[Constants.CHECKIN_SITE].toString()
                            mCheckInData.sitename = document.data[Constants.CHECKIN_SITENAME].toString()
                            mCheckInData.checkintime = getDate(document.data[Constants.CHECKIN_CHECKIN].toString()).time.toString()
                            mCheckInData.checkouttime = getDate(document.data[Constants.CHECKIN_CHECKOUT].toString()).time.toString()
                            mCheckInData.useremail = document.data[Constants.CHECKIN_USEREMAIL].toString()
                            mCheckInData.payment = document.data[Constants.CHECKIN_PAYMENT].toString()
                            if(!document.data[Constants.CHECKIN_PAYMENT].toString().equals("null") && !document.data[Constants.CHECKIN_PAYMENT].toString().equals("")) {
                                val mPayment = Integer.parseInt(document.data[Constants.CHECKIN_PAYMENT].toString())
                                total += mPayment
                            }
                            mHistory.add(mCheckInData)

                        }
                        tv_admin_history_daily_payment.text = getString(R.string.rupees,  0)

                    } else {
                        Toast.makeText(this@AdminWorkReportsActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()
                        Log.e("With time", fetchall_task.exception.toString())
                    }
                }
    }

    private fun loadMonthly() {


        ll_users_daily_root.visibility = View.GONE
        tv_user_history_daily.setTextColor(ContextCompat.getColor(this, R.color.white))
        ll_users_monthly_root.visibility = View.VISIBLE
        tv_user_history_monthly.setTextColor(ContextCompat.getColor(this, R.color.text_clicked))

        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.fetching_data))
        mProgress?.setCancelable(false)
        mProgress?.show()

        mHistory.clear()
        val mCalender1 = Calendar.getInstance()
        mCalender1.set(2018, 3, 1)
        val mCalender2 = Calendar.getInstance()
        mCalender2.set(2018, 6, 1)



        db.collection(Constants.COLLECTION_CHECKIN_HISTORY)
                .whereEqualTo(Constants.CHECKIN_USEREMAIL,user?.emailId.toString())
                /* .whereGreaterThan(Constants.CHECKIN_CHECKIN,"Thu Apr 10 00:01:01 GMT+05:30 2018")*/
                /* .whereGreaterThan(Constants.CHECKIN_CHECKIN,mCalender1.timeInMillis.toString())*/
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
                            mCheckInData.checkintime = getDate(document.data[Constants.CHECKIN_CHECKIN].toString()).time.toString()
                            mCheckInData.checkouttime = getDate(document.data[Constants.CHECKIN_CHECKOUT].toString()).time.toString()
                            mCheckInData.useremail = document.data[Constants.CHECKIN_USEREMAIL].toString()
                            mCheckInData.payment = document.data[Constants.CHECKIN_PAYMENT].toString()
                            mHistory.add(mCheckInData)

                            if(!document.data[Constants.CHECKIN_PAYMENT].toString().equals("null") && !document.data[Constants.CHECKIN_PAYMENT].toString().equals("")) {
                                val mPayment = Integer.parseInt(document.data[Constants.CHECKIN_PAYMENT].toString())
                                total_payment += mPayment
                            }
                            val mHours = getDate(document.data[Constants.CHECKIN_CHECKOUT].toString()).time - getDate(document.data[Constants.CHECKIN_CHECKIN].toString()).time
                            total_hours += (mHours)


                        }
                        tv_admin_history_monthly_payment.text = getString(R.string.rupees,  total_payment)
                        if(total_hours > 0) {
                            total_hours /= (3600 * 1000)
                            tv_admin_history_monthly_total_hours.text = getString(R.string.hours_symbl, total_hours)
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@AdminWorkReportsActivity, fetchall_task.exception.toString(),
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
}
