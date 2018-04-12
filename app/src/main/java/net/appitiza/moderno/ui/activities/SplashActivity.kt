package net.appitiza.moderno.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.admin.AdminActivity
import net.appitiza.moderno.ui.activities.users.UsersActivity
import net.appitiza.moderno.utils.PreferenceHelper

class SplashActivity : AppCompatActivity() {

    private val delayTime: Long = 3000
    private var delayJob: Job? = null

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
        setContentView(R.layout.activity_splash)
        Fabric.with(this, Crashlytics())
        initialize()
        if (isLoggedIn) {
            //Navigate with delay
            login()
        } else {

            delayJob = delaySplashScreen()
        }
    }

    public override fun onDestroy() {
        delayJob?.cancel()

        super.onDestroy()
    }

    private fun initialize() {
        //Firebase auth
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun delaySplashScreen() = launch(UI) {
        tv_name.text = "Configuring.."
        async(CommonPool) { delay(delayTime) }.await()
        //isLoggedIn = true;
        val intent = Intent(this@SplashActivity, StartUpActivity::class.java)
        startActivity(intent);
        finish()
    }


    private fun login() {
        if (!useremail.equals("") && !userpassword.equals("")) {
            loginUser(useremail, userpassword)

        } else {
            val intent = Intent(this@SplashActivity, StartUpActivity::class.java)
            startActivity(intent);
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {

        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful) {
                db.collection(Constants.COLLECTION_USER)
                        /*.whereEqualTo(Constants.USER_EMAIL, mAuth?.currentUser?.email.toString())*/
                        .document(email)
                        .get()
                        .addOnCompleteListener { login_task ->
                            if (login_task.isSuccessful) {
                                val document = login_task.result
                                if (document.exists()) {
                                    useremail = mAuth?.currentUser?.email.toString()
                                    isLoggedIn = true
                                    displayName =  document.data[Constants.USER_DISPLAY_NAME].toString()
                                    userpassword = password
                                    usertype = document.data[Constants.USER_TYPE].toString()

                                    if (usertype.equals("user")) {
                                        val intent = Intent(this@SplashActivity, UsersActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        val intent = Intent(this@SplashActivity, AdminActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }


                                } else {
                                    val intent = Intent(this@SplashActivity, StartUpActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                            } else {
                                val intent = Intent(this@SplashActivity, StartUpActivity::class.java)
                                startActivity(intent)
                                finish()

                            }
                        }


            } else {
                val intent = Intent(this@SplashActivity, StartUpActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }
}

