package com.oesvica.appibartiFace.data.model.permission

import com.google.gson.annotations.SerializedName
import com.oesvica.appibartiFace.data.model.method.Method
import com.oesvica.appibartiFace.data.model.module.Module

data class Permission(
    @SerializedName("cod_permiso") var codeRoll: String,
    @SerializedName("modulo") var module: Module,
    @SerializedName("permiso") var permission: String,
    @SerializedName("ruta") var path: String,
    @SerializedName("metodos") var methods: Array<Method>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Permission

        if (codeRoll != other.codeRoll) return false
        if (module != other.module) return false
        if (permission != other.permission) return false
        if (path != other.path) return false
        if (!methods.contentEquals(other.methods)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = codeRoll.hashCode()
        result = 31 * result + module.hashCode()
        result = 31 * result + permission.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + methods.contentHashCode()
        return result
    }
}