package com.example.surendar_5541.ssbbanksystemdemoapplication

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class DataEncryptionAndDecryption  {

   /* fun generateKey(): SecretKey {
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val kg = KeyGenerator.getInstance("AES")
        kg.init(128, sr)
        return SecretKeySpec(kg.generateKey().encoded, "AES")
    } */

    fun encryptData(data: String, secret: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES")
        cipher!!.init(Cipher.ENCRYPT_MODE, secret)
        return cipher.doFinal(data.toByteArray(charset("UTF-8")))
    }

    fun decryptData(cipherText: ByteArray, secret: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher!!.init(Cipher.DECRYPT_MODE, secret)
        return String(cipher.doFinal(cipherText), charset("UTF-8"))
    }

    fun getSecretKey(data: String): SecretKey {
        val sha = MessageDigest.getInstance("SHA-1")
        var key = data.toByteArray(charset("UTF-8"))
        key = sha.digest(key)
        key = Arrays.copyOf(key, 16) // use only first 128 bit
        return SecretKeySpec(key, "AES")
    }
}
