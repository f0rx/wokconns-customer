package com.wokconns.customer.interfaces

import org.json.JSONObject

interface Helper {
    fun backResponse(flag: Boolean, msg: String?, response: JSONObject?)
}