package com.oesvica.appibartiFace.data.model.asistencia

import com.oesvica.appibartiFace.data.model.CustomDate

data class AsistenciaFilter(
    var iniDate: CustomDate,
    var endDate: CustomDate
)