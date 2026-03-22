package fr.isen.becassematteo.projet_devmobile

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(filmTitre: String, navController: NavController) {
    var film by remember { mutableStateOf<Film?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // NOUVEAU : Liste pour stocker les utilisateurs qui veulent se débarrasser du film
    var vendeurs by remember { mutableStateOf<List<String>>(emptyList()) }

    val context = LocalContext.current

    // Palette de couleurs "Netflix"
    val netflixBackground = Color(0xFF121212)
    val netflixTextWhite = Color.White
    val netflixTextGray = Color(0xFFB3B3B3)
    val netflixRed = Color(0xFFE50914)
    val netflixDarkGray = Color(0xFF333333)

    LaunchedEffect(filmTitre) {
        val databaseUrl = "https://projet-devmobile-79f0d-default-rtdb.europe-west1.firebasedatabase.app"
        val database = FirebaseDatabase.getInstance(databaseUrl).reference

        // 1. On récupère les infos du film
        database.child("categories").get().addOnSuccessListener { snapshot ->
            var tempFilm: Film? = null
            snapshot.children.forEach { cat ->
                cat.child("franchises").children.forEach { fran ->
                    fran.child("films").children.forEach { m ->
                        val current = m.getValue(Film::class.java)
                        if (current?.titre == filmTitre) tempFilm = current
                    }
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

        // 2. NOUVEAU : On cherche qui veut se débarrasser de ce film
        val safeTitle = filmTitre.replace(Regex("[.#$\\[\\]]"), "_")
        database.child("user_movie_status").get().addOnSuccessListener { snapshot ->
            val listeVendeurs = mutableListOf<String>()
            // On parcourt tous les utilisateurs
            snapshot.children.forEach { userNode ->
                // On regarde s'ils ont ce film et quel est le statut
                val status = userNode.child(safeTitle).getValue(String::class.java)
                if (status == "WANT_TO_SELL") {
                    // On ajoute l'ID de l'utilisateur (ou son pseudo s'il était dans la BDD)
                    val userId = userNode.key ?: "Utilisateur Inconnu"
                    listeVendeurs.add(userId)
                }
            }
            vendeurs = listeVendeurs
        }.addOnFailureListener {
            Log.e("FIREBASE", "Erreur recherche vendeurs: ${it.message}")
        }
    }

    Scaffold(
        containerColor = netflixBackground,
        topBar = {
            TopAppBar(
                title = { Text("Détails", color = netflixTextWhite) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = netflixTextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = netflixBackground)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = netflixRed)
            }
        } else if (film == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Film non trouvé", color = netflixTextWhite)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Image du film
                SubcomposeAsyncImage(
                    model = film?.image?.toString()?.trim() ?: "",
                    contentDescription = film?.titre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop,
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(netflixDarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = netflixTextGray, modifier = Modifier.size(48.dp))
                        }
                    }
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    // Titre
                    Text(
                        text = film?.titre ?: "Sans titre",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = netflixTextWhite,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Année
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "Année : ${film?.annee?.toString() ?: "Inconnue"}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = netflixTextGray,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BOUTONS D'ACTION (Noms modifiés pour coller à la consigne)
                    Text("Gérer mon statut :", color = netflixTextWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatusButton("Vu", netflixRed) { updateMovieStatus(film?.titre, "WATCHED", context) }
                        StatusButton("À regarder", netflixDarkGray) { updateMovieStatus(film?.titre, "WANT_TO_WATCH", context) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatusButton("Possédé (DVD/BR)", netflixDarkGray) { updateMovieStatus(film?.titre, "OWNED", context) }
                        StatusButton("S'en débarrasser", netflixDarkGray) { updateMovieStatus(film?.titre, "WANT_TO_SELL", context) }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Synopsis
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = netflixTextWhite,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = if (!film?.description.isNullOrEmpty()) film!!.description else "Aucune description disponible pour ce film.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = netflixTextGray,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // NOUVEAU : Affichage des utilisateurs voulant se débarrasser du film
                    Text(
                        text = "Ils veulent s'en débarrasser :",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = netflixRed,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if (vendeurs.isEmpty()) {
                        Text(
                            text = "Personne ne s'en sépare pour le moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = netflixTextGray
                        )
                    } else {
                        // On affiche la liste des ID (Si tu as une base de données avec les pseudos,
                        // il faudrait faire une correspondance, mais l'ID prouve que ça marche !)
                        vendeurs.forEach { vendeurId ->
                            Text(
                                text = "👤 Utilisateur : $vendeurId",
                                style = MaterialTheme.typography.bodyMedium,
                                color = netflixTextWhite,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// Composant pour les boutons de statut
@Composable
fun StatusButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}

// Fonction pour sauvegarder le statut dans Firebase
fun updateMovieStatus(movieTitle: String?, status: String, context: android.content.Context) {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance("https://projet-devmobile-79f0d-default-rtdb.europe-west1.firebasedatabase.app").reference

    val currentUser = auth.currentUser
    if (currentUser != null && movieTitle != null) {
        val userId = currentUser.uid
        val safeTitle = movieTitle.replace(Regex("[.#$\\[\\]]"), "_")

        database.child("user_movie_status").child(userId).child(safeTitle).setValue(status)
            .addOnSuccessListener {
                Toast.makeText(context, "Statut mis à jour !", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erreur de mise à jour", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "Tu dois être connecté !", Toast.LENGTH_SHORT).show()
    }
}