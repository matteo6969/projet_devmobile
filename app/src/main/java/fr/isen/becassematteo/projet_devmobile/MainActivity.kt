package fr.isen.becassematteo.projet_devmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// Si LoginScreen est souligné en rouge, fais Alt+Entrée dessus pour l'importer !

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Le thème de base généré par Android Studio (le nom dépend de ton projet)
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // C'EST ICI LA MAGIE : On appelle ton nouvel écran !
                    LoginScreen()
                }
            }
        }
    }
}