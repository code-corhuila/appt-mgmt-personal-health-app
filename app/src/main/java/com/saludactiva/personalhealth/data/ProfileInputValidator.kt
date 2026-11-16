package com.saludactiva.personalhealth.data

object ProfileInputValidator {
    fun validateName(name: String): String? =
        if (name.trim().isEmpty()) "Ingresa tu nombre." else null

    fun validatePhone(phone: String): String? {
        val value = phone.trim()
        if (value.isEmpty()) return null
        val digits = value.count(Char::isDigit)
        val validCharacters = value.all { it.isDigit() || it in "+ -()" }
        return if (!validCharacters || digits !in 7..15) {
            "Ingresa un número telefónico válido."
        } else {
            null
        }
    }

    fun initials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter(String::isNotBlank)
        if (parts.isEmpty()) return ""
        val firstInitial = parts.first().first().uppercaseChar()
        val lastInitial = parts.last().first().uppercaseChar()
        return if (parts.size == 1) firstInitial.toString()
        else "$firstInitial$lastInitial"
    }
}
