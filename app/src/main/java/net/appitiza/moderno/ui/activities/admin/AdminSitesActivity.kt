package net.appitiza.moderno.ui.activities.admin

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_admin_sites.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.AdminSiteAdapter
import net.appitiza.moderno.ui.activities.interfaces.AdminSiteClick
import net.appitiza.moderno.ui.model.SiteListdata
import org.jetbrains.annotations.Nullable


class AdminSitesActivity : BaseActivity(), AdminSiteClick {
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    private lateinit var mSiteList: ArrayList<SiteListdata>
    private lateinit var adapter: AdminSiteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_sites)
        initializeFireBase()
        setClick()
        getAll()
    }

    private fun initializeFireBase() {
        rv_admin_site_all.layoutManager = LinearLayoutManager(this)

        mSiteList = arrayListOf()
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun setClick() {
        fab_admin_add_site.setOnClickListener {

            val intent = Intent(this@AdminSitesActivity, CreateSiteActivity::class.java)
            startActivity(intent)


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

    private fun getAll() {
        mProgress?.setTitle(getString(R.string.app_name))
        mProgress?.setMessage(getString(R.string.getting_site))
        mProgress?.setCancelable(false)
        mProgress?.show()
        tv_admin_site_all.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_all.setTypeface(tv_admin_site_all.getTypeface(), Typeface.BOLD)
        tv_admin_site_completed.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_completed.setTypeface(tv_admin_site_completed.getTypeface(), Typeface.NORMAL)
        tv_admin_site_undergoing.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_undergoing.setTypeface(tv_admin_site_undergoing.getTypeface(), Typeface.NORMAL)

        db.collection(Constants.COLLECTION_SITE)
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
        tv_admin_site_all.setTypeface(tv_admin_site_all.getTypeface(), Typeface.NORMAL)
        tv_admin_site_completed.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_completed.setTypeface(tv_admin_site_completed.getTypeface(), Typeface.NORMAL)
        tv_admin_site_undergoing.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_undergoing.setTypeface(tv_admin_site_undergoing.getTypeface(), Typeface.BOLD)
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
        tv_admin_site_all.setTypeface(tv_admin_site_all.getTypeface(), Typeface.NORMAL)
        tv_admin_site_completed.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_completed.setTypeface(tv_admin_site_completed.getTypeface(), Typeface.BOLD)
        tv_admin_site_undergoing.setTextColor(ContextCompat.getColor(this, R.color.white))
        tv_admin_site_undergoing.setTypeface(tv_admin_site_undergoing.getTypeface(), Typeface.NORMAL)
        db.collection(Constants.COLLECTION_SITE)
                .whereEqualTo(Constants.SITE_STATUS, "complete")
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


}
