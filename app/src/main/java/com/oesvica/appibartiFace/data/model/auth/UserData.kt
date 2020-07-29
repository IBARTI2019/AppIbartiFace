package com.oesvica.appibartiFace.data.model.auth

import com.google.gson.annotations.SerializedName
import com.oesvica.appibartiFace.data.model.permission.Permission
import com.oesvica.appibartiFace.data.model.role.Roll

data class UserData(
    @SerializedName("_id") var id: String,
    @SerializedName("usuario") var usuario: String,
    @SerializedName("nombre") var nombre: String,
    @SerializedName("apellido") var apellido: String,
    @SerializedName("roll") var roll: Roll,
    @SerializedName("permisos") var permissions: Array<Permission>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserData

        if (id != other.id) return false
        if (usuario != other.usuario) return false
        if (nombre != other.nombre) return false
        if (apellido != other.apellido) return false
        if (roll != other.roll) return false
        if (!permissions.contentEquals(other.permissions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + usuario.hashCode()
        result = 31 * result + nombre.hashCode()
        result = 31 * result + apellido.hashCode()
        result = 31 * result + roll.hashCode()
        result = 31 * result + permissions.contentHashCode()
        return result
    }
}

