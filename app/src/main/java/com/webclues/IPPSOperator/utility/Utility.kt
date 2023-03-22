package com.webclues.IPPSOperator.utility

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.provider.Settings
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.gson.Gson
import com.webclues.IPPSOperator.Modelclass.UserRespone
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.activity.LoginActivity
import com.webclues.IPPSOperator.view.MaterialAutoCompleteTextView
import com.webclues.IPPSOperator.view.MaterialEditText
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Utility {
    companion object {


        fun ValidateEmailTask(email: String): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            val EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            pattern = Pattern.compile(EMAIL_PATTERN)
            matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun convertDpToPixel(context: Context, sizeInDp: Int): Int {
            val scale = context.resources.displayMetrics.density
            return (sizeInDp * scale + 0.5f).toInt()
        }

        fun closeKeyboard(activity: Activity) {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

        }

        fun isValidPassword(s: String): Boolean {
            val PASSWORD_PATTERN = Pattern.compile(
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d\$@\$!%*#?&]{8,30}$"
            )

            return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches()
        }


        fun bitmapToFile(
            bitmap: Bitmap?,
            addProfileImageScreen: Activity
        ): Any {
            val wrapper = ContextWrapper(addProfileImageScreen)

            // Initialize a new file instance to save bitmap object
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file, "${UUID.randomUUID()}.jpg")

            try {
                // Compress the bitmap and save in jpg format
                val stream: OutputStream = FileOutputStream(file)
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // Return the saved bitmap uri
            return Uri.parse(file.absolutePath)
        }

        fun hideSoftKeyBoard(context: Context) {
            try {
                val v = (context as Activity).currentFocus
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v!!.windowToken, 0)
            } catch (e: Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }
        }

        fun showSiftKeyBoard(context: Context, view: View) {
            val v = (context as Activity).currentFocus
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.SHOW_FORCED, 0
            )
        }


        fun deviceID(context: Context): String {
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }





        fun checkIsMobileValid(string: String): Boolean {
            val contactPattern = "[0-9]+"
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            var reply = false
            //        if (string.matches(contactPattern)) {
            reply = !(string.length < 8 || string.length > 14)
            //        }
            return reply
        }

        fun getDate(date_string: Long): String? {
            var datetime = ""
            if (date_string != 0L) {
                val date_ =
                    Date(date_string) // *1000 is to convert seconds to milliseconds
                val dateformat =
                    SimpleDateFormat("dd MMM yyyy")
                datetime = dateformat.format(date_)
            }
            return datetime
        }

        fun getTime(date_string: Long): String? {
            var datetime = ""
            if (date_string != 0L) {
                val date_ =
                    Date(date_string) // *1000 is to convert seconds to milliseconds
                val dateformat =
                    SimpleDateFormat("hh:mm a")
                datetime = dateformat.format(date_)
            }
            return datetime
        }


        fun getDatewithTimestamp(time: Long): String {
            val date = Date(time)
            val sdf = SimpleDateFormat(Content.DATE_FORMAT, Locale.ENGLISH)

            return sdf.format(date)
        }

        fun getDateTime(timestamp: Long): String {
            try {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timestamp
                val sdf = SimpleDateFormat(Content.TAG_DateTimeFormate, Locale.getDefault())
                val currenTimeZone = calendar.time as Date
                return sdf.format(currenTimeZone)
            } catch (e: Exception) {
            }

            return ""
        }

        fun hideKeyboard(ctx: Context) {
            val inputManager =
                ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // check if no ic_view has focus:
            val v = (ctx as Activity).currentFocus ?: return
            inputManager.hideSoftInputFromWindow(v.windowToken, 0)
        }

        fun showKeyboard(ctx: Context) {
            val imm =
                ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        var CharachterFilter =
            InputFilter { cs, start, end, spanned, dStart, dEnd ->
                // TODO Auto-generated method stub
                if (cs == "") { // for backspace
                    return@InputFilter cs
                }
                if (cs.toString().matches(Regex("[a-zA-Z ]+"))) {
                    cs
                } else ""
            }
    }

    lateinit var context: Context
    fun Utility() {
        this.context = context
    }

    fun deviceWidth(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    fun deviceHeight(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.y
    }

    fun showOkDialog(context: Context, stitle: String, smessage: String) {  //Success dialogue

        val dialog = Dialog(context)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.rectangle_background)
        dialog.setContentView(R.layout.popup_ok)
        dialog.show()

        val title = dialog.findViewById(R.id.txtTitle) as TextView
        val text_message = dialog.findViewById(R.id.txtMessage) as TextView
        val text_yes = dialog.findViewById(R.id.txtOk) as TextView
        title.text = stitle
        text_message.text = smessage
        text_yes.text = context.resources.getString(R.string.ok)
        text_yes.setOnClickListener({
            dialog.dismiss() })
    }

    fun showInactiveDialog(context: Context, stitle: String, smessage: String) {   //Token Expire and delete user admin to show dialogue

        val dialog = Dialog(context)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.rectangle_background)
        dialog.setContentView(R.layout.popup_ok)
        dialog.show()

        val title = dialog.findViewById(R.id.txtTitle) as TextView
        val text_message = dialog.findViewById(R.id.txtMessage) as TextView
        val text_yes = dialog.findViewById(R.id.txtOk) as TextView
        title.text = stitle
        text_message.text = smessage
        text_yes.text = context.resources.getString(R.string.ok)
        text_yes.setOnClickListener({
            TinyDb(context).clear(context)
            context.startActivity(Intent(context, LoginActivity::class.java))
            (context as Activity).finish()
        })
    }


    fun getNotificationArrivalTime(
        notification_time: Long,
        context: Context
    ): String? {
        val calendar = Calendar.getInstance()
        var timeComment = ""
        val current_time = calendar.timeInMillis
        var time_interval: Long = 0
        time_interval = current_time - notification_time
        Log.e("Time Curnt", "" + current_time)
        Log.e("Time Notif", "" + notification_time)
        Log.e("Time intrv", "" + time_interval)
        if (time_interval <= 60) { //if notification was created within a minute
            timeComment = if (time_interval < 60) {
                time_interval.toString() + " " + context.resources.getString(R.string.seconds_ago)
            } else {
                context.resources.getString(R.string.a_minute_ago)
            }
        } else if (time_interval <= 3600) { //if notification was created within an hour
            val minutes: Long =
                calculateMinutesInSeconds(
                    time_interval
                )
            timeComment = if (minutes < 60) {
                minutes.toString() + " " + context.resources.getString(R.string.minutes_ago)
                /*   if (minutes == 1) {
                            timeComment = context.getResources().getString(R.string.a_minute_ago);
                        } else {
                            timeComment = minutes + " " + context.getResources().getString(R.string.minutes_ago);
                        }*/
            } else {
                context.resources.getString(R.string.an_hour_ago)
            }
        } else if (time_interval <= 86400) { //if notification was created within a Day
            val hours: Long =
                calculateHoursInMinutes(
                    calculateMinutesInSeconds(
                        time_interval
                    )
                )
            timeComment = if (hours < 24) {
                if (hours == 1L) {
                    context.resources.getString(R.string.an_hour_ago)
                } else {
                    hours.toString() + " " + context.resources.getString(R.string.hours_ago)
                }
            } else {
                context.resources.getString(R.string.a_day_ago)
            }
        } else if (time_interval <= 604800) { //if notification was created within a week
            val days: Long =
                calculateDaysInHours(
                    calculateHoursInMinutes(
                        calculateMinutesInSeconds(
                            time_interval
                        )
                    )
                )
            timeComment = if (days < 7) {
                if (days == 1L) {
                    context.resources.getString(R.string.a_day_ago)
                } else {
                    days.toString() + " " + context.resources.getString(R.string.days_ago)
                }
            } else {
                context.resources.getString(R.string.a_week_ago)
            }
        } else if (time_interval <= 2592000) { //if notification was created within a month
            val weeks: Long =
                calculateWeeksInDays(
                    calculateDaysInHours(
                        calculateHoursInMinutes(
                            calculateMinutesInSeconds(
                                time_interval
                            )
                        )
                    )
                )
            timeComment = if (weeks < 7) {
                if (weeks == 1L) {
                    context.resources.getString(R.string.a_week_ago)
                } else {
                    weeks.toString() + " " + context.resources.getString(R.string.weeks_ago)
                }
            } else {
                context.resources.getString(R.string.a_month_ago)
            }
        } else if (time_interval <= 31536000) { //if notification was created within a year
            val month: Long =
                calculateMonthsInWeeks(
                    calculateWeeksInDays(
                        calculateDaysInHours(
                            calculateHoursInMinutes(
                                calculateMinutesInSeconds(
                                    time_interval
                                )
                            )
                        )
                    )
                )
            if (month < 12) {
                if (month == 1L) {
                    timeComment = context.resources.getString(R.string.a_month_ago)
                } else {
                    timeComment =
                        month.toString() + context.resources.getString(R.string.months_ago)

                }
            } else {
                timeComment = context.resources.getString(R.string.a_year_ago)
            }
        } else {
            val years: Long =
                calculateMonthsInWeeks(
                    calculateWeeksInDays(
                        calculateDaysInHours(
                            calculateHoursInMinutes(
                                calculateMinutesInSeconds(
                                    time_interval
                                )
                            )
                        )
                    )
                ) / 12
            if (years == 1L) {
                timeComment = context.resources.getString(R.string.a_year_ago)
            } else {

                timeComment = years.toString() + context.resources.getString(R.string.years_ago)
            }
        }
        return timeComment
    }

    private fun calculateMinutesInSeconds(seconds: Long): Long {
        return Math.round(seconds / 60.toFloat()).toLong()
    }

    private fun calculateHoursInMinutes(minutes: Long): Long {
        return Math.round(minutes / 60.toFloat()).toLong()
    }

    private fun calculateDaysInHours(hours: Long): Long {
        return Math.round(hours / 24.toFloat()).toLong()
    }

    private fun calculateWeeksInDays(days: Long): Long {
        return Math.round(days / 7.toFloat()).toLong()
    }

    private fun calculateMonthsInWeeks(weeks: Long): Long {
        return Math.round(weeks / 4.2)
    }




    fun isValidFirstName(context: Context, editText: MaterialEditText): Boolean {  //FirstName Validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_empty_firstName)
            isValid = false
        } else if (editText.text!!.trim().length < 2) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_invalid_firstName)
            isValid = false
        }
        return isValid
    }

    fun isValidLastName(context: Context, editText: MaterialEditText): Boolean {  //LastName validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_empty_lastName)
            isValid = false
        } else if (editText.text!!.trim().length < 2) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_invalid_lastName)
            isValid = false
        }
        return isValid
    }

    fun isGeneralValidation(context: Context, editText: MaterialEditText): Boolean {

        var isValid = true
        if (editText.text!!.isEmpty()) {
//            editText.requestFocus()
            editText.error = context.resources.getString(R.string.str_empty_lastName)
            isValid = false
        }
        return isValid
    }

    fun isEmailValid(context: Context, editText: MaterialEditText): Boolean {  //Email Validation
        var isValid = true

        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_email_empty)
//            editText.requestFocus()
            isValid = false
        } else {
            val pattern: Pattern
            val matcher: Matcher
            val EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            pattern = Pattern.compile(EMAIL_PATTERN)
            matcher = pattern.matcher(editText.text)
            if (!matcher.matches()) {
                isValid = false
                editText.error = context.resources.getString(R.string.str_val_emal)
//                editText.requestFocus()
            } else {
                isValid = true
            }

        }
        return isValid
    }

    fun isPhoneValid(context: Context, editText: MaterialEditText): Boolean {   //PhoneNumber Validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_phone_empty)
//            editText.requestFocus()
            isValid = false
        } else if (editText.text!!.length < 8) {
            editText.error = context.resources.getString(R.string.str_val_phone_valid)
//            editText.requestFocus()
            isValid = false
        }
        return isValid
    }

    fun isPositionValid(context: Context, editText: MaterialAutoCompleteTextView): Boolean {  //operatorposition Validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_position)
//            editText.requestFocus()
            isValid = false
        }
        return isValid
    }

    fun isCompanyValid(context: Context, editText: MaterialAutoCompleteTextView): Boolean {  //Company validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_company)
//            editText.requestFocus()
            isValid = false
        }
        return isValid
    }

    fun isCurrentPasswordValid(context: Context, editText: MaterialEditText): Boolean {  //Currentpassword validation
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_current_pass_empty)
//            editText.requestFocus()
            return false
        } else if (!editText.text.isNullOrEmpty() && editText.text!!.length < 6 || editText.text!!.length > 15) {
            editText.error = context.resources.getString(R.string.str_val_pass_length)
//            editText.requestFocus()
            return false
        } else {
            return true
        }

    }


    fun isPasswordValid(context: Context, editText: MaterialEditText): Boolean {  //Password validation

        var isValid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.str_val_pass_empty)
//            editText.requestFocus()
            isValid = false
        } else if (!editText.text.isNullOrEmpty() && editText.text!!.length < 6 || editText.text!!.length > 15) {
            editText.error = context.resources.getString(R.string.str_val_pass_length)
//            editText.requestFocus()
            isValid = false
        }
        return isValid
    }


    fun isPassvalid(
        context: Context,
        edtPass: MaterialEditText
    ): Boolean {
        var isValid = true


        if (edtPass.text!!.isEmpty()) {
            edtPass.error = context.resources.getString(R.string.str_val_new_pass_empty)
//            edtPass.requestFocus()
            isValid = false
        } else if (!edtPass.text.isNullOrEmpty() && edtPass.text!!.length < 6 || edtPass.text!!.length > 15) {
            edtPass.error = context.resources.getString(R.string.str_val_newpass_length)
//            edtPass.requestFocus()
            isValid = false
        }

        return isValid
    }

    fun isConfirmPassvalid(                            //ConfirmPassword validation
        context: Context, edtnewpass: MaterialEditText,
        edtConfPass: MaterialEditText
    ): Boolean {
        var isValid = true
        if (edtConfPass.text!!.isEmpty()) {
            edtConfPass.error = context.resources.getString(R.string.str_val_confirm_pass_empty)
//            edtPass.requestFocus()
            isValid = false
        } else if (!edtConfPass.text.isNullOrEmpty() && edtConfPass.text!!.length < 6 || edtConfPass.text!!.length > 15) {
            edtConfPass.error = context.resources.getString(R.string.str_val_conf_pass_length)
//            edtConfPass.requestFocus()
            isValid = false
        } else if (!edtnewpass.text.toString().trim().equals(edtConfPass.text.toString().trim())) {
            edtConfPass.error = context.resources.getString(R.string.str_val_pass_conf_match)
            isValid = false
        }
        return isValid
    }


    fun isSubjectValid(context: Context, editText: MaterialEditText): Boolean {  //Subject Validation
        var isvalid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.error_subject_blank)
//            editText.requestFocus()
            isvalid = false
        }
        return isvalid
    }


    fun isMessagevalid(context: Context, editText: MaterialEditText): Boolean {  //Message validation
        var isvalid = true
        if (editText.text!!.isEmpty()) {
            editText.error = context.resources.getString(R.string.error_message_blank)
//            editText.requestFocus()
            isvalid = false
        }
        return isvalid

    }

    fun getCompany_ID(context: Context):String{
        val userData = Gson().fromJson(TinyDb(context).getString(Content.USER_DATA), UserRespone::class.java)
        var id = ""
        if(userData.company.company_id==0 && TinyDb(context).getString(Content.USER_DATA).equals(TinyDb.DEFULT_STRING)){
            id="0"
        }else
        {
            id=userData.company.company_id.toString()
        }
       return id
    }
}