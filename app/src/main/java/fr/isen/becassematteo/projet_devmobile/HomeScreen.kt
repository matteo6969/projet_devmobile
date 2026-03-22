package fr.isen.becassematteo.projet_devmobile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onCategoryClick: (String) -> Unit, onProfileClick: () -> Unit) {
    val categoriesDuProf = listOf(
        "Grandes Sagas",
        "Autres Franchises Disney",
        "Autres Franchises 20th Century Studios",
        "Autres Franchises Marvel",
        "Touchstone",
        "Dimension",
        "Franchises Internationales"
    )

    val netflixBackground = Color(0xFF121212)
    val netflixCardColor = Color(0xFF1E1E1E)
    val netflixRed = Color(0xFFE50914)
    val netflixTextWhite = Color.White
    val netflixTextGray = Color(0xFFB3B3B3)

    Scaffold(
        containerColor = netflixBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DisneyStream",
                        color = netflixRed,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = netflixTextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = netflixBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categoriesDuProf) { nomCategorie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryClick(nomCategorie) },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = netflixCardColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = nomCategorie,
                            style = MaterialTheme.typography.titleLarge,
                            color = netflixTextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Voir les franchises >",
                            style = MaterialTheme.typography.bodyMedium,
                            color = netflixTextGray
                        )
                    }
                }
            }
        }
    }
}