package com.acacioswork.util

import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Utilidades para la generación y compartición (simulada de PDF) de reportes en Android.
 * @author RADJ / Antigravity
 */
object ReportSharing {

    fun shareReportText(context: Context, titulo: String, texto: String) {
        Toast.makeText(context, "Generando consolidado...", Toast.LENGTH_SHORT).show()
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                this.type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, titulo)
                putExtra(Intent.EXTRA_TEXT, texto)
            }
            val shareIntent = Intent.createChooser(sendIntent, "Exportar a:")
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
