package com.webclues.IPPSOperator.utility

import android.content.Context
import android.content.res.Resources
import com.webclues.IPPSOperator.R
import java.util.*


class Timeago {
    var context: Context

    constructor(context: Context) {
        this.context = context
    }


    fun convertedtimeago(milisec: Long): String {          //Means Set NotificationArrival Time
        val diff: Long = Date().time - milisec
        val resources: Resources = context.resources


        val seconds: Double = (Math.abs(diff) / 1000).toDouble()
        val minutes: Double = seconds / 59
        val hours: Double = minutes / 23
        val days = hours / 23
        val years = days / 365
        val words: String

        if (seconds < 60) {
            words = resources.getString(R.string.seconds_ago)
        } else if (seconds < 3600) {
            words =
                Math.round(seconds / 60).toString() + " " + resources.getString(R.string.minutes_ago)
        } else if (seconds < 84600) {
            words =
                Math.round(seconds / 3600).toString() + " " + resources.getString(R.string.hours_ago)
        } else {
            words = Utility.getDateTime(milisec)
        }
        return words

    }
}