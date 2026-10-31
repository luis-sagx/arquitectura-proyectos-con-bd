// app/src/main/java/ec/edu/monster/navigation/AppNavGraph.kt

package ec.edu.monster.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ec.edu.monster.ui.screens.LoginScreen
import ec.edu.monster.ui.screens.AccountsScreen
import ec.edu.monster.ui.screens.MovementsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Accounts.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginError = { /* opcional: mostrar snackbar */ }
            )
        }

        composable(Screen.Accounts.route) {
            AccountsScreen(
                onAccountClick = { cuenta ->
                    navController.navigate("${Screen.Movements.route}/$cuenta")
                }
            )
        }

        composable(
            route = "${Screen.Movements.route}/{cuenta}",
            arguments = listOf(
                androidx.navigation.navArgument("cuenta") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val cuenta = backStackEntry.arguments?.getString("cuenta") ?: ""
            MovementsScreen(
                cuenta = cuenta,
                onBack = { navController.popBackStack() }
            )
        }
    }
}