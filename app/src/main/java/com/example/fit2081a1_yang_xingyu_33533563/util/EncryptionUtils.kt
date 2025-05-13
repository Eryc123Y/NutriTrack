package com.example.fit2081a1_yang_xingyu_33533563.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
private const val SALT_BYTES = 16
private const val ITERATIONS = 120000
private const val KEY_LENGTH = 256

// Generate cryptographically secure salt
fun generateSalt(): ByteArray {
    val random = SecureRandom()
    return ByteArray(SALT_BYTES).apply {
        random.nextBytes(this)
    }
}

// Hash password with salt using PBKDF2
fun hashPassword(password: String, salt: ByteArray = generateSalt()): String {
    val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
    val spec = PBEKeySpec(
        password.toCharArray(),
        salt,
        ITERATIONS,
        KEY_LENGTH
    )
    val hash = factory.generateSecret(spec).encoded
    return "${Base64.encodeToString(salt, Base64.NO_WRAP)}:${Base64.encodeToString(hash, Base64.NO_WRAP)}"
}

// Verify password against stored hash
fun verifyPassword(password: String, storedHash: String): Boolean {
    val parts = storedHash.split(":")
    require(parts.size == 2) { "Invalid hash format" }

    val salt = Base64.decode(parts[0], Base64.NO_WRAP)
    val originalHash = Base64.decode(parts[1], Base64.NO_WRAP)

    val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
    val spec = PBEKeySpec(
        password.toCharArray(),
        salt,
        ITERATIONS,
        KEY_LENGTH
    )
    val testHash = factory.generateSecret(spec).encoded
    return originalHash.contentEquals(testHash)
}