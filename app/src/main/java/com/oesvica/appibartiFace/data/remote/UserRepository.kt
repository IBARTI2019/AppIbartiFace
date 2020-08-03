package com.oesvica.appibartiFace.data.remote

import com.oesvica.appibartiFace.data.model.FirebaseTokenId
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.auth.AuthInfo
import com.oesvica.appibartiFace.data.model.auth.LogInResponse
import com.oesvica.appibartiFace.data.model.auth.LogOutResponse
import com.oesvica.appibartiFace.data.model.auth.UserData

abstract class UserRepository {

    abstract fun getAuthInfo(): AuthInfo
    abstract fun getUserData(): UserData?
    abstract suspend fun getFirebaseTokenId(): String?

    abstract suspend fun sendFirebaseTokenId(firebaseTokenId: FirebaseTokenId): Result<FirebaseTokenId>

    abstract suspend fun logIn(user: String, password: String): Result<LogInResponse>
    abstract suspend fun logOut(): Result<LogOutResponse>

}