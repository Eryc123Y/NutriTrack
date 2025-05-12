package com.example.fit2081a1_yang_xingyu_33533563.util

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


private const val ALGORITHM = "AES"
private const val TRANSFORMATION = "AES"

// Generate a new AES key
fun generateKey(): SecretKey {
    val keyGen = KeyGenerator.getInstance(ALGORITHM)
    keyGen.init(256) // AES-256
    return keyGen.generateKey()
}

// Encrypt a plain text using the provided key
fun encrypt(key: SecretKey, data: String): String {
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val encryptedBytes = cipher.doFinal(data.toByteArray())
    return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
}

// Decrypt an encrypted text using the provided key
fun decrypt(key: SecretKey, encryptedData: String): String {
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.DECRYPT_MODE, key)
    val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
    val decryptedBytes = cipher.doFinal(decodedBytes)
    return String(decryptedBytes)
}

// Convert a string key to SecretKey
fun stringToSecretKey(key: String): SecretKey {
    val decodedKey = Base64.decode(key, Base64.DEFAULT)
    return SecretKeySpec(decodedKey, 0, decodedKey.size, ALGORITHM)
}

// Convert a SecretKey to string
fun secretKeyToString(key: SecretKey): String {
    return Base64.encodeToString(key.encoded, Base64.DEFAULT)
}
