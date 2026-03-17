package fr.isen.becassematteo.projet_devmobile

data class Categorie(
    val categorie: String = "",
    val franchises: List<Franchise> = emptyList()
)

data class Franchise(
    val nom: String = "",
    val films: List<Film>? = null,
    val sous_sagas: List<SousSaga>? = null
)

data class SousSaga(
    val nom: String = "",
    val films: List<Film> = emptyList()
)

data class Film(
    val numero: Int = 0,
    val titre: String = "",
    val annee: Int? = null,
    val genre: String? = null
)
