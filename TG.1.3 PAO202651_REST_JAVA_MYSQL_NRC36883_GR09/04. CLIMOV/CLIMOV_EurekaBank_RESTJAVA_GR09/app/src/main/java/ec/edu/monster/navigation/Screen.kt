
package ec.edu.monster.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Accounts : Screen("accounts")
    object Movements : Screen("movements") // por cuenta
    object GeneralMovements : Screen("general_movements") // todos
}