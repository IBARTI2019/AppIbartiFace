package com.oesvica.appibartiFace.ui.main

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.oesvica.appibartiFace.data.model.FirebaseTokenId
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.auth.AuthInfo
import com.oesvica.appibartiFace.data.model.auth.UserData
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.decoded
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val authInfo by lazy { MutableLiveData<Result<AuthInfo>>() }

    fun loadAuthInfo(){
        authInfo.value = Result(success = maestrosRepository.getAuthInfo())
    }

    fun logIn(user: String, password: String) {
        authInfo.value = Result()
        launch {
            withContext(IO) { maestrosRepository.logIn(user, password) }
            val loggedUserAuthInfo = maestrosRepository.getAuthInfo()
            authInfo.value = Result(success = loggedUserAuthInfo)
            if(loggedUserAuthInfo.logIn && !loggedUserAuthInfo.token.isNullOrEmpty()){
                uploadFirebaseTokenId()
            }
        }
    }

    private fun uploadFirebaseTokenId(){
        launch {
            val fbToken = withContext(IO){ maestrosRepository.getFirebaseTokenId() } ?: return@launch
            val result = withContext(IO){
                maestrosRepository.sendFirebaseTokenId(FirebaseTokenId(fbToken))
            }
            debug("uploadFirebaseTokenId result=$result")
        }
    }

    fun logOut() {
        authInfo.value = Result()
        launch {
            withContext(IO) { maestrosRepository.logOut() }
            authInfo.value = Result(success = maestrosRepository.getAuthInfo())
        }
    }

}