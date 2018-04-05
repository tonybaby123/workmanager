package net.appitiza.moderno.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import net.appitiza.moderno.R
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.ui.activities.admin.AdminActivity
import net.appitiza.moderno.utils.PreferenceHelper

class SplashActivity : AppCompatActivity() {

    private val delayTime: Long = 6000
    private var delayJob: Job? = null
    private var isLoggedIn by PreferenceHelper(Constants.PREF_KEY_IS_USER_LOGGED_IN, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Fabric.with(this, Crashlytics())
        if (isLoggedIn) {
            //Navigate with delay
            delayJob = login()
        } else {

            delayJob = delaySplashScreen()
        }
    }

    public override fun onDestroy() {
        delayJob?.cancel()

        super.onDestroy()
    }

    private fun delaySplashScreen() = launch(UI) {
        tv_name.text = "Configuring.."
        async(CommonPool) { delay(delayTime) }.await()
        //isLoggedIn = true;
        val intent = Intent(this@SplashActivity, StartUpActivity::class.java)
        startActivity(intent);
        finish()
    }


    private fun login() = launch(UI)
    {
        tv_name.text = "Logging.."
        async(CommonPool) { delay(delayTime) }.await()
        val intent = Intent(this@SplashActivity, AdminActivity::class.java)
        startActivity(intent);
        finish()
    }
}
