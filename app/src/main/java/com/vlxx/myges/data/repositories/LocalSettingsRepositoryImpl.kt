package com.vlxx.myges.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.vlxx.myges.domain.repositories.LocalSettingsRepository
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class LocalSettingsRepositoryImpl(
    context: Context
) : LocalSettingsRepository {

    companion object {
        private const val KEY_ALIAS = "local_settings_key"
        private const val PREFS_NAME = "local_settings"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12

        private const val ACCESS_TOKEN_KEY = "access_token"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getAccessToken(): String? {
        return decryptValue(ACCESS_TOKEN_KEY)
    }

    override suspend fun setAccessToken(token: String) {
        try {
            encryptAndStore(ACCESS_TOKEN_KEY, token)
        } catch (e: Exception) {
            // Handle encryption error if needed
        }
    }

    override suspend fun clearAccessToken() {
        prefs.edit {
            remove(ACCESS_TOKEN_KEY)
        }
    }

    private fun encryptAndStore(key: String, value: String) {
        val secretKey = getSecretKey()
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(value.toByteArray(Charset.forName("UTF-8")))
        val combined = ByteArray(iv.size + encryptedBytes.size)
        SecureRandom().nextBytes(combined)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
        val encryptedValueBase64 = Base64.encodeToString(combined, Base64.DEFAULT)

        prefs.edit {
            putString(key, encryptedValueBase64)
        }
    }

    private fun decryptValue(key: String): String? {
        val encryptedValueBase64 = prefs.getString(key, null) ?: return null

        return try {
            val secretKey = getSecretKey()
            val cipher = Cipher.getInstance(AES_MODE)
            val combined = Base64.decode(encryptedValueBase64, Base64.DEFAULT)
            val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
            val encryptedBytes = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            null
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        if (keyStore.containsAlias(KEY_ALIAS)) {
            val secretKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            return secretKeyEntry.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

}