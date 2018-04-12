package net.appitiza.moderno.ui.activities.admin

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_admin_edit_site.*
import kotlinx.android.synthetic.main.activity_create_site.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import net.appitiza.moderno.ui.activities.adapter.SiteStatussAdapter
import net.appitiza.moderno.ui.activities.adapter.SiteTypesAdapter
import net.appitiza.moderno.ui.model.SiteListdata
import java.util.HashMap

class AdminEditSiteActivity : BaseActivity() {

    private var type_list = ArrayList<String>()
    private var status_list = ArrayList<String>()
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    private lateinit var mTypeAdapter : SiteTypesAdapter
    private lateinit var mStatusAdapter : SiteStatussAdapter
    private var data: SiteListdata = SiteListdata()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_site)
        initializeFireBase()
        setSiteType()
        setStatusType()
        data
        setClick()
    }


    private fun initializeFireBase() {
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }
    fun getData()
    {
        data = intent?.getSerializableExtra("site_data") as SiteListdata

        et_admin_edit_site_name.setText(data.sitename.toString())
        if(data.type.equals("Interior"))
        {
            spnr_admin_edit_type.setSelection(0)
        }
        else
        {
            spnr_admin_edit_type.setSelection(1)
        }
        et_admin_edit_start_date.setText(data.date.toString())
        et_admin_edit_project_cost.setText(data.cost.toString())
        et_admin_edit_contact_number.setText(data.contact.toString())
        et_admin_edit_contact_person.setText(data.person.toString())
        if(data.status.equals("undergoing"))
        {
            spnr_admin_edit_status.setSelection(0)
        }
        else
        {
            spnr_admin_edit_status.setSelection(1)
        }
    }
    fun setSiteType()
    {

        type_list.add("Interior")
        type_list.add("Exterior")
        mTypeAdapter = SiteTypesAdapter(this,type_list)
        spnr_admin_edit_type.adapter = mTypeAdapter
        //spnr_admin_edit_type.setAdapter(mTypeAdapter)

    }
    fun setStatusType()
    {
        status_list.add("undergoing")
        status_list.add("complete")
        mStatusAdapter = SiteStatussAdapter(this,status_list)
        spnr_admin_edit_status.adapter = mStatusAdapter
        //spnr_admin_edit_status.setAdapter(mTypeAdapter)
    }
    private fun setClick() {
        tv_admin_edit_edit.setOnClickListener {
            if (validation())
            {
                val map = createMap()
                db.collection(Constants.COLLECTION_SITE)
                        .document(data.siteid.toString())
                        .set(map, SetOptions.merge())
                        .addOnCompleteListener { add_task ->
                            if (add_task.isSuccessful) {
                                mProgress!!.dismiss()
                                Toast.makeText(this@AdminEditSiteActivity, "Edited",
                                        Toast.LENGTH_SHORT).show()
                                finish()

                            } else {
                                mProgress!!.hide()
                                Toast.makeText(this@AdminEditSiteActivity, add_task.exception?.message.toString(),
                                        Toast.LENGTH_SHORT).show()
                            }
                        }


            }

        }
    }
    private fun validation(): Boolean {
        if (TextUtils.isEmpty(et_admin_edit_site_name.text.toString())) {
            showValidationWarning(getString(R.string.site_name_missing))
            return false
        }else   if (TextUtils.isEmpty(et_admin_edit_start_date.text.toString())) {
            showValidationWarning(getString(R.string.site_date_missing))
            return false
        } else  if (TextUtils.isEmpty(et_admin_edit_project_cost.text.toString())) {
            showValidationWarning(getString(R.string.site_cost_missing))
            return false
        }
        else  if (TextUtils.isEmpty(et_admin_edit_contact_number.text.toString())) {
            showValidationWarning(getString(R.string.site_contact_number_missing))
            return false
        }
        else  if (TextUtils.isEmpty(et_admin_edit_contact_person.text.toString())) {
            showValidationWarning(getString(R.string.site_name_missing))
            return false
        }
        else {
            return true
        }
    }

    fun createMap(): HashMap<String, Any>
    {
        val map = HashMap<String, Any>()
        map[Constants.SITE_NAME] = et_admin_edit_site_name.text.toString()
        map[Constants.SITE_TYPE] = spnr_admin_edit_type.selectedItem.toString()
        map[Constants.SITE_DATE] = et_admin_edit_start_date.text.toString()
        map[Constants.SITE_COST] = et_admin_edit_project_cost.text.toString()
        map[Constants.SITE_CONTACT] = et_admin_edit_contact_number.text.toString()
        map[Constants.SITE_PERSON] = et_admin_edit_contact_person.text.toString()
        map[Constants.SITE_LAT] = 0.0
        map[Constants.SITE_LON] = 0.0
        map[Constants.SITE_STATUS] = spnr_admin_edit_status.selectedItem.toString()
        return map
    }


}
