package com.jorge.gymtracker.core

import java.text.Normalizer

fun String.normalizeForSearch(): String =
    Normalizer.normalize(this.lowercase(), Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")       // quita acentos
        .replace("[^a-z0-9\\s]".toRegex(), " ")  // quita símbolos
        .replace("\\s+".toRegex(), " ")          // colapsa espacios
        .trim()

/**
 * Devuelve true si TODOS los tokens de la query aparecen en alguno de los campos.
 * Únicamente compara contra: nombre y músculo primario.
 */
fun matchesAllTokens(query: String, fields: List<String?>): Boolean {
    if (query.isBlank()) return true
    val tokens = query.normalizeForSearch().split(" ").filter { it.isNotBlank() }
    if (tokens.isEmpty()) return true
    val haystack = fields.filterNotNull().joinToString(" ").normalizeForSearch()
    return tokens.all { token -> haystack.contains(token) }
}
