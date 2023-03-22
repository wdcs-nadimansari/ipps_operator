package com.webclues.IPPSOperator.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.Modelclass.UserRespone

import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.MainActivity
import com.webclues.IPPSOperator.service.checkNetworkState
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.CustomProgress
import com.webclues.IPPSOperator.utility.Log
import com.webclues.IPPSOperator.utility.Utility
import com.webclues.IPPSOperator.service.ApiInterface
import com.webclues.IPPSOperator.utility.TinyDb
import kotlinx.android.synthetic.main.fragment_contact_us.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class ContactUsFragment : Fragment(), View.OnClickListener {

    lateinit var btnSend: Button
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    lateinit var userdetails: UserRespone

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_contact_us, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview(view)
    }

    private fun initview(view: View) {
        customProgress = CustomProgress.instance
        btnSend = view.findViewById(R.id.btnSend)
        userdetails = Gson().fromJson(
            TinyDb(activity!!).getString(Content.USER_DATA),
            UserRespone::class.java
        )

        btnSend.setOnClickListener(this)
    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btnSend -> {
                if (isvalidate()) {
                    contactus(
                        edtSubject.text.toString().trim(),
                        edtMessage.text.toString().trim()
                    )

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).txtTitle.text = resources.getString(R.string.contact_us)
    }

    private fun isvalidate(): Boolean {
        var isvalid = true
        if (!Utility().isSubjectValid(context!!, edtSubject)) {
            isvalid = false
        }
        if (!Utility().isMessagevalid(context!!, edtMessage)) {
            isvalid = false
        }
        if (!checkNetworkState(context!!)) {
            Utility().showOkDialog(
                context!!,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
            isvalid = false
        }
        return isvalid
    }

    private fun contactus(          //Contactus APi call
        subject: String,
        message: String
    ) {
        customProgress.showProgress(context!!, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(context!!).create(ApiInterface::class.java)
        val call: Call<JsonObject> =
            apiInterface.contactus(subject, message)
        call.enqueue(object : Callback<JsonObject> {


            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                Log.e("Response_code", "=" + response.code())
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        var data = jsonObject.optJSONObject("data")
                        edtSubject.setText("")
                        edtMessage.setText("")

                    } else {
                        Utility().showOkDialog(
                            context!!,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                } else if (statuscode == 403) {
                    Utility().showInactiveDialog(
                        context!!,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )


                } else {
                    Utility().showOkDialog(
                        context!!,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgress.hideProgress()
                Utility().showOkDialog(
                    context!!,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })

    }

}
