package net.appitiza.moderno.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_start_up.*
import net.appitiza.moderno.R
import net.appitiza.moderno.ui.activities.admin.AdminActivity

class StartUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)
        tv_login_login.setOnClickListener {
            startActivity(Intent(this@StartUpActivity, AdminActivity::class.java))
        }
        tv_login_register.setOnClickListener {


            val intent = Intent(this@StartUpActivity, RegisterActivity::class.java)

            val p1 = Pair(tv_email_txt as View, getString(R.string.emailtext_login_register))
            val p2 = Pair(et_login_email as View, getString(R.string.email_login_register))
            val p3 = Pair(tv_password_txt as View, getString(R.string.passwordtext_login_register))
            val p4 = Pair(et_login_password as View, getString(R.string.password_login_register))
            val p5 = Pair(tv_login_register as View, getString(R.string.register_login_register))
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@StartUpActivity, p1, p2, p3, p4,p5)
            startActivity(intent, options.toBundle())
        }
    }
}
