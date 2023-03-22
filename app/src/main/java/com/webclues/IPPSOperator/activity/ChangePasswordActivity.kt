package com.webclues.IPPSOperator.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.webclues.IPPSOperator.R
import kotlinx.android.synthetic.main.activity_change_password.*


class ChangePasswordActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        initView()
    }

    private fun initView() {
        btnChangePass.setOnClickListener(this)
        tvCancle.setOnClickListener(this)
    }

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.btnChangePass -> /*if (checkValidation()) */{
//                showpopup()
                Toast.makeText(this, "Password change Successfully", Toast.LENGTH_LONG).show()
                onBackPressed()
            }
            R.id.tvCancle -> onBackPressed()
        }
    }

  /*  private fun checkValidation(): Boolean {

        var isValid = true

        if (!Utility().isPassAndConfPassValid(this, edtNewPassWord, edtConformPass)) {
            isValid = false
        }
        return isValid


    }
*/
    private fun showpopup() {

        val dialog = Dialog(this@ChangePasswordActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_change_password)

        val btnBackLogin: Button = dialog.findViewById(R.id.btnOk)
        btnBackLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
                startActivity(Intent(this@ChangePasswordActivity, LoginActivity::class.java))

            }

        })

        dialog.show()
    }
}
