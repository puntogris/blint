package com.puntogris.blint.utils

object Constants {
    const val ARG_SCANNING_RESULT = "scanning_result"
    const val ALL_PRODUCTS_CARD_CODE = 101
    const val ALL_CLIENTS_CARD_CODE = 102
    const val ALL_SUPPLIERS_CARD_CODE = 103
    const val ACCOUNTING_CARD_CODE = 104
    const val RECORDS_CARD_CODE = 105
    const val CHARTS_CARD_CODE = 106
    const val TOOLS_CARD_CODE = 107
    const val DEB_CARD_CODE = 108
    const val ACCOUNT_CARD_CODE = 109

    const val WEB_CLIENT_ID = "284997358263-0gsjvjo373578qmch4sgbu8r6408vtjl.apps.googleusercontent.com"
    const val FIX_GOOGLE_PLAY_SERVICES_URL = "https://support.google.com/googleplay/answer/9037938"
    const val SUPPORT_EMAIL = "ayuda@blint.app"

    const val APP_PLAY_STORE_URI= "https://play.google.com/store/apps/details?id=com.puntogris.blint"
    const val PLAY_STORE_PACKAGE= "com.android.vending"
    const val PRIVACY_POLICY_URI = "https://blint.app/privacy-policy.html"
    const val TERMS_AND_CONDITIONS_URI = "https://blint.app/terms-and-conditions.html"


    const val SUPPLIERS_LIST = 101
    const val CLIENTS_LIST = 102
    const val PRODUCTS_LIST = 103
    const val SUPPLIERS_RECORDS = 104
    const val CLIENTS_RECORDS = 105
    const val PRODUCTS_RECORDS = 106

    //Types for notifications adapter on app and server side
    const val ADVERTISEMENT_NOTIFICATION = 1
    const val ADVERTISEMENT_NOTIFICATION_SERVER = "ADVERTISEMENT_NOTIFICATION"
    const val EMPLOYMENT_REQUEST_SENT_NOTIFICATION = 2
    const val EMPLOYMENT_REQUEST_SENT_NOTIFICATION_SERVER = "EMPLOYMENT_REQUEST_SENT_NOTIFICATION"
    const val EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION = 3
    const val EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION_SERVER = "EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION"
    const val EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION = 4
    const val EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION_SERVER = "EMPLOYMENT_REQUEST_OWNER_RESPONSE_NOTIFICATION"
    const val EMPLOYMENT_REQUEST_EMPLOYEE_RESPONSE_NOTIFICATION = 5
    const val EMPLOYMENT_REQUEST_EMPLOYEE_RESPONSE_NOTIFICATION_SERVER = "EMPLOYMENT_REQUEST_EMPLOYEE_RESPONSE_NOTIFICATION"


    //Contact requests states
    const val ACCEPTED_STATE = "ACCEPTED"
    const val PENDING_STATE = "PENDING"
    const val DENIED_STATE = "DENIED"

    //Firestore paths
    const val WAS_READ_FIELD = "wasRead"
    const val REPORT_FIELD_FIRESTORE = "report"
    const val TIMESTAMP_FIELD_FIRESTORE = "timestamp"
    const val BUG_REPORT_COLLECTION_NAME = "bug_reports"
    const val TIMESTAMP_FIELD = "timestamp"
    const val USERS_COLLECTION = "users"
    const val NOTIFICATIONS_SUB_COLLECTION = "notifications"
    const val BUSINESS_COLLECTION = "business"
    const val EVENTS_COLLECTION = "events"
    const val PRODUCTS_COLLECTION = "products"
    const val RECORDS_COLLECTION = "records"
    const val CLIENTS_COLLECTION = "clients"
    const val SUPPLIERS_COLLECTION = "suppliers"
    const val CATEGORIES_COLLECTION = "categories"

    //Debt Type
    const val CLIENT_DEBT = 0
    const val SUPPLIER_DEBT = 1
}