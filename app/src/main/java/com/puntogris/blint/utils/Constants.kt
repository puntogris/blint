package com.puntogris.blint.utils

object Constants {
    const val APP_VERSION = "1.0.0"

    const val ARG_SCANNING_RESULT = "scanning_result"
    const val WEB_CLIENT_ID = "284997358263-0gsjvjo373578qmch4sgbu8r6408vtjl.apps.googleusercontent.com"
    const val FIX_GOOGLE_PLAY_SERVICES_URL = "https://support.google.com/googleplay/answer/9037938"
    const val SUPPORT_EMAIL = "ayuda@blint.app"
    const val APP_PLAY_STORE_URI= "https://play.google.com/store/apps/details?id=com.puntogris.blint"
    const val PLAY_STORE_PACKAGE= "com.android.vending"
    const val PRIVACY_POLICY_URI = "https://blint.app/privacy-policy.html"
    const val TERMS_AND_CONDITIONS_URI = "https://blint.app/terms-and-conditions.html"
    const val BLINT_WEBSITE_LEARN_MORE = "https://blint.app"

    const val ADMINISTRATOR = "ADMINISTRATOR"

    //Report types
    const val SUPPLIERS_LIST = 101
    const val CLIENTS_LIST = 102
    const val PRODUCTS_LIST = 103
    const val SUPPLIERS_RECORDS = 104
    const val CLIENTS_RECORDS = 105
    const val PRODUCTS_RECORDS = 106

    //Report time frame
    const val WEEKLY = "WEEKLY"
    const val MONTHLY = "MONTHLY"
    const val QUARTERLY = "QUARTERLY"
    const val BIANNUAL = "BIANNUAL"
    const val ANNUAL = "ANNUAL"
    const val HISTORICAL = "HISTORICAL"

    //Debt Type
    const val CLIENT_DEBT = 0
    const val SUPPLIER_DEBT = 1

    //Calendar event status
    const val PENDING = "PENDING"
    const val FINISHED = "DENIED"

    //Debt type
    const val CLIENT = "CLIENT"
    const val SUPPLIER = "SUPPLIER"

    //Business type
    const val ONLINE = "ONLINE"
    const val LOCAL = "LOCAL"

    //Product record/ order type
    const val IN = "IN"
    const val OUT = "OUT"
    const val INITIAL = "INITIAL"

    //Notification type
    const val NEW_USER = "NEW_USER"
    const val NEW_BUSINESS = "NEW_BUSINESS"
    const val NEW_EMPLOYEE = "NEW_EMPLOYEE"
    const val ADVERTISEMENT = "ADVERTISEMENT"

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
    const val ORDERS_COLLECTION = "orders"
    const val TYPE_FIELD = "type"
    const val OWNER_FIELD = "owner"

    //Storage
    const val USERS_PATH = "users"
    const val BACKUP_PATH = "backup"

    //Shared pref
    const val SHOW_WELCOME_PREF = "show_welcome_ui"
    const val THEME_PREF = "theme_pref"
    const val USER_HAS_BUSINESS_PREF = "user_has_business"
    const val ACCOUNT_PREF = "account_pref"
    const val NOTIFICATIONS_PREF = "notifications_pref"
    const val BACKUP_PREF = "backup_pref"
    const val HELP_PREF = "help_pref"
    const val ABOUT_PREF = "about_pref"
    const val AUTO_BACKUP_PREF = "auto_backup_pref"
    const val CREATE_BACKUP_PREF = "create_backup_pref"
    const val RESTORE_BACKUP_PREF = "restore_backup_pref"
    const val USER_DATA_PREF = "user_data_pref"
    const val USER_BUSINESS_PREF = "user_business_pref"
    const val PAYMENTS_PREF = "payments_pref"
    const val PRIVACY_PREF = "privacy_pref"
    const val SIGN_OUT_PREF = "sign_out_pref"
}