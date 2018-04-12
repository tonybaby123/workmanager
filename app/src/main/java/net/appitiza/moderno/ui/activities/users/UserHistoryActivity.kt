package net.appitiza.moderno.ui.activities.users

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_history.*
import kotlinx.android.synthetic.main.users_daily_layout.*
import kotlinx.android.synthetic.main.users_monthly_layout.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.UserHistoryAdapter
import net.appitiza.moderno.ui.model.CurrentCheckIndata
import net.appitiza.moderno.utils.PreferenceHelper
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
    private lateinit var mHistory: ArrayList<CurrentCheckIndata>
    private lateinit var adapter: UserHistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_history)
        initialize()
        setClick()
        loadDaily()
    }

    private fun initialize() {
        rv_history_list.layoutManager = LinearLayoutManager(this)
        mHistory = arrayListOf()
        adapter = UserHistoryAdapter(mHistory)
        rv_history_list.adapter = adapter
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        ll_users_daily_root.visibility = View.GONE
        ll_users_monthly_root.visibility = View.GONE
    }

    private fun setClick() {
        tv_user_history_daily.setOnClickListener { loadDaily() }
        tv_user_history_monthly.setOnClickListener { loadMonthly() }
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

        val mCalender1 = Calendar.getInstance()
        mCalender1.set(20018, 3, 1)
        val mCalender2 = Calendar.getInstance()
        mCalender2.set(20018, 6, 1)



        db.collection(Constants.COLLECTION_CHECKIN_HISTORY)
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()

                    if (fetchall_task.isSuccessful) {
                        for (document in fetchall_task.getResult()) {
                            Log.d(" data", document.getId() + " => " + document.getData())
                            val mCheckInData = CurrentCheckIndata()
                            mCheckInData.documentid = document.getId()
                            mCheckInData.siteid = document.getData()[Constants.CHECKIN_SITE].toString()
                            mCheckInData.sitename = document.getData()[Constants.CHECKIN_SITENAME].toString()
                            mCheckInData.checkintime = document.getData()[Constants.CHECKIN_CHECKIN].toString()
                            mCheckInData.checkouttime = document.getData()[Constants.CHECKIN_CHECKOUT].toString()
                            mCheckInData.useremail = document.getData()[Constants.CHECKIN_USEREMAIL].toString()
                            mCheckInData.payment = document.getData()[Constants.CHECKIN_PAYMENT].toString()
                            mHistory.add(mCheckInData)

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

        val mCalender1 = Calendar.getInstance()
        mCalender1.set(20018, 3, 1)
        val mCalender2 = Calendar.getInstance()
        mCalender2.set(20018, 6, 1)



        db.collection(Constants.COLLECTION_CHECKIN_HISTORY)
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()

                    if (fetchall_task.isSuccessful) {
                        for (document in fetchall_task.getResult()) {
                            Log.d(" data", document.getId() + " => " + document.getData())
                            val mCheckInData = CurrentCheckIndata()
                            mCheckInData.documentid = document.getId()
                            mCheckInData.siteid = document.getData()[Constants.CHECKIN_SITE].toString()
                            mCheckInData.sitename = document.getData()[Constants.CHECKIN_SITENAME].toString()
                            mCheckInData.checkintime = document.getData()[Constants.CHECKIN_CHECKIN].toString()
                            mCheckInData.checkouttime = document.getData()[Constants.CHECKIN_CHECKOUT].toString()
                            mCheckInData.useremail = document.getData()[Constants.CHECKIN_USEREMAIL].toString()
                            mCheckInData.payment = document.getData()[Constants.CHECKIN_PAYMENT].toString()
                            mHistory.add(mCheckInData)

                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@UserHistoryActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()
                        Log.e("With time", fetchall_task.exception.toString())
                    }
                }
    }
}
