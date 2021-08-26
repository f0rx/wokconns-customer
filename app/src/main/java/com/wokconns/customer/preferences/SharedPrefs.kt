package com.wokconns.customer.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wokconns.customer.dto.UserDTO
import com.wokconns.customer.interfaces.Const

class SharedPrefs private constructor() {
    fun clearAllPreferences() {
        prefsEditor = myPrefs.edit()
        prefsEditor.clear()
        prefsEditor.commit()
    }

    fun clearPreferences(key: String?) {
        prefsEditor.remove(key)
        prefsEditor.commit()
    }

    fun setIntValue(Tag: String?, value: Int) {
        prefsEditor.putInt(Tag, value)
        prefsEditor.apply()
    }

    fun getIntValue(Tag: String?): Int {
        return myPrefs.getInt(Tag, 0)
    }

    fun setLongValue(Tag: String?, value: Long) {
        prefsEditor.putLong(Tag, value)
        prefsEditor.apply()
    }

    fun getLongValue(Tag: String?): Long {
        return myPrefs.getLong(Tag, 0)
    }

    fun setValue(Tag: String?, token: String?) {
        prefsEditor.putString(Tag, token)
        prefsEditor.commit()
    }

    fun getValue(Tag: String): String? {
        if (Tag.equals(Const.LATITUDE, ignoreCase = true)) return myPrefs.getString(Tag,
            "22.7497853") else if (Tag.equals(
                Const.LONGITUDE, ignoreCase = true)
        ) return myPrefs.getString(Tag, "75.8989044")
        return myPrefs.getString(Tag, "")
    }

    fun getBooleanValue(Tag: String?): Boolean {
        return myPrefs.getBoolean(Tag, false)
    }

    fun setBooleanValue(Tag: String?, token: Boolean) {
        prefsEditor.putBoolean(Tag, token)
        prefsEditor.commit()
    }

    fun setParentUser(userDTO: UserDTO?, tag: String?) {
        val gson = Gson()
        val hashMapString = gson.toJson(userDTO)
        prefsEditor.putString(tag, hashMapString)
        prefsEditor.apply()
    }

    fun getParentUser(tag: String?): UserDTO {
        val obj = myPrefs.getString(tag, "defValue")
        return if (obj == "defValue") {
            UserDTO()
        } else {
            val gson = Gson()
            val storedHashMapString =
                myPrefs.getString(tag, "")
            val type = object : TypeToken<UserDTO?>() {}.type
            gson.fromJson(storedHashMapString, type)
        }
    }

    companion object {
        lateinit var myPrefs: SharedPreferences
        lateinit var prefsEditor: SharedPreferences.Editor
        var myObj: SharedPrefs? = null

        @JvmStatic
        fun getInstance(ctx: Context?): SharedPrefs? {
            if (myObj == null) {
                myObj = SharedPrefs()
                myPrefs = PreferenceManager.getDefaultSharedPreferences(ctx)
                prefsEditor = myPrefs.edit()
            }
            return myObj
        }
    }
}