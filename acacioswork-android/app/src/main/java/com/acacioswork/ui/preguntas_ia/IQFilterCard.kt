package com.acacioswork.ui.preguntas_ia

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.ui.theme.*

/**
 * Panel de filtro de rango de fechas para Preguntas Inteligentes (paridad Web).
 * @author RADJ / Antigravity
 */
@Composable
fun IQFilterCard(
    dateFrom: String,
    dateTo: String,
    isFilterActive: Boolean,
    onFromClick: () -> Unit,
    onToClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📅 Rango de Análisis",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón selector Desde
                DateChipButton(
                    label = "Desde",
                    value = dateFrom,
                    onClick = onFromClick,
                    modifier = Modifier.weight(1f)
                )
                // Botón selector Hasta
                DateChipButton(
                    label = "Hasta",
                    value = dateTo,
                    onClick = onToClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Indicador de estado del filtro (paridad web)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isFilterActive) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Analizando: $dateFrom al $dateTo",
                        color = AccentGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Selecciona las fechas para activar el análisis",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * Chip interactivo de selección de fecha individual.
 * @author RADJ / Antigravity
 */
@Composable
private fun DateChipButton(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BgDark)
            .border(1.dp, Primary.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Column {
            Text(text = label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Primary, modifier = Modifier.size(13.dp))
                Text(
                    text = value.ifBlank { "AAAA-MM-DD" },
                    color = if (value.isBlank()) TextMuted else TextLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
