package fr.isen.becassematteo.projet_devmobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FranchiseScreen(categoryName: String) {
    var filmsList by remember { mutableStateOf<List<Film>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(categoryName) {
        // URL de ta base située en Belgique
        val databaseUrl = "https://projet-devmobile-79f0d-default-rtdb.europe-west1.firebasedatabase.app"
        val database = FirebaseDatabase.getInstance(databaseUrl).reference

        database.child("categories").get().addOnSuccessListener { snapshot ->
            val allFilms = mutableListOf<Film>()

            snapshot.children.forEach { catSnapshot ->
                val nameInJson = catSnapshot.child("categorie").getValue(String::class.java)

                if (nameInJson == categoryName) {
                    catSnapshot.child("franchises").children.forEach { franchiseSnapshot ->
                        // 1. On récupère les films directs
                        franchiseSnapshot.child("films").children.forEach { movieSnapshot ->
                            val movie = movieSnapshot.getValue(Film::class.java)
                            if (movie != null) allFilms.add(movie)
                        }

                        // 2. On récupère les films dans les sous-sagas
                        franchiseSnapshot.child("sous_sagas").children.forEach { subSagaSnapshot ->
                            subSagaSnapshot.child("films").children.forEach { movieSnapshot ->
                                val movie = movieSnapshot.getValue(Film::class.java)
                                if (movie != null) allFilms.add(movie)
                            }
                        }
                    }
                }
            }
            filmsList = allFilms
            isLoading = false
        }.addOnFailureListener { error ->
            println("FIREBASE_ERROR: ${error.message}")
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filmsList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucun film trouvé dans cette catégorie")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filmsList) { film ->
                    Card(
                        modifier = Modifier.fillMaxWidth().aspectRatio(0.7f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        SubcomposeAsyncImage(
                            // On convertit le Any? de l'image en String pour Coil
                            model = film.image?.toString() ?: "",
                            contentDescription = film.titre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            },
                            error = {
                                Column(
                                    modifier = Modifier.fillMaxSize().background(Color.DarkGray).padding(8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = film.titre,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 2
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}