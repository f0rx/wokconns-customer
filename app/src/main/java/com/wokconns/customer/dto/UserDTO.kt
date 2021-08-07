package com.wokconns.customer.dto

import com.wokconns.customer.interfaces.Const
import java.io.Serializable

class UserDTO : Serializable {
    var user_id = ""
    var name = ""
    var email_id = ""
    var password = ""
    var image = ""
        get() = Const.DOMAIN_URL + field
    var address = ""
    var office_address = ""
    var live_lat = ""
    var live_long = ""
    var role = ""
    var status = ""
    var approval_status = ""
    var created_at = ""
    var mobile = ""
    var referral_code = ""
    var user_referral_code = ""
    var gender = ""
    var city = ""
    var country = ""
    var updated_at = ""
    var device_type = ""
    var device_id = ""
    var device_token = ""

    constructor()
    constructor(
        user_id: String,
        name: String,
        email_id: String,
        password: String,
        image: String,
        address: String,
        office_address: String,
        live_lat: String,
        live_long: String,
        role: String,
        status: String,
        approval_status: String,
        created_at: String,
        mobile: String,
        referral_code: String,
        user_referral_code: String,
        gender: String,
        city: String,
        country: String,
        updated_at: String,
        device_type: String,
        device_id: String,
        device_token: String
    ) {
        this.user_id = user_id
        this.name = name
        this.email_id = email_id
        this.password = password
        this.image = image
        this.address = address
        this.office_address = office_address
        this.live_lat = live_lat
        this.live_long = live_long
        this.role = role
        this.status = status
        this.approval_status = approval_status
        this.created_at = created_at
        this.mobile = mobile
        this.referral_code = referral_code
        this.user_referral_code = user_referral_code
        this.gender = gender
        this.city = city
        this.country = country
        this.updated_at = updated_at
        this.device_type = device_type
        this.device_id = device_id
        this.device_token = device_token
    }
}