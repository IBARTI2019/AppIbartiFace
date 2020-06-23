package com.oesvica.appibartiFace.data.preferences

import android.content.SharedPreferences
import com.oesvica.appibartiFace.utils.debug
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppPreferencesHelper @Inject constructor(sharedPreferences: SharedPreferences): PreferencesHelper(sharedPreferences){
    override fun saveTime(key: String) {
        set(key, currentTime())
    }

    override fun isTimeExpired(key: String, expiredAfterMillis: Long): Boolean {
        return (currentTime() - get<Long>(key,0) >= expiredAfterMillis).apply {
            debug("isTimeExpired for $key = $this")
        }
    }

    private fun currentTime() = Date().time
}