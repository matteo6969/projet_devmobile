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

        // 1. Écran de Login
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home")
            })
        }

        // 2. Écran d'accueil
        composable("home") {
            HomeScreen(onCategoryClick = { name ->
                // CORRECTION : On a enlevé le 's' pour correspondre à la route suivante
                navController.navigate("franchise/$name")
            })
        }

        // 3. Écran de la liste des films
        composable("franchise/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            FranchiseScreen(categoryName = categoryName, navController = navController)
        }

        // 4. Écran de détails (Obligatoire pour ne pas crash au clic sur un film)
        composable("detail/{filmTitre}") { backStackEntry ->
            val titre = backStackEntry.arguments?.getString("filmTitre") ?: ""
            DetailScreen(filmTitre = titre, navController = navController)
        }
    }
}