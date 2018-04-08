package net.appitiza.moderno.ui.activities.admin

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_create_site.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.BaseActivity
import java.util.HashMap

class CreateSiteActivity : BaseActivity() {

    internal var blood_list = arrayOf("Interior", "Exterior")
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mProgress: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_site)
        initializeFireBase()
        setSiteType()
        setClick()
    }


    private fun initializeFireBase() {
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }
    fun setSiteType()
    {
        val bloodgroups = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, blood_list)
        bloodgroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnr_admin_create_type.adapter = bloodgroups
    }
    private fun setClick() {
        tv_admin_create_create.setOnClickListener {
            if (validation())
            {
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

        }
    }
    private fun validation(): Boolean {
        if (TextUtils.isEmpty(et_admin_create_site_name.text.toString())) {
            showValidationWarning(getString(R.string.site_name_missing))
            return false
        }else   if (TextUtils.isEmpty(et_admin_create_start_date.text.toString())) {
            showValidationWarning(getString(R.string.site_date_missing))
            return false
        } else  if (TextUtils.isEmpty(et_admin_create_project_cost.text.toString())) {
            showValidationWarning(getString(R.string.site_cost_missing))
            return false
        }
        else  if (TextUtils.isEmpty(et_admin_create_contact_number.text.toString())) {
            showValidationWarning(getString(R.string.site_contact_number_missing))
            return false
        }
        else  if (TextUtils.isEmpty(et_admin_create_contact_person.text.toString())) {
            showValidationWarning(getString(R.string.site_name_missing))
            return false
        }
        else {
            return true
        }
    }

    fun createMap():HashMap<String, Any>
    {
        val map = HashMap<String, Any>()
        map[Constants.SITE_NAME] = et_admin_create_site_name.text.toString()
        map[Constants.SITE_TYPE] = spnr_admin_create_type.selectedItem.toString()
        map[Constants.SITE_DATE] = et_admin_create_start_date.text.toString()
        map[Constants.SITE_COST] = et_admin_create_project_cost.text.toString()
        map[Constants.SITE_CONTACT] = et_admin_create_contact_number.text.toString()
        map[Constants.SITE_PERSON] = et_admin_create_contact_person.text.toString()
        map[Constants.SITE_STATUS] = "undergoing"
        return map
    }


}
