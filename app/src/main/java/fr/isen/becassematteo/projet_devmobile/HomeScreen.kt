package fr.isen.becassematteo.projet_devmobile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onCategoryClick: (String) -> Unit) { // <-- On a ajouté le paramètre ici
    val categoriesDuProf = listOf(
        "Grandes Sagas",
        "Autres Franchises Disney",
        "Autres Franchises 20th Century Studios",
        "Autres Franchises Marvel",
        "Touchstone",
        "Dimension",
        "Franchises Internationales"
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("DisneyStream - Catégories") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categoriesDuProf) { nomCategorie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryClick(nomCategorie) }, // On active le clic !
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = nomCategorie,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Voir les franchises",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}