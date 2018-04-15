package net.appitiza.moderno.ui.activities.users

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_history.*
import kotlinx.android.synthetic.main.item_users_history.view.*
import kotlinx.android.synthetic.main.users_daily_layout.*
import kotlinx.android.synthetic.main.users_monthly_layout.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.UserHistoryAdapter
import net.appitiza.moderno.ui.model.CurrentCheckIndata
import net.appitiza.moderno.utils.PreferenceHelper
import java.text.SimpleDateFormat
import java.util.*


class UserHistoryActivity : BaseActivity() {
    private var isLoggedIn by PreferenceHelper(Constants.PREF_KEY_IS_USER_LOGGED_IN, false)
    private var displayName by PreferenceHelper(Constants.PREF_KEY_IS_USER_DISPLAY_NAME, "")
    private var useremail by PreferenceHelper(Constants.PREF_KEY_IS_USER_EMAIL, "")
    private var userpassword by PreferenceHelper(Constants.PREF_KEY_IS_USER_PASSWORD, "")
    private var usertype by PreferenceHelper(Constants.PREF_KEY_IS_USER_USER_TYPE, "")
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    private lateinit var mHistoryDisplay: ArrayList<CurrentCheckIndata>
    private lateinit var mHistoryDaily: ArrayList<CurrentCheckIndata>
    private lateinit var mHistoryMonthly: ArrayList<CurrentCheckIndata>
    private lateinit var adapterMonthly: UserHistoryAdapter
    val mSelectedCalender = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_history)
        initialize()
        setClick()
        loadDaily()
    }

    private fun initialize() {
        rv_history_list.layoutManager = LinearLayoutManager(this)
        mHistoryDisplay = arrayListOf()
        mHistoryDaily = arrayListOf()
        mHistoryMonthly = arrayListOf()
        adapterMonthly = UserHistoryAdapter(applicationContext, mHistoryDisplay)
        rv_history_list.adapter = adapterMonthly
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        ll_users_daily_root.visibility = View.GONE
        ll_users_monthly_root.visibility = View.GONE
        tv_useres_history_daily_date.text = convertDate(mSelectedCalender.timeInMillis,"dd MMM yyyy")

    }

    private fun setClick() {
        tv_user_history_daily.setOnClickListener { loadDaily() }
        tv_user_history_monthly.setOnClickListener { loadMonthly() }
        tv_useres_history_daily_date.setOnClickListener { loadCalendar() }
    }

    private fun loadCalendar() {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = android.app.DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    mSelectedCalender.set(year, monthOfYear, dayOfMonth)
                    tv_useres_history_daily_date.text = convertDate(mSelectedCalender.timeInMillis,"dd MMM yyyy")
                    loadDaily()
                }, mYear, mMonth, mDay)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000


        datePickerDialog.setTitle(null)
        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
    }

    private fun loadDaily() {
        ll_users_daily_root.visibility = View.VISIBLE
        tv_user_history_daily.setTextColor(ContextCompat.getColor(this, R.color.text_clicked))
        ll_users_monthly_root.visibility = View.GONE
        tv_user_history_monthly.setTextColor(ContextCompat.getColor(this, R.color.white))


        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.fetching_data))
        mProgress?.setCancelable(false)
        mProgress?.show()
        val mCalender = Calendar.getInstance()
        //mCalender.set(mCalender.gety, 3, 1)


        db.collection(Constants.COLLECTION_CHECKIN_HISTORY)
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()

                    if (fetchall_task.isSuccessful) {
                        var total_payment = 0
                        var total_hours: Long = 0
                        for (document in fetchall_task.result) {
                            Log.d(" data", document.id + " => " + document.data)
                            val mCheckInData = CurrentCheckIndata()
                            mCheckInData.documentid = document.id
                            mCheckInData.siteid = document.data[Constants.CHECKIN_SITE].toString()
                            mCheckInData.sitename = document.data[Constants.CHECKIN_SITENAME].toString()
                            if (!TextUtils.isEmpty(document.data[Constants.CHECKIN_CHECKIN].toString()) && !document.data[Constants.CHECKIN_CHECKIN].toString().equals("null")) {
                                mCheckInData.checkintime = getDate(document.data[Constants.CHECKIN_CHECKIN].toString()).time.toLong()
                            }
                            if (!TextUtils.isEmpty(document.data[Constants.CHECKIN_CHECKOUT].toString()) && !document.data[Constants.CHECKIN_CHECKOUT].toString().equals("null")) {
                                mCheckInData.checkouttime = getDate(document.data[Constants.CHECKIN_CHECKOUT].toString()).time.toLong()
                            }
                            mCheckInData.useremail = document.data[Constants.CHECKIN_USEREMAIL].toString()
                            mCheckInData.payment = document.data[Constants.CHECKIN_PAYMENT].toString()

                            if(mCheckInData.checkintime !!>= mSelectedCalender.timeInMillis && mCheckInData.checkintime !!<= (mSelectedCalender.timeInMillis + (24*60*60*1000))) {
                                if (!mCheckInData.payment.equals("null") && mCheckInData.payment.toString().equals("")) {
                                    val mPayment = Integer.parseInt(document.data[Constants.CHECKIN_PAYMENT].toString())
                                    total_payment += mPayment
                                }
                                if (mCheckInData.checkintime != 0L) {
                                    if (mCheckInData.checkouttime != 0L) {
                                        val mHours = getDate(document.data[Constants.CHECKIN_CHECKOUT].toString()).time - getDate(document.data[Constants.CHECKIN_CHECKIN].toString()).time
                                        total_hours += (mHours)
                                    }
                                }
                                mHistoryDaily.add(mCheckInData)
                            }

                        }
                        tv_useres_history_daily_payment.text = getString(R.string.rupees, total_payment)


                        if (total_hours > 0) {
                            total_hours /= (3600 * 1000)

                            if (total_hours > 1) {
                                tv_useres_history_daily_total_hours.text = getString(R.string.hours_symbl, total_hours)
                            } else if (total_hours < 1) {
                                total_hours *= 60
                                tv_useres_history_daily_total_hours.text = getString(R.string.minutes_symbl, total_hours)
                            } else {
                                tv_useres_history_daily_total_hours.text = getString(R.string.hr_symbl, total_hours)
                            }

                        }
                        else
                        {
                            tv_useres_history_daily_total_hours.text = getString(R.string.not_checked_out)

                        }
                    } else {
                        Toast.makeText(this@UserHistoryActivity, fetchall_task.exception.toString(),
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

        mHistoryMonthly.clear()
        mHistoryDisplay.clear()
        val mCalender1 = Calendar.getInstance()
        mCalender1.set(2018, 4, 12)
        val mCalender2 = Calendar.getInstance()
        mCalender2.set(2018, 6, 1)



        db.collection(Constants.COLLECTION_CHECKIN_HISTORY)
                .whereEqualTo(Constants.CHECKIN_USEREMAIL, useremail)
                /*  historyRef.whereGreaterThan(Constants.CHECKIN_CHECKIN, mCalender1.time.toString())*/
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()

                    if (fetchall_task.isSuccessful) {
                        var total_payment = 0
                        var total_hours: Long = 0
                        for (document in fetchall_task.result) {
                            Log.d(" data", document.id + " => " + document.getData())

                            val mCheckInData = CurrentCheckIndata()
                            mCheckInData.documentid = document.id
                            mCheckInData.siteid = document.data[Constants.CHECKIN_SITE].toString()
                            mCheckInData.sitename = document.data[Constants.CHECKIN_SITENAME].toString()
                            if (!TextUtils.isEmpty(document.data[Constants.CHECKIN_CHECKIN].toString()) && !document.data[Constants.CHECKIN_CHECKIN].toString().equals("null")) {
                                mCheckInData.checkintime = getDate(document.data[Constants.CHECKIN_CHECKIN].toString()).time.toLong()
                            }
                            if (!TextUtils.isEmpty(document.data[Constants.CHECKIN_CHECKOUT].toString()) && !document.data[Constants.CHECKIN_CHECKOUT].toString().equals("null")) {
                                mCheckInData.checkouttime = getDate(document.data[Constants.CHECKIN_CHECKOUT].toString()).time.toLong()
                            }
                            mCheckInData.useremail = document.data[Constants.CHECKIN_USEREMAIL].toString()
                            mCheckInData.payment = document.data[Constants.CHECKIN_PAYMENT].toString()
                            mHistoryMonthly.add(mCheckInData)

                            if (!document.data[Constants.CHECKIN_PAYMENT].toString().equals("null") && !document.data[Constants.CHECKIN_PAYMENT].toString().equals("")) {
                                val mPayment = Integer.parseInt(document.data[Constants.CHECKIN_PAYMENT].toString())
                                total_payment += mPayment
                            }
                            if (mCheckInData.checkintime != 0L) {
                                if (mCheckInData.checkouttime != 0L) {
                                    val mHours = getDate(document.data[Constants.CHECKIN_CHECKOUT].toString()).time - getDate(document.data[Constants.CHECKIN_CHECKIN].toString()).time
                                    total_hours += (mHours)
                                }
                            }


                        }
                        tv_useres_history_monthly_payment.text = getString(R.string.rupees, total_payment)
                        if (total_hours > 0) {
                            total_hours /= (3600 * 1000)

                            if (total_hours > 1) {
                                tv_useres_history_monthly_total_hours.text = getString(R.string.hours_symbl, total_hours)
                            } else if (total_hours < 1) {
                                total_hours *= 60
                                tv_useres_history_monthly_total_hours.text = getString(R.string.minutes_symbl, total_hours)
                            } else {
                                tv_useres_history_monthly_total_hours.text = getString(R.string.hr_symbl, total_hours)
                            }

                        }
                        mHistoryDisplay.addAll(mHistoryMonthly)
                        adapterMonthly.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@UserHistoryActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()
                        Log.e("With time", fetchall_task.exception.toString())
                    }
                }
    }

    private fun getDate(date: String): Date {
        val format = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val value: Date = format.parse(date)
        return value
    }
    private fun convertDate(milli : Long,dateFormat: String): String {
        val format = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        var  calendar = Calendar.getInstance()
        calendar.timeInMillis = milli
        val value = format.format(calendar.time)
        return value
    }
}
