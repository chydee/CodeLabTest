package com.desmond.codelab.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Singleton

@Singleton
class SettingsContext constructor(applicationContext: Context) {

    private val config: SharedPreferences

    init {
        config = EncryptedSharedPreferences.create(
            applicationContext,
            CONFIG_NAME,
            MasterKey.Builder(applicationContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var id: Int
        get() = config.getInt(KEY_USER_ID, -1)
        set(value) = config.edit().putInt(KEY_USER_ID, value).apply()

    var isRememberMe: Boolean
        get() = config.getBoolean(KEY_REMEMBER_ME, false)
        set(value) = config.edit().putBoolean(KEY_REMEMBER_ME, value).apply()

    var userEmail: String
        get() = config.getString(KEY_USER_EMAIL, "") ?: ""
        set(value) = config.edit().putString(KEY_USER_EMAIL, value).apply()

    var userName: String
        get() = config.getString(KEY_USER_NAME, "") ?: ""
        set(value) = config.edit().putString(KEY_USER_NAME, value).apply()

    var firstName: String
        get() = config.getString(KEY_FIRST_NAME, "") ?: ""
        set(value) = config.edit().putString(KEY_FIRST_NAME, value).apply()

    var lastName: String
        get() = config.getString(KEY_LAST_NAME, "") ?: ""
        set(value) = config.edit().putString(KEY_LAST_NAME, value).apply()

    var gender: String
        get() = config.getString(KEY_GENDER, "") ?: ""
        set(value) = config.edit().putString(KEY_GENDER, value).apply()
    var avatar: String
        get() = config.getString(KEY_DEFAULT_IMAGE, "") ?: ""
        set(value) = config.edit().putString(KEY_DEFAULT_IMAGE, value).apply()

    var localAvatar: String
        get() = config.getString(KEY_LOCAL_IMAGE, "") ?: ""
        set(value) = config.edit().putString(KEY_LOCAL_IMAGE, value).apply()

    var token: String
        get() = config.getString(KEY_TOKEN, "") ?: ""
        set(value) = config.edit().putString(KEY_TOKEN, value).apply()
    var password: String
        get() = config.getString(KEY_LOGIN_PASSWORD, "") ?: ""
        set(value) = config.edit().putString(KEY_LOGIN_PASSWORD, value).apply()

    fun clearAllStoredData() {
        config.edit().clear().apply()
    }

    companion object {
        private const val CONFIG_NAME = "codelab_secret_shared_prefs"
        private const val KEY_REMEMBER_ME = "user_email"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_FIRST_NAME = "user_name_first"
        private const val KEY_LAST_NAME = "user_name_last"
        private const val KEY_GENDER = "user_gender"
        private const val KEY_DEFAULT_IMAGE = "def_image"
        private const val KEY_TOKEN = "token"
        private const val KEY_LOGIN_PASSWORD = "auth_password"
        private const val KEY_LOCAL_IMAGE = "local_image_uri"
    }
}