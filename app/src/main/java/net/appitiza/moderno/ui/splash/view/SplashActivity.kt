package net.appitiza.moderno.ui.splash.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.experimental.*
import net.appitiza.moderno.R
import net.appitiza.moderno.ui.home.view.HomeActivity
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.coroutineContext
import kotlinx.coroutines.experimental.android.UI
class SplashActivity : AppCompatActivity() {

    private val delayTime: Long = 3000
    private var delayJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Navigate with delay
        delayJob = delaySplashScreen()

    }

    public override fun onDestroy() {
        delayJob?.cancel()

        super.onDestroy()
    }

    private fun delaySplashScreen() = launch(UI) {
        async (CommonPool) { delay(delayTime) } .await()

        val intent = Intent(this@SplashActivity,HomeActivity::class.java)
        startActivity(intent);
        finish()
    }
}
