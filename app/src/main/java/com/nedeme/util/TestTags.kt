package com.nedeme.util

object TestTags {
    // Login screen
    const val LOGIN_EMAIL = "login_email"
    const val LOGIN_PASSWORD = "login_password"
    const val LOGIN_BUTTON = "login_button"
    const val LOGIN_REGISTER_LINK = "login_register_link"
    const val LOGIN_TITLE = "login_title"
    const val LOGIN_ERROR = "login_error"

    // Register screen
    const val REGISTER_NAME = "register_name"
    const val REGISTER_EMAIL = "register_email"
    const val REGISTER_PHONE = "register_phone"
    const val REGISTER_PASSWORD = "register_password"
    const val REGISTER_CONFIRM_PASSWORD = "register_confirm_password"
    const val REGISTER_BUTTON = "register_button"
    const val REGISTER_CLIENT_CHIP = "register_client_chip"
    const val REGISTER_TRADESPERSON_CHIP = "register_tradesperson_chip"
    const val REGISTER_BACK = "register_back"
    const val REGISTER_ERROR = "register_error"

    // Home screen
    const val HOME_TITLE = "home_title"
    const val HOME_CATEGORY_GRID = "home_category_grid"
    fun categoryCard(id: String) = "category_$id"

    // Search results
    const val SEARCH_LIST = "search_list"
    const val SEARCH_MAP_TOGGLE = "search_map_toggle"
    const val SEARCH_EMPTY = "search_empty"
    fun tradespersonCard(id: String) = "tradesperson_$id"

    // Tradesperson profile
    const val PROFILE_NAME = "profile_name"
    const val PROFILE_REQUEST_BUTTON = "profile_request_button"

    // Booking request
    const val BOOKING_DESCRIPTION = "booking_description"
    const val BOOKING_DATE_PICKER = "booking_date_picker"
    const val BOOKING_SUBMIT = "booking_submit"
    const val BOOKING_SUCCESS = "booking_success"

    // Dashboard
    const val DASHBOARD_AVAILABILITY_TOGGLE = "dashboard_availability"
    const val DASHBOARD_PENDING_TAB = "dashboard_pending_tab"
    const val DASHBOARD_ACCEPTED_TAB = "dashboard_accepted_tab"

    // Bottom nav
    const val NAV_HOME = "nav_home"
    const val NAV_BOOKINGS = "nav_bookings"
    const val NAV_PROFILE = "nav_profile"
    const val NAV_DASHBOARD = "nav_dashboard"
}
