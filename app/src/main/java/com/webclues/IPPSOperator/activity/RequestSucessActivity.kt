package com.webclues.IPPSOperator.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.webclues.IPPSOperator.R
import kotlinx.android.synthetic.main.activity_request_sucess.*

class RequestSucessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_sucess)
        btnBackToHome.setOnClickListener(View.OnClickListener {
            finishAffinity()
            startActivity(Intent(this@RequestSucessActivity, MainActivity::class.java))
        })
    }
}
