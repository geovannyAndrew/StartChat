package com.gyros.startchat.data

import android.content.Context
import javax.inject.Inject
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext

class StartChatSharedPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE)


    fun saveDefaultCountryCode(dialCode: String) {
        sharedPreferences.edit {
            putString(DEFAULT_COUNTRY_CODE, dialCode)
        }
    }

    fun getDefaultCountryCode(): String? {
        return sharedPreferences.getString(DEFAULT_COUNTRY_CODE, null)
    }

    companion object {
        private const val DEFAULT_COUNTRY_CODE = "defaultCountryCode"
        private const val NAME_SHARED_PREFERENCES = "StartChatPrefs"
    }
}