package com.puntogris.blint.common.utils

object Constants {

    const val APP_PLAY_STORE_URI =
        "https://play.google.com/store/apps/details?id=com.puntogris.blint"
    const val PRIVACY_POLICY_URI = "https://blint.app/privacy-policy.html"
    const val BLINT_WEBSITE_LEARN_MORE = "https://blint.app"
    const val BACKUP_WEBSITE_LEARN_MORE = "https://blint.app"
    const val SYNC_INFO_WEBSITE_LEARN_MORE = "https://blint.app"
    const val WHATS_APP_PACKAGE = "com.whatsapp"

    //Navigation
    const val PRODUCT_BARCODE_KEY = "product_barcode_key"
    const val EVENT_FILTER_KEY = "event_filter_key"
    const val EVENT_POSITION_KEY = "event_position_key"
    const val PRODUCT_CATEGORIES_KEY = "product_categories_key"
    const val PRODUCT_SUPPLIERS_KEY = "product_suppliers_key"
    const val EDIT_PRODUCT_KEY = "edit_fragment_results_key"
    const val SCANNER_FRAGMENT_KEY = "scanner_fragment_key"
    const val SCANNER_RESULT_KEY = "scanner_results_key"
    const val RESUME_CAMERA_KEY = "resume_camera_key"

    //Ticket types
    const val PROBLEM = "PROBLEM"
    const val SUGGESTION = "SUGGESTION"

    //Debt Type
    const val CLIENT_DEBT = 0
    const val SUPPLIER_DEBT = 1

    //Calendar event status
    const val PENDING = "PENDING"
    const val FINISHED = "DENIED"

    //Debt type
    const val CLIENT = "CLIENT"
    const val SUPPLIER = "SUPPLIER"

    //Product record/ order type
    const val IN = "IN"
    const val OUT = "OUT"
    const val INITIAL = "INITIAL"

    //Firestore paths
    const val TICKETS_COLLECTION = "tickets"
    const val USERS_COLLECTION = "users"

    //Storage
    const val USERS_PATH = "users"
    const val BACKUP_PATH = "backup"

    //Shared pref
    const val SHOW_WELCOME_PREF = "show_welcome_ui"
    const val USER_HAS_BUSINESS_PREF = "user_has_business"

    //Supplier contact picker
    const val COMPANY_CONTACT = 1
    const val SELLER_CONTACT = 2
}