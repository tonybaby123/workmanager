package net.appitiza.moderno.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_register.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.users.UsersActivity
import net.appitiza.moderno.utils.PreferenceHelper
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private var mProgress: ProgressDialog? = null

    //Firebase auth
    private var mAuth: FirebaseAuth? = null
    //Firestore referrence
    private lateinit var db: FirebaseFirestore

    private var isLoggedIn by PreferenceHelper(Constants.PREF_KEY_IS_USER_LOGGED_IN, false)
    private var displayName by PreferenceHelper(Constants.PREF_KEY_IS_USER_DISPLAY_NAME, "")
    private var useremail by PreferenceHelper(Constants.PREF_KEY_IS_USER_EMAIL, "")
    private var userpassword by PreferenceHelper(Constants.PREF_KEY_IS_USER_PASSWORD, "")
    private var usertype by PreferenceHelper(Constants.PREF_KEY_IS_USER_USER_TYPE, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initializeFireBase()
        tv_register_register.setOnClickListener { registerUser(et_register_name.text.toString(), et_register_email.text.toString(), et_register_password.text.toString()) }
    }

    private fun initializeFireBase() {
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun registerUser(displayname: String, email: String, password: String) {
        if (validation(displayname, email, password)) {
            mProgress?.setTitle(getString(R.string.app_name))
            mProgress?.setMessage(getString(R.string.signing_message))
            mProgress?.setCancelable(false)
            mProgress?.show()

            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            // Sign in success, update UI with the signed-in user's information
                            val user = mAuth?.getCurrentUser()
                            val uid = user!!.uid
                            val deviceToken = FirebaseInstanceId.getInstance().token
                            val map = HashMap<String, Any>()
                            map[Constants.USER_DISPLAY_NAME] = displayname
                            map[Constants.USER_EMAIL] = mAuth?.getCurrentUser()?.email.toString()
                            map[Constants.USER_TOKEN] = deviceToken.toString()
                            map[Constants.USER_TYPE] = "user"



                            db.collection(Constants.USER_TABLE_NAME)
                                    .document(mAuth?.getCurrentUser()?.email.toString())
                                    .set(map, SetOptions.merge())
                                    .addOnCompleteListener { reg_task ->
                                        if (reg_task.isSuccessful) {
                                            mProgress!!.dismiss()
                                            useremail = mAuth?.getCurrentUser()?.email.toString()
                                            isLoggedIn = true
                                            this.displayName = displayname
                                            userpassword = password
                                            usertype = "user"
                                            startActivity(Intent(this@RegisterActivity,UsersActivity::class.java))
                                            finish()

                                        } else {
                                            mProgress!!.hide()
                                            Toast.makeText(this@RegisterActivity, task.exception?.message.toString(),
                                                    Toast.LENGTH_SHORT).show()
                                        }
                                    }



                        } else {
                            mProgress!!.hide()
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this@RegisterActivity, task.exception?.message.toString(),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this@RegisterActivity, "incomplete",
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun validation(displayname: String, email: String, password: String): Boolean {
        if (displayname.equals("")) {
            return false
        } else if (email.equals("")) {
            return false
        } else if (password.equals("") || password.length < 6) {
            return false
        } else {
            return true
        }
    }
}
