package com.nedeme.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object RoleSelection : Screen("role_selection")
    data object Home : Screen("home")
    data object SearchResults : Screen("search_results/{category}") {
        fun createRoute(category: String) = "search_results/$category"
    }
    data object TradespersonProfile : Screen("tradesperson_profile/{tradespersonId}") {
        fun createRoute(tradespersonId: String) = "tradesperson_profile/$tradespersonId"
    }
    data object BookingRequest : Screen("booking_request/{tradespersonId}/{category}") {
        fun createRoute(tradespersonId: String, category: String) =
            "booking_request/$tradespersonId/$category"
    }
    data object MyBookings : Screen("my_bookings")
    data object Dashboard : Screen("dashboard")
    data object Profile : Screen("profile")
    data object TradespersonSetup : Screen("tradesperson_setup")
}
