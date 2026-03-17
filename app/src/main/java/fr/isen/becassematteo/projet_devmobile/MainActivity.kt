package fr.isen.becassematteo.projet_devmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Le reste de ta classe MainActivity ici...
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
            LoginScreen(onLoginSuccess = { navController.navigate("home") })
        }
        composable("home") {
            // On passe la logique de clic ici
            HomeScreen(onCategoryClick = { name ->
                navController.navigate("franchises/$name")
            })
        }
        composable("franchises/{catName}") { backStackEntry ->
            val catName = backStackEntry.arguments?.getString("catName") ?: ""
            FranchiseScreen(catName)
        }
    }
}