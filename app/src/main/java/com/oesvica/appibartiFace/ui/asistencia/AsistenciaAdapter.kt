package com.oesvica.appibartiFace.ui.asistencia

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.data.model.asistencia.Asistencia
import kotlinx.android.synthetic.main.fragment_asistencia.view.*

/**
 * [RecyclerView.Adapter] that can display a [Asistencia].
 */
class AsistenciaAdapter : RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder>() {

    var allAsistencias: List<Asistencia> = ArrayList()
        set(value) {
            field = listOf(
                Asistencia(
                    docId = "Cedula",
                    codFicha = "Ficha",
                    names = "Nombres",
                    surnames = "Apellidos",
                    date = "Fecha",
                    time = "Hora"
                )
            ) + value
            updateFilteredAsistencias()
        }
    var asistenciasFilter: (Asistencia) -> Boolean = { true }
        set(value) {
            field = value
            updateFilteredAsistencias()
        }
    private var filteredAsistencias: List<Asistencia> = allAsistencias

    private fun updateFilteredAsistencias() {
        filteredAsistencias =
            allAsistencias.filterIndexed { index, asistencia -> asistenciasFilter(asistencia) || index == 0 }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_asistencia, parent, false)
        return AsistenciaViewHolder(view)
    }

    override fun onBindViewHolder(holderAsistencia: AsistenciaViewHolder, position: Int) {
        holderAsistencia.bind(position, filteredAsistencias[position])
    }

    override fun getItemCount(): Int = filteredAsistencias.size

    inner class AsistenciaViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val docId: TextView by lazy { view.cedulaTextView }
        private val ficha: TextView by lazy { view.fichaTextView }
        private val names: TextView by lazy { view.nombresTextView }
        private val surnames: TextView by lazy { view.apellidosTextView }
        private val date: TextView by lazy { view.fechaTextView }
        private val time: TextView by lazy { view.horaTextView }

        @SuppressLint("DefaultLocale")
        fun bind(index: Int, asistencia: Asistencia) {
            when {
                index == 0 -> view.setBackgroundColor(Color.rgb(221, 245, 255))
                index % 2 == 0 -> view.setBackgroundColor(Color.rgb(223, 251, 255))
                else -> view.setBackgroundColor(Color.rgb(255, 255, 255))
            }
            docId.text = asistencia.docId
            ficha.text = asistencia.codFicha
            names.text = asistencia.names?.split(" ")?.get(0)?.toLowerCase()?.capitalize() ?: ""
            surnames.text = asistencia.surnames?.split(" ")?.get(0)?.toLowerCase()?.capitalize() ?: ""
            date.text = asistencia.date
            time.text = asistencia.time
        }
    }
}