package net.appitiza.moderno.ui.activities.users

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_user_report.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.UserCheckSiteAdapter
import net.appitiza.moderno.ui.activities.admin.AdminEditSiteActivity
import net.appitiza.moderno.ui.activities.interfaces.UserSiteClick
import net.appitiza.moderno.ui.model.CurrentCheckIndata
import net.appitiza.moderno.ui.model.SiteListdata
import net.appitiza.moderno.utils.PreferenceHelper
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class UserReportActivity : BaseActivity(), UserSiteClick {
    private var isLoggedIn by PreferenceHelper(Constants.PREF_KEY_IS_USER_LOGGED_IN, false)
    private var displayName by PreferenceHelper(Constants.PREF_KEY_IS_USER_DISPLAY_NAME, "")
    private var useremail by PreferenceHelper(Constants.PREF_KEY_IS_USER_EMAIL, "")
    private var userpassword by PreferenceHelper(Constants.PREF_KEY_IS_USER_PASSWORD, "")
    private var usertype by PreferenceHelper(Constants.PREF_KEY_IS_USER_USER_TYPE, "")
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    private lateinit var mSiteList: ArrayList<SiteListdata>
    private lateinit var ciadapter: UserCheckSiteAdapter
    private lateinit var coadapter: UserCheckSiteAdapter
    private val mCheckInData: CurrentCheckIndata = CurrentCheckIndata()
    private var checkinSite: SiteListdata = SiteListdata()
    private var checkoutSite: SiteListdata = SiteListdata()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_report)
        initializeFireBase()
        getSites()
        getCheckInInfo()
        setClick()
    }

    private fun setClick() {
        tv_user_report_checkin.setOnClickListener {
            if (TextUtils.isEmpty(mCheckInData.siteid)) {
                insertHistory()
            } else {
                mProgress!!.hide()
                Toast.makeText(this@UserReportActivity, "already checked in " + mCheckInData.checkintime + "  \nPlease check out",
                        Toast.LENGTH_SHORT).show()
            }

        }
        tv_user_report_checkout.setOnClickListener {

            if (!TextUtils.isEmpty(mCheckInData.siteid)) {
                if (mCheckInData.siteid.equals(checkoutSite.siteid)) {
                    updateHistory()
                } else {
                    Toast.makeText(this@UserReportActivity, "You have checked in at " + mCheckInData.sitename + " \nPlease check out from same site",
                            Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this@UserReportActivity, "You need to check in first",
                        Toast.LENGTH_SHORT).show()
            }

        }
        spnr_users_check_out_site.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                checkoutSite = mSiteList[position]
            }

        }
        spnr_users_check_in_site.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                checkinSite = mSiteList[position]
            }

        }
    }

    private fun checkInData() {

        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.checking_in_user))
        mProgress?.setCancelable(false)
        mProgress?.show()
        val map = HashMap<String, Any>()
        map[Constants.CHECKIN_SITE] = checkinSite.siteid.toString()
        map[Constants.CHECKIN_SITENAME] = checkinSite.sitename.toString()
        map[Constants.CHECKIN_CHECKIN] = FieldValue.serverTimestamp()
        map[Constants.CHECKIN_USEREMAIL] = useremail


        db.collection(Constants.COLLECTION_CHECKIN_DATA)
                .document(mCheckInData.documentid.toString())
                .set(map, SetOptions.merge())
                .addOnCompleteListener { checkin_task ->
                    if (checkin_task.isSuccessful) {
                        mProgress!!.dismiss()
                        Toast.makeText(this@UserReportActivity, "CHECKED IN",
                                Toast.LENGTH_SHORT).show()
                        finish()

                    } else {
                        mProgress!!.hide()
                        Toast.makeText(this@UserReportActivity, checkin_task.exception?.message.toString(),
                                Toast.LENGTH_SHORT).show()
                    }
                }


    }


    private fun checkinClear() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.checking_out_user))
        mProgress?.setCancelable(false)
        mProgress?.show()
        db.collection(Constants.COLLECTION_CHECKIN_DATA)
                .document(mCheckInData.documentid.toString())
                .delete()
                .addOnCompleteListener { clear_task ->
                    if (clear_task.isSuccessful) {
                        mProgress!!.dismiss()
                        Toast.makeText(this@UserReportActivity, "CHECKED OUT",
                                Toast.LENGTH_SHORT).show()
                        finish()

                    } else {
                        mProgress!!.hide()
                        Toast.makeText(this@UserReportActivity, clear_task.exception?.message.toString(),
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun insertHistory() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.syn))
        mProgress?.setCancelable(false)
        mProgress?.show()
        val map = HashMap<String, Any>()
        map[Constants.CHECKIN_SITE] = checkinSite.siteid.toString()
        map[Constants.CHECKIN_SITENAME] = checkinSite.sitename.toString()
        map[Constants.CHECKIN_CHECKIN] = FieldValue.serverTimestamp()
        map[Constants.CHECKIN_USEREMAIL] = useremail


        db.collection(Constants.COLLECTION_CHECKIN_HISTORY)
                .add(map)
                .addOnSuccessListener { documentReference ->
                    mCheckInData.documentid = documentReference.id
                    mCheckInData.siteid = checkinSite.siteid.toString()
                    mCheckInData.sitename = checkinSite.sitename.toString()
                    mCheckInData.useremail = useremail
                    mProgress!!.dismiss()
                    checkInData()
                    Toast.makeText(this@UserReportActivity, "Sync Done",
                            Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    mProgress!!.dismiss()
                    Toast.makeText(this@UserReportActivity, "Sync failed",
                            Toast.LENGTH_SHORT).show()
                }
    }


    private fun updateHistory() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.syn))
        mProgress?.setCancelable(false)
        mProgress?.show()
        val map = HashMap<String, Any>()
        map[Constants.CHECKIN_CHECKOUT] = FieldValue.serverTimestamp()
        db.collection(Constants.COLLECTION_CHECKIN_HISTORY)
                .document(mCheckInData.documentid.toString())
                .set(map, SetOptions.merge())
                .addOnCompleteListener { sync_task ->
                    if (sync_task.isSuccessful) {
                        mProgress!!.dismiss()
                        Toast.makeText(this@UserReportActivity, "Sync Done",
                                Toast.LENGTH_SHORT).show()
                        checkinClear()

                    } else {
                        mProgress!!.hide()
                        Toast.makeText(this@UserReportActivity, sync_task.exception?.message.toString(),
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }


    fun getDateFromString(datetoSaved: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        try {
            return format.parse(datetoSaved)
        } catch (e: ParseException) {
            return null
        }

    }

    private fun getCheckInInfo() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.get_checkin_info))
        mProgress?.setCancelable(false)
        mProgress?.show()
        db.collection(Constants.COLLECTION_CHECKIN_DATA)
                .whereEqualTo(Constants.CHECKIN_USEREMAIL, useremail)
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()
                    if (fetchall_task.isSuccessful) {
                        for (document in fetchall_task.getResult()) {

                            mCheckInData.documentid = document.getId()
                            mCheckInData.siteid = document.getData()[Constants.CHECKIN_SITE].toString()
                            mCheckInData.sitename = document.getData()[Constants.CHECKIN_SITENAME].toString()
                            mCheckInData.checkintime = document.getData()[Constants.CHECKIN_CHECKIN].toString()
                            mCheckInData.checkouttime = document.getData()[Constants.CHECKIN_CHECKOUT].toString()
                            mCheckInData.useremail = document.getData()[Constants.CHECKIN_USEREMAIL].toString()
                            tv_user_report_checkin_at.text = document.getData()[Constants.CHECKIN_SITENAME].toString()
                        }

                    }
                }


    }

    private fun initializeFireBase() {

        mSiteList = arrayListOf()
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun getSites() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.getting_site))
        mProgress?.setCancelable(false)
        mProgress?.show()

        db.collection(Constants.COLLECTION_SITE)
                .whereEqualTo(Constants.SITE_STATUS, "undergoing")
                .get()
                .addOnCompleteListener { fetchall_task ->
                    mProgress?.dismiss()
                    if (fetchall_task.isSuccessful) {
                        for (document in fetchall_task.getResult()) {
                            // Log.d(FragmentActivity.TAG, document.getId() + " => " + document.getData())
                            val data: SiteListdata = SiteListdata()
                            data.siteid = document.getId()
                            data.sitename = document.getData()[Constants.SITE_NAME].toString()
                            data.type = document.getData()[Constants.SITE_TYPE].toString()
                            data.date = document.getData()[Constants.SITE_DATE].toString()
                            data.cost = document.getData()[Constants.SITE_COST].toString()
                            data.contact = document.getData()[Constants.SITE_CONTACT].toString()
                            data.person = document.getData()[Constants.SITE_PERSON].toString()
                            data.status = document.getData()[Constants.SITE_STATUS].toString()
                            mSiteList.add(data)

                        }
                        ciadapter = UserCheckSiteAdapter(this, mSiteList, this)
                        coadapter = UserCheckSiteAdapter(this, mSiteList, this)
                        spnr_users_check_in_site.setAdapter(ciadapter)
                        spnr_users_check_out_site.setAdapter(coadapter)
                        mProgress?.dismiss()

                    } else {
                        Toast.makeText(this@UserReportActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()

                    }
                }


    }

    override fun onSiteClick(data: SiteListdata) {
        val intent = Intent(this@UserReportActivity, AdminEditSiteActivity::class.java)
        intent.putExtra("site_data", data)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
