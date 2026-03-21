package fr.isen.becassematteo.projet_devmobile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FranchiseScreen(categoryName: String, navController: NavController) {
    var allFilms by remember { mutableStateOf<List<Film>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val filteredFilms = remember(allFilms, searchQuery) {
        allFilms.filter { it.titre.contains(searchQuery, ignoreCase = true) }
    }

    LaunchedEffect(categoryName) {
        val databaseUrl = "https://projet-devmobile-79f0d-default-rtdb.europe-west1.firebasedatabase.app"
        val database = FirebaseDatabase.getInstance(databaseUrl).reference

        database.child("categories").get().addOnSuccessListener { snapshot ->
            val tempFilms = mutableListOf<Film>()
            snapshot.children.forEach { catSnapshot ->
                val nameInJson = catSnapshot.child("categorie").getValue(String::class.java)

                if (nameInJson == categoryName) {
                    catSnapshot.child("franchises").children.forEach { franchise ->
                        franchise.child("films").children.forEach { m ->
                            m.getValue(Film::class.java)?.let { tempFilms.add(it) }
                        }
                        franchise.child("sous_sagas").children.forEach { ss ->
                            ss.child("films").children.forEach { m ->
                                m.getValue(Film::class.java)?.let { tempFilms.add(it) }
                            }
                        }
                    }
                }
            }
            allFilms = tempFilms
            isLoading = false
        }.addOnFailureListener {
            Log.e("FIREBASE", "Erreur: ${it.message}")
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                TopAppBar(
                    title = { Text(categoryName, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    placeholder = { Text("Rechercher un film...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredFilms) { film ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            // AJOUT DU CLIC ICI
                            .clickable {
                                // On passe le titre du film dans l'URL de navigation
                                Log.d("NAVI", "Navigation vers detail/${film.titre}")
                                navController.navigate("detail/${film.titre}")
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column {
                            Box(modifier = Modifier.weight(1f)) {
                                SubcomposeAsyncImage(
                                    model = film.image?.toString()?.trim() ?: "",
                                    contentDescription = film.titre,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                        }
                                    },
                                    error = {
                                        Box(
                                            modifier = Modifier.fillMaxSize().background(Color.LightGray),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Gray)
                                        }
                                    }
                                )
                            }
                            Text(
                                text = film.titre,
                                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}