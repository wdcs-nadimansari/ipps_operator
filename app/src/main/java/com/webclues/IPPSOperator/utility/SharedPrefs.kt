package com.webclues.IPPSOperator.utility

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs {

    private var mContext: Context? = null
    var sharedPreferences: SharedPreferences? = null

    companion object {
        val DEFULT_STRING = "-1"
    }

    constructor(mContext: Context?) {
        this.mContext = mContext
        this.sharedPreferences =
            mContext?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }


    fun getValue(key: String?): String? {
        return sharedPreferences?.getString(key, DEFULT_STRING)
    }

    fun setValue(key: String?, value: String?) {
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun getFloat(key: String?): Float? {
        return sharedPreferences?.getFloat(key, 0.0.toFloat())
    }

    fun setFloat(key: String?, value: Float) {
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.putFloat(key, value)
        editor?.apply()
    }

    fun getBoolean(key: String?): Boolean? {
        return sharedPreferences?.getBoolean(key, false)
    }

    fun setBoolean(key: String?, value: Boolean) {
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun getLong(key: String?): Long? {
        return sharedPreferences?.getLong(key, 0)
    }

    fun setLong(key: String?, value: Long) {
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.putLong(key, value)
        editor?.apply()
    }


    fun getInt(key: String?): Int? {
        return sharedPreferences?.getInt(key, 0)
    }

    fun setInt(key: String?, value: Int) {
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.putInt(key, value)
        editor?.apply()
    }

    fun clearAll() {
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.clear()
        editor?.apply()
    }
}