package com.oesvica.appibartiFace.ui.main

import androidx.lifecycle.MutableLiveData
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.auth.AuthInfo
import com.oesvica.appibartiFace.data.model.auth.LogInResponse
import com.oesvica.appibartiFace.data.model.auth.LogOutResponse
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
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
            authInfo.value = Result(success = maestrosRepository.getAuthInfo())
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