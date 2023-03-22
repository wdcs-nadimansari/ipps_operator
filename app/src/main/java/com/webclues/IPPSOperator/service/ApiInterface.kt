package com.webclues.IPPSOperator.service

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {


    // To send FCM Notification--------------------------------------------

    @POST("send")
    fun sendFCM(@Body body: JsonObject?): Call<JsonObject?>?

    //----------------------------------------------------
/*
    @FormUrlEncoded
    @POST("login")
    fun dologin(
        @Field("email") email: String,
        @Field("device_id") deviceid: String,
        @Field("fcm_token") fcm_token: String,
        @Field("device_type") device_type: String,
        @Field("password") password: String,
        @Field("user_type") Usertype: String,
        @Field("destroy_oauth") destroy_oauth: String
    ): Call<JsonObject>
*/


    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("device_id") deviceid: String,
        @Field("fcm_token") fcm_token: String,
        @Field("device_type") device_type: String,
        @Field("password") password: String,
        @Field("user_type") Usertype: String,
        @Field("destroy_oauth") destroy_oauth: String
    ): Call<JsonObject>


    @POST("companies_list")
    fun getcompanieslist(): Call<JsonObject>

    @POST("positions_list")
    fun getpositionlist(@Query("user_type") usertype: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("register")
    fun dosignup(
        @Field("email") email: String,
        @Field("first_name") firstname: String,
        @Field("last_name") lastname: String,
        @Field("phone") phone: String,
        @Field("company_id") coumpunyid: String,
        @Field("device_id") deviceid: String,
        @Field("fcm_token") fcm_token: String,
        @Field("device_type") device_type: String,
        @Field("user_type") Usertype: String,
        @Field("position_id") positionid: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("forget_password")
    fun forgetpassword(
        @Field("email") email: String,
        @Field("user_type") Usertype: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("logout")
    fun logout(@Field("fcm_token") fcm_token: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("change_password")
    fun changepassword(
        @Field("old_password") oldpassword: String,
        @Field("new_password") new_password: String,
        @Field("confirm_password") confirm_password: String
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("profile_detail")
    fun getprofiledetails(@Field("user_id") userid: String): Call<JsonObject>

    @Multipart
    @POST("update_profile")
    fun updateprofiledetails(
        @Part("first_name") firstname: RequestBody,
        @Part("last_name") lastname: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phonenumber: RequestBody,
        @Part("company_id") companyid: RequestBody,
        @Part("position_id") position_id: RequestBody,
        @Part("user_type") usertype: RequestBody,
        @Part profilepic: MultipartBody.Part?
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("contact_us")
    fun contactus(
        @Field("subject") subject: String,
        @Field("message") message: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("support")
    fun support(
        @Field("email") email: String,
        @Field("message") message: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("cms")
    fun aboutus(@Field("slug") slug: String): Call<JsonObject>

    @POST("faqs_list")
    fun faq(): Call<JsonObject>

    @POST("locations_list")
    fun getlocationlist(): Call<JsonObject>


    @FormUrlEncoded
    @POST("machines_list")
    fun getmachinelist(@Field("location_id") locationid: String): Call<JsonObject>

    @POST("problems_list")
    fun getproblemlist(): Call<JsonObject>

    @Multipart
    @POST("add_job")
    fun addjob(
        @Part("machine_id") machineid: RequestBody, @Part("problem_id") problemid: RequestBody,
        @Part("location_id") locationid: RequestBody, @Part("comment") comment: RequestBody,
        @Part imagearray: Array<MultipartBody.Part?>
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("job_by_status")
    fun jobstatus(
        @Field("page_number") pagenumber: Int,
        @Field("job_status") jobstatus: Int,
        @Field("filter_by_priority") filterpriority: Int,
        @Field("engineer_id") engineer_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("filter_by_priority")
    fun jobpriority(
        @Field("priority") priority: String,
        @Field("page_number") page_number: String,
        @Field("job_status") job_status: String,
        @Field("engineer_id") engineer_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("job_details")
    fun jobdetails(@Field("job_id") jobid: Int): Call<JsonObject>


    @FormUrlEncoded
    @POST("notification_list")
    fun notificationslist(@Field("page_number") page_number: Int): Call<JsonObject>

    @FormUrlEncoded
    @POST("refresh_token")
    fun refreshtoken(@Field("device_id") device_id: String, @Field("fcm_token") fcm_token: String)
            : Call<JsonObject>

    @FormUrlEncoded
    @POST("qr_code")
    fun Scanqrcode(@Field("qr_code") QRcode: String): Call<JsonObject>

    @Multipart
    @POST("upload_image")
    fun upload_image(@Part image: MultipartBody.Part?): Call<JsonObject>
}