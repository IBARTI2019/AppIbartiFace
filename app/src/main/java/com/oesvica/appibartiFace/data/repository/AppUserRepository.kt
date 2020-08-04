package com.oesvica.appibartiFace.data.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.oesvica.appibartiFace.data.database.AppDatabase
import com.oesvica.appibartiFace.data.model.FirebaseTokenId
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.auth.*
import com.oesvica.appibartiFace.data.preferences.AppPreferencesHelper
import com.oesvica.appibartiFace.data.preferences.PreferencesHelper
import com.oesvica.appibartiFace.data.api.AppIbartiFaceApi
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.decoded
import com.oesvica.appibartiFace.utils.mapToResult
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AppUserRepository
@Inject constructor(
    private val appIbartiFaceApi: AppIbartiFaceApi,
    private val prefs: PreferencesHelper,
    private val appDatabase: AppDatabase
) : UserRepository() {


    override fun getAuthInfo(): AuthInfo {
        return AuthInfo(
            logIn = prefs[AppPreferencesHelper.LOGIN],
            token = prefs[AppPreferencesHelper.TOKEN]
        )
    }

    override fun getUserData(): UserData? {
        val token = getAuthInfo().token
        if (token.isNullOrEmpty()) return null
        return Gson().fromJson<UserData>(token.decoded().payload, UserData::class.java)
    }

    override suspend fun getFirebaseTokenId(): String? {
        return suspendCoroutine { cont ->
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        debug("FirebaseInstanceId getInstance failed ${task.exception}")
                        cont.resume(null)
                        return@OnCompleteListener
                    }
                    val token = task.result?.token
                    debug("FirebaseInstanceId getInstance token=$token")
                    cont.resume(token)
                })
        }
    }

    override suspend fun sendFirebaseTokenId(firebaseTokenId: FirebaseTokenId): Result<FirebaseTokenId> {
        return mapToResult {
            appIbartiFaceApi.sendFirebaseTokenId(
                userId = getUserData()?.id ?: throw Exception("getUserData.id is null"),
                firebaseTokenId = firebaseTokenId
            ).also { prefs[AppPreferencesHelper.TOKEN_UPLOADED] = true }
        }
    }

    override suspend fun logIn(user: String, password: String): Result<LogInResponse> {
        return mapToResult {
            //logOut() didnt work :u
            appIbartiFaceApi.logIn(
                LogInRequest(
                    usuario = user,
                    clave = password
                )
            ).apply {
                prefs[AppPreferencesHelper.LOGIN] = logIn
                prefs[AppPreferencesHelper.USER] = user
                prefs[AppPreferencesHelper.TOKEN] = token
                prefs[AppPreferencesHelper.TOKEN_UPLOADED] = false
            }
        }
    }

    override suspend fun logOut(): Result<LogOutResponse> {
        return mapToResult {
            appIbartiFaceApi.logOut(LogOutRequest(usuario = prefs[AppPreferencesHelper.USER]))
                .apply {
                    prefs[AppPreferencesHelper.LOGIN] = logIn
                    prefs[AppPreferencesHelper.TOKEN] = ""
                    prefs[AppPreferencesHelper.TOKEN_UPLOADED] = false
                }.also { appDatabase.clearAllTables() }
        }
    }

}