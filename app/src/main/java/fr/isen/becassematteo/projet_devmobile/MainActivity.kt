package fr.isen.becassematteo.projet_devmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DisneyAppNavigation()
        }
    }
}

@Composable
fun DisneyAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home")
            })
        }

        composable("home") {
            HomeScreen(
                onCategoryClick = { name ->
                    navController.navigate("franchise/$name")
                },
                onProfileClick = {
                    navController.navigate("profile")
                }
            )
        }

        composable("franchise/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            FranchiseScreen(categoryName = categoryName, navController = navController)
        }

        composable("detail/{filmTitre}") { backStackEntry ->
            val titre = backStackEntry.arguments?.getString("filmTitre") ?: ""
            DetailScreen(filmTitre = titre, navController = navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}