package fr.isen.becassematteo.projet_devmobile

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Categorie(
    val categorie: String = "",
    val franchises: List<Franchise> = emptyList()
)

@IgnoreExtraProperties
data class Franchise(
    val nom: String = "",
    val films: List<Film>? = null,
    val sous_sagas: List<SousSaga>? = null
)

@IgnoreExtraProperties
data class SousSaga(
    val nom: String = "",
    val films: List<Film> = emptyList()
)

@IgnoreExtraProperties
data class Film(
    val titre: String = "",
    val image: Any? = null, // Any? car l'URL peut être manquante ou mal formatée
    val description: String = "", // Champ important pour l'écran de détails
    val annee: Any? = null  // Any? car le prof a mis des Int et des String selon les films
)