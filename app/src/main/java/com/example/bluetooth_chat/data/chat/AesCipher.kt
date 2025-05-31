package com.example.bluetooth_chat.data.chat

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

object AesCipher {
    // Replace this demo encryption setup with secure key management before production.
    // This is just a proof of concept; do not use as-is in serious deployments.
    private const val SECRET = "YourSuperSecretPassword" // Change this!
    private const val SALT = "YourSalt" // Change this!
    private const val ITERATION_COUNT = 65536
    private const val KEY_LENGTH = 256
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private val IV = ByteArray(16).apply { Random.Default.nextBytes(this) }

    private fun generateKey(): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(SECRET.toCharArray(), SALT.toByteArray(), ITERATION_COUNT, KEY_LENGTH)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val key = generateKey()
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(IV))
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        // Prepend IV for decryption
        val encryptedWithIv = IV + encrypted
        return Base64.encodeToString(encryptedWithIv, Base64.DEFAULT)
    }

    fun decrypt(base64CipherText: String): String {
        val decoded = Base64.decode(base64CipherText, Base64.DEFAULT)
        val iv = decoded.copyOfRange(0, 16)
        val cipherText = decoded.copyOfRange(16, decoded.size)
        val cipher = Cipher.getInstance(ALGORITHM)
        val key = generateKey()
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        val decrypted = cipher.doFinal(cipherText)
        return String(decrypted, Charsets.UTF_8)
    }
}
