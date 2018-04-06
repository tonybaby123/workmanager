package net.appitiza.moderno.ui.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import net.appitiza.moderno.R
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private var mProgress: ProgressDialog? = null

    //Firebase auth
    private var mAuth: FirebaseAuth? = null
    //Firebase referrence
    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initializeFireBase()
        tv_register_register.setOnClickListener { registerUser(et_register_name.text.toString(), et_register_email.text.toString(), et_register_password.text.toString()) }
    }

    private fun initializeFireBase() {
        mProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
    }

    private fun registerUser(displayname: String, email: String, password: String) {
        if (validation(displayname, email, password)) {
            mProgress?.setTitle("Registering")
            mProgress?.setMessage("Please wait..")
            mProgress?.setCancelable(false)
            mProgress?.show()

            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            // Sign in success, update UI with the signed-in user's information
                            val user = mAuth?.getCurrentUser()
                            val uid = user!!.uid
                            mDatabase = FirebaseDatabase.getInstance().reference.child("User").child(uid)
                            val map = HashMap<String, String>()
                            map["name"] = displayname
                            map["type"] = "user"
                            mDatabase?.setValue(map)?.addOnCompleteListener { reg_task ->
                                if (reg_task.isSuccessful) {
                                    mProgress!!.dismiss()
                                    finish()

                                } else {
                                    mProgress!!.hide()
                                    Toast.makeText(this@RegisterActivity, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            mProgress!!.hide()
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this@RegisterActivity, "Authentication failed.",
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
