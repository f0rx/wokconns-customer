package com.wokconns.customer.interfaces

/**
 * Created by VARUN on 01/01/19.
 */
interface Const {
    companion object {
        /*app data*/
        const val INTROAPP = "INTROAPP"
        const val mBroadcastShowAdd = "Wokconns.showAdd"

        // Google Console APIs developer key
        // Replace this key with your's
        const val DEVELOPER_KEY = "AIzaSyBlLIsCaCw8ylCTPR0XhaKp-vkeD4S-5_0"
        const val APP_NAME = "Wokconns"
        const val DOMAIN_URL = "https://wms.wokconns.com/"
        const val BASE_URL = DOMAIN_URL + "Webservice/"
        const val PAYMENT_FAIL = "payment_failed"
        const val PAYMENT_SUCCESS = "payment_success"
        const val PRIVACY_URL = "privacyPolicy"
        const val FAQ_URL = "faq"
        const val TERMS_URL = "termsCondition"

        /*Api Details*/
        const val GET_ALL_ARTISTS_API = "getAllArtists"
        const val GET_ARTIST_BY_ID_API = "getArtistByid"
        const val GET_NOTIFICATION_API = "getNotifications"
        const val GET_INVOICE_API = "getMyInvoice"
        const val GET_REFERRAL_CODE_API = "getMyReferralCode"
        const val GET_CHAT_HISTORY_API = "getChatHistoryForUser"
        const val GET_CHAT_API = "getChat"
        const val SEND_CHAT_API = "sendmsg"
        const val LOGIN_API = "signIn"
        const val REGISTER_API = "SignUp"
        const val UPDATE_PROFILE_API = "editPersonalInfo"
        const val CURRENT_BOOKING_API = "getMyCurrentBookingUser"
        const val BOOK_ARTIST_API = "book_artist"
        const val BOOK_APPOINTMENT_API = "book_appointment"
        const val DECLINE_BOOKING_API = "decline_booking"
        const val UPDATE_LOCATION_API = "updateLocation"
        const val MAKE_PAYMENT_API = "makePayment"
        const val CHECK_COUPON_API = "checkCoupon"
        const val GET_MY_TICKET_API = "getMyTicket"
        const val GENERATE_TICKET_API = "generateTicket"
        const val GET_TICKET_COMMENTS_API = "getTicketComments"
        const val ADD_TICKET_COMMENTS_API = "addTicketComments"
        const val FORGET_PASSWORD_API = "forgotPassword"
        const val GET_APPOINTMENT_API = "getAppointment"
        const val EDIT_APPOINTMENT_API = "edit_appointment"
        const val APPOINTMENT_OPERATION_API = "appointment_operation"
        const val GET_ALL_CATEGORY_API = "getAllCaegory"
        const val GET_ALL_JOB_USER_API = "get_all_job_user"
        const val POST_JOB_API = "post_job_new"
        const val GET_APPLIED_JOB_BY_ID_API = "get_applied_job_by_id"
        const val JOB_STATUS_USER_API = "job_status_user"
        const val VERIFY_PHONE = "verifyMobile"
        const val RESEND_VERIFY_OTP_CODE = "resendMobileOtp"
        const val EDIT_POST_JOB_API = "edit_post_job"
        const val DELETE_JOB_API = "deletejob"
        const val ADD_FAVORITES_API = "add_favorites"
        const val REMOVE_FAVORITES_API = "remove_favorites"
        const val GET_LOCATION_ARTIST_API = "getLocationArtist"
        const val ADD_RATING_API = "addRating"
        const val BOOKING_OPERATION_API = "booking_operation"
        const val JOB_COMPLETE_API = "jobComplete"
        const val DELETE_PROFILE_IMAGE_API = "deleteProfileImage"
        const val ADD_MONEY_API = "addMoney"
        const val GET_WALLET_HISTORY_API = "getWalletHistory"
        const val GET_USER_WALLET_API = "getUserWallet"
        const val GET_WALLET_API = "getWallet"
        const val CUSTOMER_HOME_DATA = "customerHomeData"
        const val GET_CURRENCY_API = "getCurrency"
        const val GET_ALL_ARTIST_FILTER = "getAllArtistsFilter"
        const val RECORD_JOB_CANCELLATION = "recordJobCancellation"
        const val CAMERA_ACCEPTED = "camera_accepted"
        const val STORAGE_ACCEPTED = "storage_accepted"
        const val MODIFY_AUDIO_ACCEPTED = "modify_audio_accepted"
        const val CALL_PRIVILAGE = "call_privilage"
        const val FINE_LOC = "fine_loc"
        const val CORAS_LOC = "coras_loc"
        const val CALL_PHONE = "call_phone"
        const val PAYMENT_URL = "payment_url"
        const val JOB_PRICE = "job_price"
        const val SURL = "surl"
        const val FURL = "furl"
        const val SCREEN_TAG = "screen_tag"
        const val SERVICE_ARRAY = "service_array"
        const val SERVICE_NAME_ARRAY = "service_name_array"
        const val DTO = "dto"
        const val POSTION = "postion"
        const val UPDATE_PROFILE_IMAGE = "updateProfileImage"
        const val SURL_BOOKING = "surl_booking"
        const val FURL_BOOKING = "furl_booking"

        /*app data*/ /*Project Parameter*/
        const val ARTIST_ID = "artist_id"
        const val CHAT_LIST_DTO = "chat_list_dto"
        const val USER_DTO = "user_dto"
        const val POST_JOB_DTO = "post_job_dto"
        const val IS_REGISTERED = "is_registered"
        const val IMAGE_URI_CAMERA = "image_uri_camera"
        const val DATE_FORMATE_SERVER = "EEE, MMM dd, yyyy hh:mm a" //Wed, JUL 06, 2018 04:30 pm
        const val DATE_FORMATE_TIMEZONE = "z"
        const val HISTORY_DTO = "history_dto"
        const val BROADCAST = "broadcast"
        const val ARTIST_DTO = "artist_dto"
        const val FLAG = "flag"

        /*Parameter Get Artist and Search*/
        const val USER_ID = "user_id"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val CATEGORY_ID = "category_id"

        /*Get All History*/
        const val ROLE = "role"

        /*Send Message*/
        const val MESSAGE = "message"
        const val SEND_BY = "send_by"
        const val SENDER_NAME = "sender_name"

        /*Login Parameter*/
        const val NAME = "name"
        const val EMAIL = "email"
        const val EMAIL_ID = "email_id"
        const val OTP_CODE = "otp"
        const val PASSWORD = "password"
        const val DEVICE_TYPE = "device_type"
        const val DEVICE_TOKEN = "device_token"
        const val DEVICE_ID = "device_id"
        const val REFERRAL_CODE = "referral_code"

        /*Update Profile*/
        const val NEW_PASSWORD = "new_password"
        const val GENDER = "gender"
        const val MOBILE = "mobile"
        const val MOBILE_NUMBER = "mobile_no"
        const val OFFICE_ADDRESS = "office_address"
        const val ADDRESS = "address"
        const val IMAGE = "image"
        const val CITY = "city"
        const val COUNTRY = "country"

        /*Book Artist*/
        const val DATE_STRING = "date_string"
        const val TIMEZONE = "timezone"
        const val PRICE = "price"
        const val PLACE = "place"
        const val ESTIMATE_TIME = "estimate_time"
        const val SERVICE_ID = "service_id"

        /*Decline*/
        const val BOOKING_ID = "booking_id"
        const val DECLINE_BY = "decline_by"
        const val DECLINE_REASON = "decline_reason"

        /*Make Payment*/
        const val INVOICE_ID = "invoice_id"

        // String USER_ID = "user_id";
        const val COUPON_CODE = "coupon_code"
        const val FINAL_AMOUNT = "final_amount"
        const val PAYMENT_STATUS = "payment_status"

        /*Chat intent*/
        const val ARTIST_NAME = "artist_name"

        /*Add Ticket*/
        const val REASON = "reason"

        /*Get Ticket*/
        const val TICKET_ID = "ticket_id"
        const val COMMENT = "comment"

        /*Edit Appointment*/
        const val APPOINTMENT_ID = "appointment_id"

        /*Decline Appointment*/
        const val REQUEST = "request"

        /*Post Job*/
        const val AVTAR = "avtar"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val LATI = "lati"
        const val LONGI = "longi"
        const val JOB_DATE = "job_date"

        /*Get Applied Job*/
        const val JOB_ID = "job_id"
        const val aj_id = "aj_id"

        /*Job Status*/
        const val AJ_ID = "aj_id"
        const val STATUS = "status"

        /*Payment*/
        const val PAYMENT_TYPE = "payment_type"
        const val DISCOUNT_AMOUNT = "discount_amount"

        /*Chat*/
        const val CHAT_TYPE = "chat_type"

        /*Paypal Client Id*/ /*Add Review*/
        const val RATING = "rating"

        /*Add Money*/
        const val TXN_ID = "txn_id"
        const val ORDER_ID = "order_id"
        const val AMOUNT = "amount"
        const val CURRENCY = "currency"

        /*Home*/
        const val DISTANCE = "distance"
        const val CURRENCY_ID = "currency"

        /*Notifications Codes*/
        const val BOOK_ARTIST_NOTIFICATION = "10001" //ar
        const val DECLINE_BOOKING_ARTIST_NOTIFICATION = "10002" //both
        const val START_BOOKING_ARTIST_NOTIFICATION = "10003"
        const val END_BOOKING_ARTIST_NOTIFICATION = "10004" //user
        const val CANCEL_BOOKING_ARTIST_NOTIFICATION = "10005"
        const val ACCEPT_BOOKING_ARTIST_NOTIFICATION = "10006" //user
        const val CHAT_NOTIFICATION = "10007" //both
        const val USER_BLOCK_NOTIFICATION = "1008"
        const val TICKET_COMMENT_NOTIFICATION = "10009" //both
        const val WALLET_NOTIFICATION = "10010" //both
        const val JOB_NOTIFICATION = "10011" //ar
        const val JOB_APPLY_NOTIFICATION = "10012" //user
        const val DELETE_JOB_NOTIFICATION = "10013" //ar
        const val BRODCAST_NOTIFICATION = "10014" //both
        const val TICKET_STATUS_NOTIFICATION = "10015" //both
        const val ADMIN_NOTIFICATION = "10016"
        const val TYPE = "type"
        const val TOPIC_CUSTOMER = "Wokconns"
        const val LANGUAGE_SELECTION = "language_selection"
        const val VOICE_PREFERENCE = "voice_preference"
        const val VOICE_PREFERENCE_ENGLISH = "en"
        const val VOICE_PREFERENCE_ARABIC = "ar"
        const val LANGUAGE = "Language"
        const val ENGLISH_TAG = "en"
        const val ARABIC_TAG = "ar"

        //webView
        const val URL = "url"
        const val HEADER = "header"
        const val VALUE = "value"
    }
}