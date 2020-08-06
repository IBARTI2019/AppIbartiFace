package com.oesvica.appibartiFace.data.repository

import androidx.lifecycle.LiveData
import com.oesvica.appibartiFace.data.api.CedulasByDate
import com.oesvica.appibartiFace.data.api.Doc
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia

abstract class ReportsRepository {
    abstract fun findAsistencias(iniDate: CustomDate, endDate: CustomDate): LiveData<List<Asistencia>>
    abstract suspend fun refreshAsistencias(iniDate: CustomDate, endDate: CustomDate): Result<Unit>

    abstract suspend fun refreshAptos(iniDate: CustomDate, endDate: CustomDate): Result<List<Doc>>
    abstract suspend fun refreshNoAptos(iniDate: CustomDate, endDate: CustomDate): Result<List<Doc>>
}