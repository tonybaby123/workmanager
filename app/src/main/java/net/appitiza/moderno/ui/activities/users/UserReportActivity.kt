package net.appitiza.moderno.ui.activities.users

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_admin_sites.*
import kotlinx.android.synthetic.main.activity_user_report.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.AdminSiteAdapter
import net.appitiza.moderno.ui.activities.adapter.UserCheckSiteAdapter
import net.appitiza.moderno.ui.activities.admin.AdminEditSiteActivity
import net.appitiza.moderno.ui.activities.interfaces.UserSiteClick
import net.appitiza.moderno.ui.model.SiteListdata

class UserReportActivity : BaseActivity(),UserSiteClick {

    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    private lateinit var mSiteList: ArrayList<SiteListdata>
    private lateinit var ciadapter: UserCheckSiteAdapter
    private lateinit var coadapter: UserCheckSiteAdapter
    private var listener : ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_report)
        initializeFireBase()
        getSites()
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

        /*db.collection(Constants.COLLECTION_SITE)
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
                        ciadapter = UserCheckInSiteAdapter(this,mSiteList)
                        coadapter = UserCheckInSiteAdapter(this,mSiteList)
                        spnr_users_check_in_site.setAdapter(ciadapter)
                        spnr_users_check_out_site.setAdapter(coadapter)

                    } else {
                        Toast.makeText(this@UserReportActivity, fetchall_task.exception.toString(),
                                Toast.LENGTH_SHORT).show()

                    }
                }*/

        listener =  db.collection(Constants.COLLECTION_SITE)
                .whereEqualTo(Constants.SITE_STATUS, "undergoing")
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        return@EventListener
                    }
                    for (document in snapshots) {
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
                    ciadapter = UserCheckSiteAdapter(this,mSiteList)
                    coadapter = UserCheckSiteAdapter(this,mSiteList)
                    spnr_users_check_in_site.setAdapter(ciadapter)
                    spnr_users_check_out_site.setAdapter(coadapter)


                })



    }
    override fun onSiteClick(data: SiteListdata) {
        val intent = Intent(this@UserReportActivity, AdminEditSiteActivity::class.java)
        intent.putExtra("site_data", data)
        startActivity(intent)
    }
    override fun onDestroy() {
        listener!!.remove()
        super.onDestroy()
    }
}
