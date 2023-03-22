package com.webclues.IPPSOperator.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.application.mApplicationClass
import com.webclues.IPPSOperator.service.checkNetworkState
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.CustomProgress
import com.webclues.IPPSOperator.utility.Log
import com.webclues.IPPSOperator.utility.Utility
import com.webclues.IPPSOperator.service.ApiInterface
import com.webclues.IPPSOperator.utility.TinyDb
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var apiInterface: ApiInterface
    lateinit var deviceid: String
    lateinit var fcmtoken: String
    lateinit var tinyDb: TinyDb
    lateinit var customProgress: CustomProgress
    lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context = this
        initview()
    }

    private fun initview() {
        customProgress = CustomProgress.instance
        deviceid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        btnlogin.setOnClickListener(this)
        tvregister.setOnClickListener(this)
        tvforgot.setOnClickListener(this)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Log.e("fcm_token", "=" + it.token)
            TinyDb(context).putString(Content.FCM_TOKEN, it.token)
            fcmtoken = it.token

        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnlogin -> if (isvalidate()) {
                login(
                    edtEmail.text.toString().trim(), deviceid, fcmtoken, Content.DEVICE_TYPE,
                    edtPassword.text.toString().trim(), Content.OPERATOR_POSITION, ""
                )
            }
            R.id.tvregister -> {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }
            R.id.tvforgot -> {
                startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
            }
        }
    }

    private fun isvalidate(): Boolean {
        var isValid = true

        if (!Utility().isEmailValid(context, edtEmail)) {
            isValid = false
        }
        if (!Utility().isPasswordValid(context, edtPassword)) {
            isValid = false
        }
        if (!checkNetworkState(context)) {
            Utility().showOkDialog(
                context,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
            isValid = false

        }
        return isValid
    }

    fun login(
        email: String,
        deviceid: String,
        fcmtoken: String,
        device_type: String,
        password: String,
        operatorposition: String,
        destroy_auth: String
    ) {
        customProgress.showProgress(this, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiInterface.login(
            email,
            deviceid, fcmtoken, device_type,
            password,
            operatorposition, destroy_auth

        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful) {
                    customProgress.hideProgress()
                    val jsonObject = JSONObject(response.body().toString())
                    val statuscode: Int = jsonObject.optInt("status_code")
                    Log.e("Response_code ", "=" + response.code())
                    if (statuscode == 200) {
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                            SetPrefData(data)

                        } else {
                            customProgress.hideProgress()
                            Utility().showOkDialog(context, resources.getString(R.string.app_name), jsonObject.optString("message"))
                        }
                    } else if (statuscode == 402) {
                        customProgress.hideProgress()
                        showpopuplogin(context, resources.getString(R.string.app_name), jsonObject.optString("message"))

                    } else {
                        customProgress.hideProgress()
                        Utility().showOkDialog(context, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }
                } else {
                    customProgress.hideProgress()
                    Utility().showOkDialog(context, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgress.hideProgress()
                Utility().showOkDialog(context, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

            }
        })

    }

    private fun SetPrefData(data: JSONObject) {

        TinyDb(context).putBoolean(Content.IS_LOGIN, true)
        TinyDb(context).putString(Content.USER_DATA, data.toString())
        TinyDb(context).putString(Content.USER_ID, data.optString(Content.USER_ID))
        TinyDb(context).putString(Content.AUTORIZATION_TOKEN, "Bearer " + data.optString(Content.AUTORIZATION_TOKEN))
        TinyDb(context).putString(Content.FCM_TOKEN, fcmtoken)
        TinyDb(context).putString(Content.FIREBASE_UID, data.optJSONObject("firebase_credential").optString(Content.FIREBASE_UID))
        mApplicationClass.getInstance()?.setFirebaseUser(data)
        startActivity(Intent(context, MainActivity::class.java))
        finishAffinity()


    }

    fun showpopuplogin(context: Context, Title: String, Message: String) {
        val dialog = Dialog(context)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.rectangle_background)
        dialog.setContentView(R.layout.popup_login)
        dialog.show()

        val txtTitle = dialog.findViewById(R.id.txtTitle) as TextView
        val txtMessage = dialog.findViewById(R.id.txtMessage) as TextView
        val txtOk = dialog.findViewById(R.id.txtOk) as TextView
        val txtCancel = dialog.findViewById(R.id.txtCancel) as TextView

        txtTitle.text = Title
        txtMessage.text = Message
        txtCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
            }

        })
        txtOk.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
                login(
                    edtEmail.text.toString().trim(),
                    deviceid,
                    fcmtoken,
                    Content.DEVICE_TYPE,
                    edtPassword.text.toString().trim(),
                    Content.OPERATOR_POSITION,
                    Content.DESTROY_AUTH
                )
            }
        })

    }


}
