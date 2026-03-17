package fr.isen.becassematteo.projet_devmobile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FranchiseScreen(categoryName: String) {
    val franchises = when (categoryName) {
        "Grandes Sagas" -> listOf("Star Wars", "Marvel MCU", "Pixar", "Avatar")
        "Autres Franchises Disney" -> listOf("Pirates des Caraïbes", "Le Monde de Narnia", "Les Muppets")
        else -> listOf("Contenu à venir...")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(categoryName) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(franchises) { franchise ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = franchise,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}