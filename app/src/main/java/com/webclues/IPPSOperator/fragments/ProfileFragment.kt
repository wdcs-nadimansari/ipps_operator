package com.webclues.IPPSOperator.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.Modelclass.ProfileResponse
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.EditProfileActivity
import com.webclues.IPPSOperator.activity.MainActivity
import com.webclues.IPPSOperator.service.checkNetworkState
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.CustomProgress
import com.webclues.IPPSOperator.utility.Log
import com.webclues.IPPSOperator.utility.Utility
import com.squareup.picasso.Picasso
import com.webclues.IPPSOperator.service.ApiInterface
import com.webclues.IPPSOperator.utility.TinyDb
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment(), View.OnClickListener {

    var positionList: ArrayList<String> = arrayListOf("Position 1", "Position 2", "Position 3")
    var companyList: ArrayList<String> = arrayListOf("Company 1", "Company 2", "Company 3")
    lateinit var edtFirstName: EditText
    lateinit var edtLastName: EditText
    lateinit var edtEmail: EditText
    lateinit var edtCompany: AutoCompleteTextView
    lateinit var edtPosition: AutoCompleteTextView
    lateinit var edtPhone: EditText
    lateinit var btnEditProfile: Button
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        initview(view)
        return view

    }

    private fun initview(view: View) {
        customProgress = CustomProgress.instance
        edtFirstName = view.findViewById(R.id.edtFirstName)
        edtLastName = view.findViewById(R.id.edtLastName)
        edtEmail = view.findViewById(R.id.edtEmail)
        edtCompany = view.findViewById(R.id.edtCompany)
        edtPosition = view.findViewById(R.id.edtPosition)
        edtPhone = view.findViewById(R.id.edtPhone)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener(this)
        setData()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnEditProfile -> {
                startActivityForResult(
                    Intent(context!!, EditProfileActivity::class.java),
                    Content.REQ_EDIT_PROFILE
                )

            }
        }
    }


    fun setData() {

        edtFirstName.keyListener = null
        edtLastName.keyListener = null
        edtEmail.keyListener = null
        edtPhone.keyListener = null
        edtCompany.keyListener = null
        edtPosition.keyListener = null


        edtFirstName.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtLastName.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtEmail.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtPhone.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtCompany.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtPosition.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))




        if (checkNetworkState(activity!!)) {
            getprofiledetails(TinyDb(activity!!).getString(Content.USER_ID))
        } else {
            Utility().showOkDialog(
                context!!,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
        }

    }

    private fun getprofiledetails(userid: String) {    //get profile data

        customProgress.showProgress(context!!, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(context!!).create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiInterface.getprofiledetails(
            userid
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Response_code", "=" + response.code())
                customProgress.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {

                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val profileResponse =
                            Gson().fromJson(data.toString(), ProfileResponse::class.java)
                        SetProfileData(profileResponse)

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

    private fun SetProfileData(profileResponse: ProfileResponse) {  // set profile data
        Picasso.get().load(profileResponse.profile_pic)
            .placeholder(R.drawable.ic_placeholder_profile).error(R.drawable.ic_placeholder_profile)
            .into(ivProfile)
        edtFirstName.setText(profileResponse.first_name)
        edtLastName.setText(profileResponse.last_name)
        edtEmail.setText(profileResponse.email)
        edtPhone.setText(profileResponse.phone)
        edtPosition.setText(profileResponse.position.position_name)
        edtCompany.setText(profileResponse.company.company_name)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Content.REQ_EDIT_PROFILE) {
            if (resultCode == RESULT_OK) {
                getprofiledetails(TinyDb(activity!!).getString(Content.USER_ID))
            }
        }
    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).txtTitle.text = getString(R.string.profile)
    }
}
