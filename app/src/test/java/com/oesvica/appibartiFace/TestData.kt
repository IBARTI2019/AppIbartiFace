package com.oesvica.appibartiFace

import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.data.model.standby.Prediction
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.data.model.status.Status

fun getStandBy(): StandBy {
    return StandBy(
        client = "001",
        device = "0001",
        time = "12:04",
        date = "2020-08-11",
        url = "imgExample.jpg"
    )
}

fun getPrediction(): Prediction {
    return Prediction(cedula = "214542", url = "someImg.jpg", distancia = "12", semejanza = "45")
}

fun <T>T.asResult(): com.oesvica.appibartiFace.data.model.Result<T> {
    return com.oesvica.appibartiFace.data.model.Result(success = this)
}

fun getCategory(): Category {
    return Category(
        id = "65654654gj",
        alert = false,
        description = "Walk the walk"
    )
}

fun getStatus(): Status {
    return Status(
        id = "4654ghfdjh6gk6",
        description = "Talk the talk",
        category = "fhgj465465"
    )
}