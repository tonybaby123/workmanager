package net.appitiza.moderno.ui.splash.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import net.appitiza.moderno.R

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startHome()
    }
    fun startHome()
    {
        println("Start")

        tv_name.text = "Start";
        // Start a coroutine
        launch {
            delay(1000)
            //tv_name.text = "Stop";
        }

       // Thread.sleep(2000) // wait for 2 seconds
        tv_name.text = "Stop";
    }
}
