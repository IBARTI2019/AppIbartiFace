package com.oesvica.appibartiFace.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.FirebaseTokenId
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.auth.AuthInfo
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.data.repository.UserRepository
import com.oesvica.appibartiFace.utils.SingleLiveEvent
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.coroutines.CoroutineContextProvider
import com.oesvica.appibartiFace.utils.debug
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainViewModel
@Inject constructor(
    private val userRepository: UserRepository,
    private val maestrosRepository: MaestrosRepository,
    coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(coroutineContextProvider) {

    companion object {
        val ITEM_MENU_TO_PERMISSIONS = mapOf(
            "/standby/" to R.id.nav_standby,
            "/maestros/category/" to R.id.nav_categorias,
            "/maestros/status/" to R.id.nav_status,
            "/maestros/persons/" to R.id.nav_personas,
            "/reporte/asistencia-ibarti/" to R.id.nav_asistencia_ibarti,
            "/reporte/asistencia-apto/" to R.id.nav_aptos,
            "/reporte/asistencia-noapto/" to R.id.nav_no_aptos
        )
    }

    fun getUsername() = userRepository.getUserData()?.usuario

    fun getMenusToDisplayForUser(): List<Int>? {
        return userRepository.getUserData()?.permissions?.mapNotNull { permission ->
            val keyFound = ITEM_MENU_TO_PERMISSIONS.keys.find { key ->
                permission.path.contains(key)
            }
            ITEM_MENU_TO_PERMISSIONS[keyFound]
        }
    }

    val authInfo by lazy { MutableLiveData<Result<AuthInfo>>() }
    val snackBarMsg = SingleLiveEvent<String>()

    fun loadAuthInfo() {
        if (authInfo.value == null)
            authInfo.value = Result(success = userRepository.getAuthInfo())
    }

    fun logIn(user: String, password: String) {
        authInfo.value = Result()
        viewModelScope.launch {
            val result = withContext(IO) { userRepository.logIn(user, password) }
            val loggedUserAuthInfo = userRepository.getAuthInfo()
            authInfo.value = Result(success = loggedUserAuthInfo, error = result.error)
            if (result.error != null) snackBarMsg.value = result.error.message
            if (loggedUserAuthInfo.logIn && !loggedUserAuthInfo.token.isNullOrEmpty()) {
                uploadFirebaseTokenId()
                fetchCategories()
                fetchStatuses()
            }
        }
    }

    private suspend fun uploadFirebaseTokenId() {
        val fbToken =
            withContext(IO) { userRepository.getFirebaseTokenId() } ?: return
        val result = withContext(IO) {
            userRepository.sendFirebaseTokenId(FirebaseTokenId(fbToken))
        }
        debug("uploadFirebaseTokenId result=$result")
    }

    private suspend fun fetchCategories() {
        withContext(IO) { maestrosRepository.refreshCategories() }
    }

    private suspend fun fetchStatuses() {
        withContext(IO) { maestrosRepository.refreshStatuses() }
    }

    fun logOut() {
        authInfo.value = Result()
        viewModelScope.launch {
            withContext(IO) { userRepository.logOut() }
            authInfo.value = Result(success = userRepository.getAuthInfo())
        }
    }

}