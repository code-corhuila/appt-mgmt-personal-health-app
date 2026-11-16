package com.saludactiva.personalhealth.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileInputValidatorTest {
    @Test
    fun nameMustNotBeBlank() {
        assertEquals("Ingresa tu nombre.", ProfileInputValidator.validateName("  "))
        assertNull(ProfileInputValidator.validateName("Ana Pérez"))
    }

    @Test
    fun phoneIsOptionalButWhenPresentMustBeValid() {
        assertNull(ProfileInputValidator.validatePhone(""))
        assertNull(ProfileInputValidator.validatePhone("+57 300 123 4567"))
        assertEquals(
            "Ingresa un número telefónico válido.",
            ProfileInputValidator.validatePhone("12")
        )
        assertEquals(
            "Ingresa un número telefónico válido.",
            ProfileInputValidator.validatePhone("300abc1234")
        )
    }

    @Test
    fun initialsUseTheFirstTwoNameParts() {
        assertEquals("AP", ProfileInputValidator.initials("Ana María Pérez"))
        assertEquals("", ProfileInputValidator.initials("   "))
    }
}
