package fr.isen.becassematteo.projet_devmobile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var watchedFilms by remember { mutableStateOf<List<Film>>(emptyList()) }
    var wantToWatchFilms by remember { mutableStateOf<List<Film>>(emptyList()) }
    var ownedFilms by remember { mutableStateOf<List<Film>>(emptyList()) }
    var wantToSellFilms by remember { mutableStateOf<List<Film>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var isUserLoggedIn by remember { mutableStateOf(false) }

    val netflixBackground = Color(0xFF121212)
    val netflixTextWhite = Color.White
    val netflixTextGray = Color(0xFFB3B3B3)
    val netflixRed = Color(0xFFE50914)

    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            isUserLoggedIn = true
            val databaseUrl = "https://projet-devmobile-79f0d-default-rtdb.europe-west1.firebasedatabase.app"
            val database = FirebaseDatabase.getInstance(databaseUrl).reference
            val userId = currentUser.uid

            database.child("user_movie_status").child(userId).get().addOnSuccessListener { statusSnapshot ->
                val userStatuses = mutableMapOf<String, String>()
                statusSnapshot.children.forEach { movieNode ->
                    userStatuses[movieNode.key ?: ""] = movieNode.getValue(String::class.java) ?: ""
                }

                database.child("categories").get().addOnSuccessListener { catSnapshot ->
                    val tempWatched = mutableListOf<Film>()
                    val tempWantToWatch = mutableListOf<Film>()
                    val tempOwned = mutableListOf<Film>()
                    val tempWantToSell = mutableListOf<Film>()

                    catSnapshot.children.forEach { cat ->
                        cat.child("franchises").children.forEach { fran ->
                            fran.child("films").children.forEach { m ->
                                val currentFilm = m.getValue(Film::class.java)
                                if (currentFilm != null) {
                                    val safeTitle = currentFilm.titre.replace(Regex("[.#$\\[\\]]"), "_")
                                    when (userStatuses[safeTitle]) {
                                        "WATCHED" -> tempWatched.add(currentFilm)
                                        "WANT_TO_WATCH" -> tempWantToWatch.add(currentFilm)
                                        "OWNED" -> tempOwned.add(currentFilm)
                                        "WANT_TO_SELL" -> tempWantToSell.add(currentFilm)
                                    }
                                }
                            }
                            fran.child("sous_sagas").children.forEach { ss ->
                                ss.child("films").children.forEach { m ->
                                    val currentFilm = m.getValue(Film::class.java)
                                    if (currentFilm != null) {
                                        val safeTitle = currentFilm.titre.replace(Regex("[.#$\\[\\]]"), "_")
                                        when (userStatuses[safeTitle]) {
                                            "WATCHED" -> tempWatched.add(currentFilm)
                                            "WANT_TO_WATCH" -> tempWantToWatch.add(currentFilm)
                                            "OWNED" -> tempOwned.add(currentFilm)
                                            "WANT_TO_SELL" -> tempWantToSell.add(currentFilm)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    watchedFilms = tempWatched
                    wantToWatchFilms = tempWantToWatch
                    ownedFilms = tempOwned
                    wantToSellFilms = tempWantToSell
                    isLoading = false
                }.addOnFailureListener {
                    isLoading = false
                }
            }.addOnFailureListener {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = netflixBackground,
        topBar = {
            TopAppBar(
                title = { Text("Mon Profil", color = netflixTextWhite, fontWeight = FontWeight.Bold) },
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
        } else if (!isUserLoggedIn) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Veuillez vous connecter pour voir votre profil.", color = netflixTextWhite)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                if (watchedFilms.isNotEmpty()) {
                    ProfileMovieRow(title = "Films Déjà Vus", films = watchedFilms, navController = navController)
                }
                if (wantToWatchFilms.isNotEmpty()) {
                    ProfileMovieRow(title = "Ma Liste (À regarder)", films = wantToWatchFilms, navController = navController)
                }
                if (ownedFilms.isNotEmpty()) {
                    ProfileMovieRow(title = "Mes DVD & Blu-Ray", films = ownedFilms, navController = navController)
                }
                if (wantToSellFilms.isNotEmpty()) {
                    ProfileMovieRow(title = "Films dont je me sépare", films = wantToSellFilms, navController = navController)
                }

                if (watchedFilms.isEmpty() && wantToWatchFilms.isEmpty() && ownedFilms.isEmpty() && wantToSellFilms.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Votre liste est vide. Allez explorer des films !", color = netflixTextGray, textAlign = TextAlign.Center)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = netflixRed),
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                ) {
                    Text("Se Déconnecter", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ProfileMovieRow(title: String, films: List<Film>, navController: NavController) {
    val netflixTextWhite = Color.White
    val netflixCardColor = Color(0xFF1E1E1E)

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            color = netflixTextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(films) { film ->
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .clickable { navController.navigate("detail/${film.titre}") },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = netflixCardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SubcomposeAsyncImage(
                            model = film.image?.toString()?.trim() ?: "",
                            contentDescription = film.titre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = {
                                Box(
                                    modifier = Modifier.fillMaxSize().background(Color.DarkGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Gray)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}