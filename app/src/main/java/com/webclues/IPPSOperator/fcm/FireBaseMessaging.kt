package com.webclues.IPPSOperator.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.Modelclass.GroupListModel
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.Modelclass.NotificationModel
import com.webclues.IPPSOperator.Modelclass.UserRespone
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.*
import com.webclues.IPPSOperator.application.mApplicationClass
import com.webclues.IPPSOperator.service.checkNetworkState
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.Log
import com.webclues.IPPSOperator.utility.Utility
import com.webclues.IPPSOperator.service.ApiInterface
import com.webclues.IPPSOperator.utility.TinyDb
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*

class FireBaseMessaging : FirebaseMessagingService() {

    private var JobId: Int = 0
    private lateinit var mContext: Context
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("Fcm_token", "=" + token)
        if (checkNetworkState(this)) {

            SendRegistrationtoServer(token)
        }
    }

    fun SendRegistrationtoServer(token: String) {
        Log.e("Token", "=" + token)
        val deviceid: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiInterface.refreshtoken(deviceid, token)
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {

                    Log.e("Response->refeshToken", response.body()!!.toString())
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }
        })

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("remotemessage", "=" + remoteMessage.data)
        mContext = this
        if (remoteMessage.data.containsKey("groupId")) {
            Log.e("onMessageReceived", "Chat data payload: " + remoteMessage.data)
            if (ChatActivity.groupId == null) {
                notifyChat(remoteMessage, mContext)
            } else {
                if (!ChatActivity.groupId.equals(remoteMessage.data["groupId"])) {
                    notifyChat(remoteMessage, mContext)
                }
            }
        } else {
            try {
                if (remoteMessage.data.size > 0) {
                    Log.e("NotificationBody", "=" + remoteMessage.data.toString())
                    val jsonObject: JSONObject = JSONObject(remoteMessage.data as Map<*, *>)
                    val notificationtitle: String = remoteMessage.data.get("title")!!
                    val notificationtype: String = remoteMessage.data.get("type")!!
                    val notificationmessage: String = remoteMessage.data.get("message")!!
                    if (remoteMessage.data.get("job_id") != null) {

                        JobId = remoteMessage.data.get("job_id")!!.toInt()
                    }

                    /*  val intent = Intent("sendnotify")
                      intent.putExtra("type", notificationtype)
                      intent.putExtra("message", notificationmessage)
                      LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)*/
                    PrepareNotifications(
                        notificationtitle,
                        notificationmessage,
                        notificationtype,
                        JobId
                    )
                    val jobintent = Intent("Notify_Job")
                    jobintent.putExtra("unread_job_request_count", jsonObject.optInt("unread_job_request_count"))
                    jobintent.putExtra("unread_notification_count", jsonObject.optInt("unread_notification_count"))
                    jobintent.putExtra("unread_workorder_count", jsonObject.optInt("unread_workorder_count"))
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(jobintent)
                    /*    var title: String = remoteMessage.notification!!.title!!
                var body: String = remoteMessage.notification!!.body!!*/

//                ShowNotifications(notificationtitle, notificationmessage, notificationtype, jobid)

                }
            } catch (e: Exception) {
                Log.e("Error", "=" + e.localizedMessage)
            }

        }


        super.onMessageReceived(remoteMessage)

    }

    fun PrepareNotifications(
        notificationtitle: String,
        notificationbody: String,
        notificationtype: String,
        Jobid: Int
    ) {
        val NOTIFICATION_ID = getRandomNumber(0, 999)
        var intent: Intent? = null

        when (notificationtype) {
            Content.JOB_ADDED -> {
                intent = Intent(this, JobDetailActivity::class.java)
                intent.putExtra(Content.JOB_ID, Jobid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(Intent(Content.JOB_ADDED).putExtra("message",Jobid))

            }
            Content.JOB_ACCEPTED -> {
                intent = Intent(this, JobDetailActivity::class.java)
                intent.putExtra(Content.JOB_ID, Jobid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            }
            Content.ENGINEER_REASSIGNED -> {
                intent = Intent(this, JobDetailActivity::class.java)
                intent.putExtra(Content.JOB_ID, Jobid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            }
            Content.JOB_DECLINED -> {
                intent = Intent(this, JobDetailActivity::class.java)
                intent.putExtra(Content.JOB_ID, Jobid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            }
            Content.JOB_INCOMPLETED -> {
                intent = Intent(this, JobDetailActivity::class.java)
                intent.putExtra(Content.JOB_ID, Jobid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            Content.JOB_STARTED -> {
                intent = Intent(this, JobDetailActivity::class.java)
                intent.putExtra(Content.JOB_ID, Jobid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            }
            Content.JOB_COMPLETED -> {
                intent = Intent(this, JobDetailActivity::class.java)
                intent.putExtra(Content.JOB_ID, Jobid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            }
            Content.ENGINEER_ASSIGNED -> {
                intent = Intent(this, JobDetailActivity::class.java)
                intent.putExtra(Content.JOB_ID, Jobid)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            }
            Content.PROFILE_UPDATED -> {
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("From","Notifyflag")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                getprofiledetails(TinyDb(this).getString(Content.USER_ID))
            }

            else -> {
                intent = Intent(this, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, NOTIFICATION_ID, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        ShowNotifications(pendingIntent, notificationtitle, notificationbody, NOTIFICATION_ID)

    }

    fun ShowNotifications(
        pendingIntent: PendingIntent,
        notificationtitle: String,
        notificationbody: String,
        notificationid: Int
    ) {

        /*   val intent = Intent(this, SplashActivity::class.java)
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
           intent.putExtra(Content.NOTIFICATION_TYPE, notificationtype)
           intent.putExtra(Content.JOB_ID, jobid)
           val pendingIntent: PendingIntent = PendingIntent.getActivity(
               this, 0, intent,
               PendingIntent.FLAG_ONE_SHOT
           )*/
        val channelId: String = getString(R.string.default_notification_channel_id)
        val defaultsounduri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val NotificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setContentTitle(notificationtitle)
                .setContentText(notificationbody)
                .setAutoCancel(true)
                .setSound(defaultsounduri)
                .setContentIntent(pendingIntent)
        val NotificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel = NotificationChannel(
                channelId, "Deafult Notification Channel",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            )
            NotificationManager.createNotificationChannel(channel)
        }
        NotificationManager.notify(notificationid, NotificationBuilder.build())
    }

    private fun getprofiledetails(userid: String) {
        val apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiInterface.getprofiledetails(
            userid
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                Log.e("Response_code", "=" + response.code())
                if (statuscode == 200) {

                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val UserRespone =
                            Gson().fromJson(data.toString(), UserRespone::class.java)

                        TinyDb(applicationContext).putString(
                            Content.USER_DATA,
                            data.toString()
                        )

                    } else {

                        Utility().showOkDialog(
                            applicationContext,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                } else if (statuscode == 403) {

                    Utility().showInactiveDialog(
                        applicationContext,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )


                } else {

                    Utility().showOkDialog(
                        applicationContext,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    applicationContext,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )

            }
        })
    }
    fun notifyChat(remoteMessage: RemoteMessage, context: Context?) {
        var grpId: String? = ""


        try {
            Log.e("Chat Notification Body:", remoteMessage.data.toString())
            val getData: Map<*, *> = remoteMessage.data
            var json = JSONObject(getData)
            grpId = json.optString("groupId")


            var notificationModel: NotificationModel? = null
            for (i in mApplicationClass.notificationModels.indices) {
                if (mApplicationClass.notificationModels[i].chatListModel != null) {
                    if (mApplicationClass.notificationModels[i].chatListModel?.id == grpId) {
                        notificationModel = mApplicationClass.notificationModels[i]
                        notificationModel.notificationCount = notificationModel.notificationCount + 1
                    }
                }

            }
            Log.e("grpId", "" + grpId)
            val myUid = mApplicationClass.getPreference()?.getString(Content.FIREBASE_UID)
            if (notificationModel == null) {
                val model = GroupListModel()
                model.id = grpId!!
                model.group_name = json.optString("group_name")
                notificationModel?.chatListModel = model
                showChatNotification(null, json, model)
//                showNotification(model, title, message, context, null)

            } else {
                showChatNotification(notificationModel, json, notificationModel.chatListModel!!)
//                showNotification(notificationModel.chatListModel, title, message, context, notificationModel)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun showChatNotification(notificationModel: NotificationModel?, jsonData: JSONObject, grouplistmodel: GroupListModel) {
        var title = "New Message in " + jsonData.optString("group_name") + " from " + jsonData.optString("sender_first_name")
        var message = ""
        var messageImage = ""
        if (jsonData.optString("messageType") == Content.CHAT_TEXT_TYPE) {
            message = jsonData.optString("message")
        } else {
            message =  jsonData.optString("sender_first_name")+" shared an image"
            messageImage = jsonData.optString("messageImage")
        }

        var notificationModel = notificationModel
        if (notificationModel == null) {
            notificationModel = NotificationModel()
            notificationModel.notificationCount = 1
            notificationModel.id = getRandomNumber(1, 999)
            notificationModel.message = message
            notificationModel.chatListModel = grouplistmodel
            notificationModel.title = title
            mApplicationClass.notificationModels.add(notificationModel)
        } else {
            notificationModel.title = title

        }
        val nManager = mContext?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder
        val idChannel = "Chats"
        val notificationIntent = Intent(mContext, ChatActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        notificationIntent.putExtra("chat_group", Gson().toJson(grouplistmodel))
        notificationIntent.putExtra("isFromNotifications", true)
        val contentIntent = PendingIntent.getActivity(mContext, notificationModel.id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH

            val mChannel = NotificationChannel(idChannel, mContext.getString(R.string.app_name), importance)
            mChannel.description = "Chat Messages"
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(500, 500, 500, 500, 500)
            nManager.createNotificationChannel(mChannel)
            builder = NotificationCompat.Builder(mContext!!, idChannel)
        } else {
            builder = NotificationCompat.Builder(mContext)
        }
        builder.setContentTitle(notificationModel.title)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setVibrate(longArrayOf(500, 500, 500, 500, 500))
        builder.setSound(uri)
        builder.setContentIntent(contentIntent)
        if (notificationModel.notificationCount === 1) {
            builder.setContentText(message)
            notificationModel.inboxStyle.addLine(message)
        } else {
            //builder.setContentText("You have " + notificationModel.getNotificationCount() + "" + " " + "messages");
            builder.setContentText(notificationModel.notificationCount.toString() + " " + "messages")
            notificationModel.inboxStyle.addLine(message)
            builder.setStyle(notificationModel.inboxStyle)
        }
        builder.setSmallIcon(R.drawable.ic_notification_small)
        builder.setAutoCancel(true)
        val notificationManager = mContext?.applicationContext?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationModel.id)
        nManager.notify(mContext.getString(R.string.app_name), notificationModel.id, builder.build())
    }
    fun getRandomNumber(min: Int, max: Int): Int {
        val rand = Random()
        return rand.nextInt(max - min + 1) + min
    }

}