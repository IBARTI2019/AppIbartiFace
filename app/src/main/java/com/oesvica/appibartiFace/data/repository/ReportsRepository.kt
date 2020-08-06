package com.oesvica.appibartiFace.data.repository

import androidx.lifecycle.LiveData
import com.oesvica.appibartiFace.data.model.personAsistencia.PersonAsistencia
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia

abstract class ReportsRepository {
    abstract fun findAsistencias(
        iniDate: CustomDate,
        endDate: CustomDate
    ): LiveData<List<Asistencia>>

    abstract suspend fun refreshAsistencias(iniDate: CustomDate, endDate: CustomDate): Result<Unit>

    abstract fun findPersonAsistencias(
        iniDate: CustomDate,
        endDate: CustomDate,
        isApto: Boolean
    ): LiveData<List<PersonAsistencia>>

    abstract suspend fun refreshPersonAsistencias(
        iniDate: CustomDate,
        endDate: CustomDate,
        isApto: Boolean
    ): Result<List<PersonAsistencia>>
}