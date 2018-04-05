package net.appitiza.moderno.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_start_up.*
import net.appitiza.moderno.R
import net.appitiza.moderno.ui.activities.admin.AdminActivity

class StartUpActivity : AppCompatActivity() {

    private var mProgress: ProgressDialog? = null
    //Firebase auth
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)
        initialize()
        tv_login_login.setOnClickListener {

            loginUser(et_login_email.text.toString(), et_login_password.text.toString())
        }
        tv_login_register.setOnClickListener {


            val intent = Intent(this@StartUpActivity, RegisterActivity::class.java)

            val p1 = Pair(tv_email_txt as View, getString(R.string.emailtext_login_register))
            val p2 = Pair(et_login_email as View, getString(R.string.email_login_register))
            val p3 = Pair(tv_password_txt as View, getString(R.string.passwordtext_login_register))
            val p4 = Pair(et_login_password as View, getString(R.string.password_login_register))
            val p5 = Pair(tv_login_register as View, getString(R.string.register_login_register))
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@StartUpActivity, p1, p2, p3, p4, p5)
            startActivity(intent, options.toBundle())
        }
    }

    private fun initialize() {
        mProgress = ProgressDialog(this)
        //Firebase auth
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
    }

    private fun loginUser(email: String, password: String) {
        if (validation(email, password)) {
            mProgress?.setTitle("Registering")
            mProgress?.setMessage("Please wait..")
            mProgress?.setCancelable(false)
            mProgress?.show()

            mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful) {
                    mProgress?.dismiss()

                    val deviceToken = FirebaseInstanceId.getInstance().token
                    val userId = mAuth?.getCurrentUser()!!.uid
                    mDatabase?.child("User")?.child(userId)?.child("device_token")?.setValue(deviceToken)?.addOnSuccessListener {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth?.getCurrentUser()
                        val intent = Intent(this@StartUpActivity, AdminActivity::class.java)
                        startActivity(intent);
                    }

                } else {
                    mProgress?.hide()
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@StartUpActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()

                }

            }
        } else {
            Toast.makeText(this@StartUpActivity, "incomplete",
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun validation(email: String, password: String): Boolean {
        if (email.equals("")) {
            return false
        } else if (password.equals("")) {
            return false
        } else {
            return true
        }
    }
}
