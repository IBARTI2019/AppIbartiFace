package com.oesvica.appibartiFace.data.repository

import androidx.lifecycle.LiveData
import com.oesvica.appibartiFace.data.database.AsistenciaDao
import com.oesvica.appibartiFace.data.model.CustomDate
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia
import com.oesvica.appibartiFace.data.preferences.AppPreferencesHelper.Companion.TOKEN
import com.oesvica.appibartiFace.data.preferences.PreferencesHelper
import com.oesvica.appibartiFace.data.remote.AppIbartiFaceApi
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.mapToResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppReportsRepository
@Inject constructor(
    private val appIbartiFaceApi: AppIbartiFaceApi,
    private val prefs: PreferencesHelper,
    private val asistenciaDao: AsistenciaDao
) : ReportsRepository() {

    override fun findAsistencias(
        iniDate: CustomDate,
        endDate: CustomDate
    ): LiveData<List<Asistencia>> {
        return asistenciaDao.findAsistencias(iniDate.toString(), endDate.toString())
    }

    override suspend fun refreshAsistencias(
        iniDate: CustomDate,
        endDate: CustomDate
    ): Result<Unit> {
        return mapToResult {
            val asistencias =
                appIbartiFaceApi.findAsistencias(
                    authorization = prefs[TOKEN],
                    iniDate = iniDate.toString(),
                    endDate = endDate.toString()
                )
                    .map { asis ->
                        // the api may return names o surnames as null
                        asis.names = asis.names?.trim() ?: ""
                        asis.surnames = asis.surnames?.trim() ?: ""
                        asis
                    }
            debug("refreshAsistencias($iniDate: String, $endDate: String)=${asistencias.take(2)}")
            asistenciaDao.replaceAsistencias(
                iniDate.toString(),
                endDate.toString(),
                *asistencias.toTypedArray()
            )
        }
    }
}