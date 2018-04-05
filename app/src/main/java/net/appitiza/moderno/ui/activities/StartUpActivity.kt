package net.appitiza.moderno.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start_up.*
import net.appitiza.moderno.R

class StartUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)
        tv_login_login.setOnClickListener { startActivity(Intent(this@StartUpActivity,AdminActivity::class.java)) }
    }
}
