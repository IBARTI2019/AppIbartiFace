import androidx.lifecycle.*
import com.oesvica.appibartiFace.data.model.Result
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

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



fun <T> LiveData<T>.observeJustOnce(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, Observer {  item->
        observer.onChanged(item)
        removeObservers(owner)
    })
}
fun <T> LiveData<T>.distinct(): LiveData<T> {
    val distinctLiveData = MediatorLiveData<T>()
    distinctLiveData.addSource(this, object : Observer<T>{
        private var initialized = false
        private var lastObj: T? = null
        override fun onChanged(obj: T?) {
            if(!initialized){
                initialized = true
                lastObj = obj
                distinctLiveData.postValue(lastObj?:return)
            } else{
                if((obj == null && lastObj != null) || obj != lastObj){
                    lastObj = obj
                    distinctLiveData.postValue(lastObj?:return)
                }
            }
        }
    })
    return distinctLiveData
}

//suspend fun <T> findOrRefresh(
//    findSync: suspend () -> List<T>,
//    refresh: suspend () -> Result<List<T>>
//): LiveData<List<T>> {
//    return liveData<List<T>> {
//        val list = findSync()
//        if (list.isNullOrEmpty()) {
//            val result = withContext(IO) { refresh() }
//            if (result.success != null) {
//                emit(result.success)
//            } else {
//                emit(emptyList())
//            }
//        } else {
//            emit(list)
//        }
//    }
//}

abstract class CachedResourceList<T>(){

    protected abstract suspend fun loadFromDb(): List<T>
    protected abstract suspend fun fetch(): Result<List<T>>

    fun asLiveData(context: CoroutineContext): LiveData<List<T>> {
        return liveData<List<T>> {
            val list = loadFromDb()
            if (list.isNullOrEmpty()) {
                val result = withContext(context) { fetch() }
                if (result.success != null) {
                    emit(result.success)
                } else {
                    emit(emptyList())
                }
            } else {
                emit(list)
            }
        }
    }

}