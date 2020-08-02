/*
 * Copyright (C) 2018 Sneyder Angulo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oesvica.appibartiFace.utils.base

import androidx.lifecycle.ViewModel
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import com.oesvica.appibartiFace.utils.coroutines.CoroutineContextProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(
    private val coroutineContextProvider: CoroutineContextProvider? = null
) : ViewModel(), CoroutineScope {

    val compositeDisposable by lazy { CompositeDisposable() }

    private val job by lazy { SupervisorJob() }

    override val coroutineContext: CoroutineContext
        get() = Main

    val IO by lazy { coroutineContextProvider?.IO ?: Dispatchers.IO + job }
    val Main by lazy { coroutineContextProvider?.Main ?: Dispatchers.Main + job }
    val Unconfined by lazy { coroutineContextProvider?.Unconfined ?: Dispatchers.Unconfined + job }

    /**
     * Adds a disposable to compositeDisposable
     */
    fun add(disposable: Disposable): Boolean {
        return compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        job.cancel()
        super.onCleared()
    }
}