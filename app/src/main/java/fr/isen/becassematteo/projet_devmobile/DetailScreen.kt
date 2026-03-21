package fr.isen.becassematteo.projet_devmobile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(filmTitre: String, navController: NavController) {
    var film by remember { mutableStateOf<Film?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(filmTitre) {
        val databaseUrl = "https://projet-devmobile-79f0d-default-rtdb.europe-west1.firebasedatabase.app"
        val database = FirebaseDatabase.getInstance(databaseUrl).reference

        // On va rechercher le film complet dans toute la base par son titre
        database.child("categories").get().addOnSuccessListener { snapshot ->
            var tempFilm: Film? = null
            // C'est un peu lourd comme recherche, mais c'est le plus simple pour ton projet
            snapshot.children.forEach { cat ->
                cat.child("franchises").children.forEach { fran ->
                    // Films directs
                    fran.child("films").children.forEach { m ->
                        val current = m.getValue(Film::class.java)
                        if (current?.titre == filmTitre) tempFilm = current
                    }
                    // Films sous-sagas
                    fran.child("sous_sagas").children.forEach { ss ->
                        ss.child("films").children.forEach { m ->
                            val current = m.getValue(Film::class.java)
                            if (current?.titre == filmTitre) tempFilm = current
                        }
                    }
                }
            }
            film = tempFilm
            isLoading = false
        }.addOnFailureListener {
            Log.e("FIREBASE", "Erreur: ${it.message}")
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (film == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Film non trouvé")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()) // Pour pouvoir scroller si la description est longue
            ) {
                // Grande image du film en haut
                SubcomposeAsyncImage(
                    model = film?.image?.toString()?.trim() ?: "",
                    contentDescription = film?.titre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop,
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        }
                    }
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    // Titre du film
                    Text(
                        text = film?.titre ?: "Sans titre",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Année et Genre sur une ligne
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "Année : ${film?.annee?.toString() ?: "Inconnue"}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        // Note : Ajoute le champ 'genre' dans ton modèle Film si tu veux l'afficher
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Titre de la description
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Texte de la description
                    Text(
                        text = if (!film?.description.isNullOrEmpty()) film!!.description else "Aucune description disponible pour ce film.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}