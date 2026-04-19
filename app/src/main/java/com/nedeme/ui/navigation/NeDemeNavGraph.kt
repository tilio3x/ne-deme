package com.nedeme.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nedeme.data.model.UserRole
import com.nedeme.ui.screens.auth.AuthViewModel
import com.nedeme.ui.screens.auth.LoginScreen
import com.nedeme.ui.screens.auth.RegisterScreen
import com.nedeme.ui.screens.booking.BookingRequestScreen
import com.nedeme.ui.screens.booking.BookingViewModel
import com.nedeme.ui.screens.booking.MyBookingsScreen
import com.nedeme.ui.screens.dashboard.DashboardViewModel
import com.nedeme.ui.screens.dashboard.TradespersonDashboardScreen
import com.nedeme.ui.screens.dashboard.TradespersonSetupScreen
import com.nedeme.ui.screens.home.HomeScreen
import com.nedeme.ui.screens.home.HomeViewModel
import com.nedeme.ui.screens.profile.ProfileViewModel
import com.nedeme.ui.screens.profile.TradespersonProfileScreen
import com.nedeme.ui.screens.profile.UserProfileScreen
import com.nedeme.ui.screens.search.SearchResultsScreen
import com.nedeme.ui.screens.search.SearchViewModel
import com.nedeme.ui.screens.splash.SplashScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@Composable
fun NeDemeNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    // Determine start destination based on auth state
    LaunchedEffect(authState.isLoggedIn, authState.user) {
        if (authState.isLoggedIn && authState.user != null) {
            val dest = when (authState.user!!.role) {
                UserRole.TRADESPERSON -> Screen.Dashboard.route
                UserRole.CLIENT -> Screen.Home.route
            }
            navController.navigate(dest) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isMainScreen = currentRoute in listOf(
        Screen.Home.route, Screen.MyBookings.route,
        Screen.Profile.route, Screen.Dashboard.route
    )

    val clientNavItems = listOf(
        BottomNavItem(Screen.Home.route, "Accueil") {
            Icon(Icons.Default.Home, contentDescription = "Accueil")
        },
        BottomNavItem(Screen.MyBookings.route, "Demandes") {
            Icon(Icons.Default.Assignment, contentDescription = "Demandes")
        },
        BottomNavItem(Screen.Profile.route, "Profil") {
            Icon(Icons.Default.Person, contentDescription = "Profil")
        }
    )

    val tradespersonNavItems = listOf(
        BottomNavItem(Screen.Dashboard.route, "Dashboard") {
            Icon(Icons.Default.Dashboard, contentDescription = "Dashboard")
        },
        BottomNavItem(Screen.Profile.route, "Profil") {
            Icon(Icons.Default.Person, contentDescription = "Profil")
        }
    )

    val navItems = if (authState.user?.role == UserRole.TRADESPERSON)
        tradespersonNavItems else clientNavItems

    Scaffold(
        bottomBar = {
            if (isMainScreen) {
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth
            composable(Screen.Login.route) {
                LoginScreen(
                    uiState = authState,
                    onLogin = authViewModel::login,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onClearError = authViewModel::clearError
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    uiState = authState,
                    onRegister = authViewModel::register,
                    onNavigateBack = { navController.popBackStack() },
                    onClearError = authViewModel::clearError
                )
            }

            // Client screens
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    uiState = uiState,
                    onCategoryClick = { category ->
                        navController.navigate(Screen.SearchResults.createRoute(category))
                    }
                )
            }

            composable(Screen.SearchResults.route) {
                val viewModel: SearchViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                SearchResultsScreen(
                    uiState = uiState,
                    onTradespersonClick = { id ->
                        navController.navigate(Screen.TradespersonProfile.createRoute(id))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TradespersonProfile.route) {
                val viewModel: ProfileViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                TradespersonProfileScreen(
                    uiState = uiState,
                    onRequestService = { tpId, category ->
                        navController.navigate(Screen.BookingRequest.createRoute(tpId, category))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BookingRequest.route) {
                val viewModel: BookingViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                BookingRequestScreen(
                    uiState = uiState,
                    category = viewModel.category,
                    onSubmit = viewModel::submitBooking,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.MyBookings.route) {
                MyBookingsScreen()
            }

            // Tradesperson screens
            composable(Screen.Dashboard.route) {
                val viewModel: DashboardViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                TradespersonDashboardScreen(
                    uiState = uiState,
                    onAccept = viewModel::acceptBooking,
                    onReject = viewModel::rejectBooking,
                    onComplete = viewModel::completeBooking,
                    onToggleAvailability = viewModel::toggleAvailability
                )
            }

            composable(Screen.TradespersonSetup.route) {
                TradespersonSetupScreen(
                    onSetupComplete = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Common
            composable(Screen.Profile.route) {
                UserProfileScreen(
                    user = authState.user,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
