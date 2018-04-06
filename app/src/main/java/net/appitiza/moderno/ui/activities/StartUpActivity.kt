package net.appitiza.moderno.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_start_up.*
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.admin.AdminActivity
import net.appitiza.moderno.ui.activities.users.UsersActivity
import net.appitiza.moderno.utils.PreferenceHelper


class StartUpActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_start_up)
        initialize()
        setClick()

    }

    private fun initialize() {
        mProgress = ProgressDialog(this)
        //Firebase auth
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun setClick() {
        tv_login_login.setOnClickListener {

            loginUser(et_login_email.text.toString(), et_login_password.text.toString())
        }
        tv_login_reset_password.setOnClickListener { resetPassword(et_login_email.text.toString()) }
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

    private fun resetPassword(email: String) {
        if (!et_login_email.text.equals("")) {
            mProgress?.setTitle(getString(R.string.app_name))
            mProgress?.setMessage(getString(R.string.registering_message))
            mProgress?.setCancelable(false)
            mProgress?.show()
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { resetTask ->

                mProgress?.dismiss()
                if (resetTask.isSuccessful) {
                    Toast.makeText(this@StartUpActivity, "reset link sent",
                            Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@StartUpActivity, resetTask.exception.toString(),
                            Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this@StartUpActivity, "email missing",
                    Toast.LENGTH_SHORT).show()
        }

    }

    private fun loginUser(email: String, password: String) {
        if (validation(email, password)) {
            mProgress?.setTitle(getString(R.string.app_name))
            mProgress?.setMessage(getString(R.string.registering_message))
            mProgress?.setCancelable(false)
            mProgress?.show()

            mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful) {
                    mProgress?.dismiss()

                    db.collection(Constants.USER_TABLE_NAME)
                            .whereEqualTo(Constants.USER_EMAIL, mAuth?.currentUser?.email.toString())
                            .get()
                            .addOnCompleteListener { login_task ->
                                if (login_task.isSuccessful) {
                                    val document = task.result as DocumentSnapshot
                                    if (document.exists()) {
                                        useremail = mAuth?.getCurrentUser()?.email.toString()
                                        isLoggedIn = true
                                        displayName = document.data[Constants.USER_DISPLAY_NAME].toString()
                                        userpassword = password
                                        usertype = document.data[Constants.USER_TYPE].toString()
                                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@StartUpActivity, tv_login_login,
                                                ViewCompat.getTransitionName(tv_login_login))
                                        if (usertype.equals("user")) {
                                            val intent = Intent(this@StartUpActivity, UsersActivity::class.java)

                                            startActivity(intent, options.toBundle())
                                        } else {
                                            val intent = Intent(this@StartUpActivity, AdminActivity::class.java)

                                            startActivity(intent, options.toBundle())
                                        }


                                    } else {
                                        Toast.makeText(this@StartUpActivity, "no User Exist",
                                                Toast.LENGTH_SHORT).show()
                                    }

                                } else {
                                    Toast.makeText(this@StartUpActivity, login_task.exception.toString(),
                                            Toast.LENGTH_SHORT).show()

                                }
                            }
                } else {
                    mProgress?.hide()
                    Toast.makeText(this@StartUpActivity, task.exception.toString(),
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
