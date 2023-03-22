package com.webclues.IPPSOperator.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.Modelclass.PositionListReponse
import com.webclues.IPPSOperator.Modelclass.UserRespone
import com.webclues.IPPSOperator.Modelclass.companymodel
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.application.mApplicationClass
import com.webclues.IPPSOperator.service.checkNetworkState
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.CustomProgress
import com.webclues.IPPSOperator.utility.Log
import com.webclues.IPPSOperator.utility.Utility
import com.squareup.picasso.Picasso
import com.webclues.IPPSOperator.service.ApiInterface
import com.webclues.IPPSOperator.utility.CircleTransform
import com.webclues.IPPSOperator.utility.TinyDb
import kotlinx.android.synthetic.main.activity_edit_profile.edtLastName
import kotlinx.android.synthetic.main.activity_profile.btnEditProfile
import kotlinx.android.synthetic.main.activity_profile.edtCompany
import kotlinx.android.synthetic.main.activity_profile.edtEmail
import kotlinx.android.synthetic.main.activity_profile.edtFirstName
import kotlinx.android.synthetic.main.activity_profile.edtPhone
import kotlinx.android.synthetic.main.activity_profile.edtPosition
import kotlinx.android.synthetic.main.activity_profile.frmProfile
import kotlinx.android.synthetic.main.activity_profile.ivBack
import kotlinx.android.synthetic.main.activity_profile.ivProfile
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    var positionList: ArrayList<String> = arrayListOf("Position 1", "Position 2", "Position 3")
    var companyList: ArrayList<String> = arrayListOf("Company 1", "Company 2", "Company 3")
    lateinit var mContext: Context
    var imagePath: String = ""
    lateinit var FileName: String
    lateinit var userdetails: UserRespone
    var positionarraylist: ArrayList<PositionListReponse> = arrayListOf()
    var stringpositionarraylist: ArrayList<String> = arrayListOf()
    var companyarraylist: ArrayList<companymodel> = arrayListOf()
    var stringcompanyarratlist: ArrayList<String> = arrayListOf()
    lateinit var apiInterface: ApiInterface
    lateinit var positionid: String
    lateinit var companyid: String
    lateinit var customprogress: CustomProgress


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        mContext = this

        initview()

    }

    private fun initview() {

        customprogress = CustomProgress.instance

        btnEditProfile.text = getString(R.string.save)
        btnEditProfile.tag = "save"

        userdetails =
            Gson().fromJson(TinyDb(this).getString(Content.USER_DATA), UserRespone::class.java)
        Setdata(userdetails)

        frmProfile.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        btnEditProfile.setOnClickListener(this)
        frmProfile.setOnClickListener(this)

    }

    private fun Setdata(userdetails: UserRespone) {
        Picasso.get().load(userdetails.profile_pic).placeholder(R.drawable.ic_placeholder_profile)
            .error(R.drawable.ic_placeholder_profile).into(ivProfile)
        edtFirstName.setText(userdetails.first_name)
        edtLastName.setText(userdetails.last_name)
        edtEmail.setText(userdetails.email)
        edtPhone.setText(userdetails.phone)
        edtPosition.setText(userdetails.position.position_name)
        edtCompany.setText(userdetails.company.company_name)
        positionid = userdetails.position.position_id.toString()
        companyid = userdetails.company.company_id.toString()

        getpositionlist()

        getcompunylist()

    }


    fun getpositionlist() {   // position List

        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiInterface.getpositionlist(Content.OPERATOR_POSITION)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Response_code", "=" + response.code())
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val positionlist = data.optJSONArray("positions_list")
                        if (positionlist != null && positionlist.length() > 0) {
                            positionarraylist.clear()

                            positionarraylist.addAll(
                                Gson().fromJson(
                                    positionlist.toString(),
                                    Array<PositionListReponse>::class.java
                                ).toList()
                            )

                            Setpositiondata(positionarraylist)

                        }
                    } else {
                        Utility().showOkDialog(
                            mContext,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )

            }
        })


    }

    private fun Setpositiondata(positionarraylist: ArrayList<PositionListReponse>) {      // set position data


        for (item in positionarraylist) {
            stringpositionarraylist.add(item.position_name)
        }

        edtPosition.setAdapter(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item, stringpositionarraylist
            )
        )
        edtPosition.threshold = 0
        edtPosition.keyListener = null
        edtPosition.setOnTouchListener(View.OnTouchListener { v, event ->
            edtPosition.showDropDown()
            Utility.hideKeyboard(this)
            return@OnTouchListener false
        })
        edtPosition.setOnItemClickListener { parent, view, position, id ->
            positionid = positionarraylist.get(position).position_id.toString()
        }
    }


    fun getcompunylist() {            // get company list

        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiInterface.getcompanieslist()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Response_code", "=" + response.code())
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val coumpanylist = data.optJSONArray("companies_list")
                        if (coumpanylist != null && coumpanylist.length() > 0) {
                            companyarraylist.clear()
                            companyarraylist.addAll(
                                Gson().fromJson(
                                    coumpanylist.toString(),
                                    Array<companymodel>::class.java
                                ).toList()
                            )

                            Setcoumpunydata(companyarraylist)

                        }
                    } else {
                        Utility().showOkDialog(
                            mContext,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    mContext,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )

            }
        })

    }

    private fun Setcoumpunydata(coumpunyarraylist: ArrayList<companymodel>) {  // Set company list data

        for (items in coumpunyarraylist) {
            stringcompanyarratlist.add(items.company_name)

        }

        edtCompany.setAdapter(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                stringcompanyarratlist
            )
        )
        edtCompany.threshold = 0
        edtCompany.keyListener = null
        edtCompany.setOnTouchListener(View.OnTouchListener { v, event ->
            edtCompany.showDropDown()
            Utility.hideKeyboard(this)
            return@OnTouchListener false
        })
        edtCompany.setOnItemClickListener { parent, view, position, id ->
            companyid = coumpunyarraylist.get(position).company_id.toString()
        }

    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.ivBack -> {
                Utility.hideSoftKeyBoard(this)
                finish()
            }
            R.id.btnEditProfile -> {

                if (isvalidate()) {
                    EditProfile()
                }

            }
            R.id.frmProfile -> {

                if (isPermissionGranted()) {
                    AlertDialogForImagePicker()
                }
            }
        }
    }

    private fun isvalidate(): Boolean {
        var isValid = true
        if (!Utility().isValidFirstName(this, edtFirstName)) {
            isValid = false
        }
        if (!Utility().isValidLastName(this, edtLastName)) {
            isValid = false
        }
        if (!Utility().isEmailValid(this, edtEmail)) {
            isValid = false
        }
        if (!Utility().isPhoneValid(this, edtPhone)) {
            isValid = false
        }
        if (!Utility().isPositionValid(this, edtPosition)) {
            isValid = false
        }
        if (!Utility().isCompanyValid(this, edtCompany)) {
            isValid = false
        }
        if (!checkNetworkState(this)) {
            Utility().showOkDialog(
                this,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
            isValid = false
        }
        return isValid
    }


    private fun EditProfile() {     //Updateprofile Api call

        customprogress.showProgress(this, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        val firstname: RequestBody = edtFirstName.text.toString().trim()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val lastname: RequestBody = edtLastName.text.toString().trim()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val email: RequestBody = edtEmail.text.toString().trim()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val phonenumber: RequestBody = edtPhone.text.toString().trim()
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val positionid: RequestBody =
            positionid.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val usertype: RequestBody =
            Content.OPERATOR_POSITION.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val companyid: RequestBody =
            companyid.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        var profilepic: MultipartBody.Part? = null

        if (imagePath.trim().length > 0) {
            Log.e("Imagepath", "=" + imagePath)
            val file: RequestBody = File(imagePath).asRequestBody("image/*".toMediaTypeOrNull())
            profilepic = MultipartBody.Part.createFormData("profile_pic", FileName, file)
        }

        val call: Call<JsonObject> = apiInterface.updateprofiledetails(
            firstname,
            lastname,
            email,
            phonenumber,
            companyid,
            positionid, usertype,
            profilepic
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customprogress.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                        SetEditData(data)

                    } else {
                        Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))

                    }
                } else if (statuscode == 403) {
                    Utility().showInactiveDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                } else {
                    Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                }

            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customprogress.hideProgress()
                Utility().showOkDialog(mContext, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

            }

        })
    }

    private fun SetEditData(data: JSONObject) {
        TinyDb(this).putString(Content.USER_DATA, data.toString())
        TinyDb(this).putString(Content.AUTORIZATION_TOKEN, "Bearer " + data.optString(Content.AUTORIZATION_TOKEN))
        mApplicationClass.getInstance()?.EditFirebaseUser(data)
        val intent = Intent()
        intent.putExtra(Content.USER_DATA, data.optString(Content.USER_DATA))
        setResult(Activity.RESULT_OK, intent)
        finish()

    }


    /**
     * @ Function Name      : isPermissionGranted
     * @ Function Params    : none
     * @ Function Purpose   : to check that if permission of camera and storage is granted or not.
     * if not then show popup to grant the permission.
     */
    fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                AlertDialogForImagePicker()
            } else {
                showPermissionNotAllowed()
            }
        }
    }

    private fun showPermissionNotAllowed() { //        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditProfileActivity.this);


        val dialog = AlertDialog.Builder(this)

        dialog.setMessage(resources.getString(R.string.lbl_grant_required_permissions_line))
            .setPositiveButton("Yes") { dialog, which ->
                goToPermissionSetting()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
                finish()
            }

        dialog.show()
    }

    private fun goToPermissionSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, Content.REQ_PERMISSION)
    }

    private fun AlertDialogForImagePicker() {
        Utility.hideKeyboard(mContext)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.image_picker_popup, null)
        //        View dialogView = inflater.inflate(R.layout.pop   _up_picker, null);
        val mpopup = PopupWindow(
            dialogView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        ) //Creation of popup
        mpopup.animationStyle = android.R.style.Animation_Dialog
        (dialogView.findViewById<View>(R.id.txtcamera) as TextView).setOnClickListener {

            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    600,
                    600
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .cameraOnly()
                .start(Content.REQ_PICK_IMAGE)

            mpopup.dismiss()
        }
        (dialogView.findViewById<View>(R.id.txtGallery) as TextView).setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    600,
                    600
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .galleryOnly()
                .start(Content.REQ_PICK_IMAGE)
            mpopup.dismiss()
        }
        (dialogView.findViewById<View>(R.id.txtCancel) as TextView).setOnClickListener { mpopup.dismiss() }
        mpopup.showAtLocation(dialogView, Gravity.CENTER, 0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Content.REQ_PERMISSION) {
            isPermissionGranted()
        } else if (requestCode == Content.REQ_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
//                imgProfile.setImageURI(fileUri)
                Picasso.get().load(fileUri).transform(CircleTransform())
                    .placeholder(R.drawable.ic_placeholder_profile).into(ivProfile)

                //You can get File object from intent
                val file: File? = ImagePicker.getFile(data)
                FileName = fileUri!!.lastPathSegment!!
                //You can also get File Path from intent
                imagePath = ImagePicker.getFilePath(data)!!

            }
        }
    }

}
