package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.OrderType
import com.example.ui.screens.ActiveOrdersScreen
import com.example.ui.screens.BiometricLockOverlay
import com.example.ui.screens.CustomerSupportScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OrderDetailsScreen
import com.example.ui.screens.PaymentInstructionsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QrScannerScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.UtrSubmissionScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceHigh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MexcPayViewModel

object MexcPayDestinations {
    const val ROUTE_SPLASH = "splash"
    const val ROUTE_LOGIN = "login"
    const val ROUTE_SIGNUP = "signup"
    const val ROUTE_HOME = "home_dashboard"
    const val ROUTE_ACTIVE_ORDERS = "active_orders"
    const val ROUTE_ORDER_DETAILS = "order_details/{orderId}"
    const val ROUTE_PAYMENT_INSTRUCTIONS = "payment_instructions/{orderId}"
    const val ROUTE_UTR_SUBMISSION = "utr_submission/{orderId}"
    const val ROUTE_QR_SCANNER = "qr_scanner/{orderId}"
    const val ROUTE_WALLET = "wallet"
    const val ROUTE_PROFILE = "profile"
    const val ROUTE_SUPPORT = "customer_support"

    fun orderDetailsRoute(orderId: String) = "order_details/$orderId"
    fun paymentInstructionsRoute(orderId: String) = "payment_instructions/$orderId"
    fun utrSubmissionRoute(orderId: String) = "utr_submission/$orderId"
    fun qrScannerRoute(orderId: String = "ALL") = "qr_scanner/$orderId"
}

data class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topLevelDestinations = listOf(
    TopLevelDestination(
        route = MexcPayDestinations.ROUTE_HOME,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_tab_home"
    ),
    TopLevelDestination(
        route = MexcPayDestinations.ROUTE_ACTIVE_ORDERS,
        label = "Orders",
        selectedIcon = Icons.Filled.ListAlt,
        unselectedIcon = Icons.Outlined.ListAlt,
        testTag = "nav_tab_orders"
    ),
    TopLevelDestination(
        route = MexcPayDestinations.ROUTE_WALLET,
        label = "Wallet",
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet,
        testTag = "nav_tab_wallet"
    ),
    TopLevelDestination(
        route = MexcPayDestinations.ROUTE_PROFILE,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_tab_profile"
    ),
    TopLevelDestination(
        route = MexcPayDestinations.ROUTE_SUPPORT,
        label = "Support",
        selectedIcon = Icons.Filled.SupportAgent,
        unselectedIcon = Icons.Outlined.SupportAgent,
        testTag = "nav_tab_support"
    )
)

@Composable
fun MexcPayAppRoot(
    viewModel: MexcPayViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val showMainNavigation = currentRoute != null &&
        currentRoute != MexcPayDestinations.ROUTE_SPLASH &&
        currentRoute != MexcPayDestinations.ROUTE_LOGIN &&
        currentRoute != MexcPayDestinations.ROUTE_SIGNUP

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val authError by viewModel.authErrorMessage.collectAsStateWithLifecycle()
    val biometricAppLockEnabled by viewModel.biometricAppLockEnabled.collectAsStateWithLifecycle()
    val isSessionBiometricLocked by viewModel.isSessionBiometricLocked.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val filteredOrders by viewModel.filteredOrders.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedOrderTypeFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val allSubmissions by viewModel.allSubmissions.collectAsStateWithLifecycle()
    val bonusSettings by viewModel.bonusSettings.collectAsStateWithLifecycle()
    val walletSummary by viewModel.walletSummary.collectAsStateWithLifecycle()
    val utrFormStatus by viewModel.utrFormStatus.collectAsStateWithLifecycle()
    val scannedUtrPrefill by viewModel.scannedUtrPrefill.collectAsStateWithLifecycle()
    val scannedUpiPrefill by viewModel.scannedUpiPrefill.collectAsStateWithLifecycle()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 600.dp

        Scaffold(
            containerColor = NavyBackground,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (showMainNavigation && !isExpandedScreen) {
                    NavigationBar(
                        containerColor = NavySurface,
                        contentColor = TextPrimary
                    ) {
                        topLevelDestinations.forEach { dest ->
                            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = CyanAccent,
                                    selectedTextColor = CyanAccent,
                                    indicatorColor = NavySurfaceHigh,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showMainNavigation && isExpandedScreen) {
                    NavigationRail(
                        containerColor = NavySurface,
                        contentColor = TextPrimary
                    ) {
                        topLevelDestinations.forEach { dest ->
                            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = CyanAccent,
                                    selectedTextColor = CyanAccent,
                                    indicatorColor = ElectricBlue.copy(alpha = 0.25f),
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = MexcPayDestinations.ROUTE_SPLASH,
                    modifier = Modifier.weight(1f)
                ) {
                    composable(MexcPayDestinations.ROUTE_SPLASH) {
                        SplashScreen(
                            onFinished = {
                                val nextRoute = if (userProfile.isLoggedIn) {
                                    MexcPayDestinations.ROUTE_HOME
                                } else {
                                    MexcPayDestinations.ROUTE_LOGIN
                                }
                                navController.navigate(nextRoute) {
                                    popUpTo(MexcPayDestinations.ROUTE_SPLASH) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(MexcPayDestinations.ROUTE_LOGIN) {
                        LoginScreen(
                            errorMessage = authError,
                            onClearError = viewModel::clearAuthError,
                            onBiometricError = viewModel::setAuthError,
                            onLoginSubmit = { email, password ->
                                viewModel.loginWithCredentials(email, password) {
                                    navController.navigate(MexcPayDestinations.ROUTE_HOME) {
                                        popUpTo(MexcPayDestinations.ROUTE_LOGIN) { inclusive = true }
                                    }
                                }
                            },
                            onQuickSandboxLogin = {
                                viewModel.loginWithSandboxDemoAccount {
                                    navController.navigate(MexcPayDestinations.ROUTE_HOME) {
                                        popUpTo(MexcPayDestinations.ROUTE_LOGIN) { inclusive = true }
                                    }
                                }
                            },
                            onNavigateToSignUp = {
                                viewModel.clearAuthError()
                                navController.navigate(MexcPayDestinations.ROUTE_SIGNUP)
                            }
                        )
                    }

                    composable(MexcPayDestinations.ROUTE_SIGNUP) {
                        BackHandler {
                            viewModel.clearAuthError()
                            navController.popBackStack()
                        }
                        SignUpScreen(
                            errorMessage = authError,
                            onClearError = viewModel::clearAuthError,
                            onSignUpSubmit = { name, email, phone, password, upi ->
                                viewModel.signUpAccount(name, email, phone, password, upi) {
                                    navController.navigate(MexcPayDestinations.ROUTE_HOME) {
                                        popUpTo(MexcPayDestinations.ROUTE_LOGIN) { inclusive = true }
                                    }
                                }
                            },
                            onNavigateBackToLogin = {
                                viewModel.clearAuthError()
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(MexcPayDestinations.ROUTE_HOME) {
                        HomeDashboardScreen(
                            userProfile = userProfile,
                            walletSummary = walletSummary,
                            orders = allOrders,
                            transactions = allTransactions,
                            bonusSettings = bonusSettings,
                            onBuyInrClick = {
                                viewModel.setOrderTypeFilter(OrderType.BUY_INR)
                                navController.navigate(MexcPayDestinations.ROUTE_ACTIVE_ORDERS)
                            },
                            onSellInrClick = {
                                viewModel.setOrderTypeFilter(OrderType.SELL_INR)
                                navController.navigate(MexcPayDestinations.ROUTE_ACTIVE_ORDERS)
                            },
                            onViewAllOrders = {
                                viewModel.setOrderTypeFilter(null)
                                navController.navigate(MexcPayDestinations.ROUTE_ACTIVE_ORDERS)
                            },
                            onOrderClick = { orderId ->
                                navController.navigate(MexcPayDestinations.orderDetailsRoute(orderId))
                            },
                            onNavigateToWallet = {
                                navController.navigate(MexcPayDestinations.ROUTE_WALLET)
                            },
                            onNavigateToSupport = {
                                navController.navigate(MexcPayDestinations.ROUTE_SUPPORT)
                            },
                            onLockAppNow = viewModel::lockSessionNow,
                            onOpenQrScanner = {
                                navController.navigate(MexcPayDestinations.qrScannerRoute("ALL"))
                            }
                        )
                    }

                    composable(MexcPayDestinations.ROUTE_ACTIVE_ORDERS) {
                        ActiveOrdersScreen(
                            orders = filteredOrders,
                            selectedFilter = selectedFilter,
                            searchQuery = searchQuery,
                            onFilterChange = viewModel::setOrderTypeFilter,
                            onSearchQueryChange = viewModel::setSearchQuery,
                            onOrderClick = { orderId ->
                                navController.navigate(MexcPayDestinations.orderDetailsRoute(orderId))
                            }
                        )
                    }

                    composable(
                        route = MexcPayDestinations.ROUTE_ORDER_DETAILS,
                        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId").orEmpty()
                        val order = allOrders.find { it.orderId == orderId }
                        val orderSubmissions = allSubmissions.filter { it.orderId == orderId }

                        OrderDetailsScreen(
                            order = order,
                            submissionsForOrder = orderSubmissions,
                            onBack = { navController.popBackStack() },
                            onProceedToPaymentInstructions = { id ->
                                navController.navigate(MexcPayDestinations.paymentInstructionsRoute(id))
                            },
                            onProceedToUtrSubmission = { id ->
                                viewModel.resetUtrFormStatus()
                                navController.navigate(MexcPayDestinations.utrSubmissionRoute(id))
                            }
                        )
                    }

                    composable(
                        route = MexcPayDestinations.ROUTE_PAYMENT_INSTRUCTIONS,
                        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId").orEmpty()
                        val order = allOrders.find { it.orderId == orderId }

                        PaymentInstructionsScreen(
                            order = order,
                            onBack = { navController.popBackStack() },
                            onNavigateToUtrSubmission = { id ->
                                viewModel.resetUtrFormStatus()
                                navController.navigate(MexcPayDestinations.utrSubmissionRoute(id))
                            },
                            onOpenQrScanner = { id ->
                                navController.navigate(MexcPayDestinations.qrScannerRoute(id))
                            }
                        )
                    }

                    composable(
                        route = MexcPayDestinations.ROUTE_UTR_SUBMISSION,
                        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId").orEmpty()
                        val order = allOrders.find { it.orderId == orderId }

                        UtrSubmissionScreen(
                            order = order,
                            utrFormStatus = utrFormStatus,
                            scannedUtrPrefill = scannedUtrPrefill,
                            scannedUpiPrefill = scannedUpiPrefill,
                            onConsumeScannedPrefill = viewModel::clearScannedQrPrefill,
                            onOpenQrScanner = { id ->
                                navController.navigate(MexcPayDestinations.qrScannerRoute(id))
                            },
                            onBack = { navController.popBackStack() },
                            onSubmitUtr = { id, utr, upi, remarks ->
                                viewModel.submitUtrClaim(id, utr, upi, remarks)
                            },
                            onResetStatus = viewModel::resetUtrFormStatus,
                            onNavigateToWallet = {
                                navController.navigate(MexcPayDestinations.ROUTE_WALLET) {
                                    popUpTo(MexcPayDestinations.ROUTE_HOME)
                                }
                            }
                        )
                    }

                    composable(
                        route = MexcPayDestinations.ROUTE_QR_SCANNER,
                        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val orderIdArg = backStackEntry.arguments?.getString("orderId").orEmpty()
                        val preselectedOrder = allOrders.find { it.orderId == orderIdArg }

                        QrScannerScreen(
                            preselectedOrder = preselectedOrder,
                            allOrders = allOrders,
                            onBack = { navController.popBackStack() },
                            onProceedToUtrWithScan = { targetOrderId, scannedUtr, scannedUpi ->
                                viewModel.resetUtrFormStatus()
                                viewModel.applyScannedQrPrefill(scannedUtr, scannedUpi)
                                navController.navigate(MexcPayDestinations.utrSubmissionRoute(targetOrderId)) {
                                    popUpTo(MexcPayDestinations.ROUTE_QR_SCANNER) { inclusive = true }
                                }
                            },
                            onInspectOrderDetails = { targetOrderId ->
                                navController.navigate(MexcPayDestinations.orderDetailsRoute(targetOrderId)) {
                                    popUpTo(MexcPayDestinations.ROUTE_QR_SCANNER) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(MexcPayDestinations.ROUTE_WALLET) {
                        WalletScreen(
                            walletSummary = walletSummary,
                            transactions = allTransactions,
                            submissions = allSubmissions,
                            onBrowseOrders = {
                                navController.navigate(MexcPayDestinations.ROUTE_ACTIVE_ORDERS)
                            }
                        )
                    }

                    composable(MexcPayDestinations.ROUTE_PROFILE) {
                        ProfileScreen(
                            userProfile = userProfile,
                            bonusSettings = bonusSettings,
                            biometricAppLockEnabled = biometricAppLockEnabled,
                            onToggleBiometricAppLock = viewModel::setBiometricAppLockEnabled,
                            onLockSessionNow = viewModel::lockSessionNow,
                            onNavigateToSupport = {
                                navController.navigate(MexcPayDestinations.ROUTE_SUPPORT)
                            },
                            onResetSandboxData = viewModel::resetSampleData,
                            onLogout = {
                                viewModel.logout {
                                    navController.navigate(MexcPayDestinations.ROUTE_LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }

                    composable(MexcPayDestinations.ROUTE_SUPPORT) {
                        CustomerSupportScreen(
                            onBack = {
                                if (!navController.popBackStack()) {
                                    navController.navigate(MexcPayDestinations.ROUTE_HOME)
                                }
                            }
                        )
                    }
                }
            }
        }

        if (userProfile.isLoggedIn && isSessionBiometricLocked) {
            BiometricLockOverlay(
                userFullName = userProfile.fullName,
                onUnlockSuccess = viewModel::unlockBiometricSession,
                onSwitchToPasswordLogin = {
                    viewModel.unlockBiometricSession()
                    viewModel.logout {
                        navController.navigate(MexcPayDestinations.ROUTE_LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
