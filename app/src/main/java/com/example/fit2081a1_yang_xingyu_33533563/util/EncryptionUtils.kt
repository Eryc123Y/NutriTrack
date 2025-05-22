package com.example.fit2081a1_yang_xingyu_33533563.util

import android.util.Base64
import android.util.Log
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
private const val SALT_BYTES = 16
private const val ITERATIONS = 120000
private const val KEY_LENGTH = 256

// The clinician BCrypt hash
private const val CLINICIAN_BCRYPT_HASH = "\$2a\$12\$JxFbYNzs4UtDEym3gCRWr.xgWcLdJkAwNRXqyLYjKSVp3iIHRo2xO"

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

/**
 * Verify the user password against the stored hash.
 * @param password The password to verify.
 * @param storedHash The stored hash to compare against.
 * @return True if the password matches, false otherwise.
 */
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

/**
 * Verify the clinician code against the stored BCrypt hash.
 * @param code The clinician code to verify.
 * @return True if the code matches, false otherwise.
 */
fun verifyClinicianCode(code: String): Boolean {
    return try {
        BCrypt.checkpw(code, CLINICIAN_BCRYPT_HASH)
    } catch (e: Exception) {
        Log.e("EncryptionUtils", "Error verifying clinician code: ${e.message}", e)
        false
    }
}