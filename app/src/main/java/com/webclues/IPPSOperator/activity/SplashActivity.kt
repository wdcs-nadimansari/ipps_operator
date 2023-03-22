package com.webclues.IPPSOperator.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.Static.Companion.SPLASH_SCREEN_TIME_OUT
import com.webclues.IPPSOperator.utility.TinyDb


class SplashActivity : AppCompatActivity() {


    val handler: Handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler.postDelayed(Runnable {

            NavigateToInfo()

        }, SPLASH_SCREEN_TIME_OUT.toLong())
    }

    private fun NavigateToInfo() {


        if (TinyDb(this@SplashActivity).getBoolean(Content.IS_LOGIN)) {

            startActivity(Intent(this, MainActivity::class.java))
            finish()

        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

}
