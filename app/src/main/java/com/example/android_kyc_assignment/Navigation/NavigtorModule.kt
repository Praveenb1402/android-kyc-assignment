package com.example.android_kyc_assignment.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android_kyc_assignment.uiscreen.CameraScreen
import com.example.android_kyc_assignment.uiscreen.HomeScreen
import com.example.android_kyc_assignment.uiscreen.CustomerAccount
import com.example.android_kyc_assignment.uiscreen.HomeViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: Int) = "customer_detail/$customerId"
    }
    object Camera : Screen("camera/{customerId}") {
        fun createRoute(customerId: Int) = "camera/$customerId"
    }
}

@Composable
fun NavigatorModule(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val homeViewModel = hiltViewModel<HomeViewModel>()


            LaunchedEffect(Unit) {
                homeViewModel.refreshFromRoom()
            }

            HomeScreen(
                viewModel = homeViewModel,

                onCustomerClick = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                },
                onKycClick = { customerId ->
                    navController.navigate(Screen.Camera.createRoute(customerId))
                }
            )
        }

        composable(Screen.CustomerDetail.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")?.toIntOrNull() ?: 0
            CustomerAccount(
                customerId = customerId,
                onBackClick = { navController.popBackStack() },
                onOpenCamera = { navController.navigate(Screen.Camera.createRoute(it)) }
            )
        }

        composable(Screen.Camera.route) { backStackEntry ->
            val customerId = backStackEntry.arguments
                ?.getString("customerId")?.toIntOrNull() ?: 0
            CameraScreen(
                customerId = customerId,
                onPhotoTaken = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}