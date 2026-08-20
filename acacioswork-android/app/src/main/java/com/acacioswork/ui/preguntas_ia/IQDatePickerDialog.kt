package com.acacioswork.ui.preguntas_ia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.acacioswork.ui.theme.*
import java.time.LocalDate

/**
 * Dialog selector de fecha para el filtro de Preguntas Inteligentes.
 * Usa Material3 DatePicker nativo de Android.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IQDatePickerDialog(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val parsedDate = runCatching { LocalDate.parse(initialDate) }.getOrElse { LocalDate.now() }

    val initialMillis = parsedDate
        .atStartOfDay(java.time.ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BgCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Seleccionar Fecha",
                    fontSize = 16.sp,
                    color = TextLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = BgCard,
                        titleContentColor = TextLight,
                        headlineContentColor = TextLight,
                        weekdayContentColor = TextMuted,
                        subheadContentColor = TextMuted,
                        navigationContentColor = TextLight,
                        yearContentColor = TextLight,
                        currentYearContentColor = Primary,
                        selectedYearContentColor = TextLight,
                        selectedYearContainerColor = Primary,
                        dayContentColor = TextLight,
                        selectedDayContentColor = TextLight,
                        selectedDayContainerColor = Primary,
                        todayContentColor = Primary,
                        todayDateBorderColor = Primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val selectedMillis = datePickerState.selectedDateMillis
                            if (selectedMillis != null) {
                                val date = java.time.Instant.ofEpochMilli(selectedMillis)
                                    .atZone(java.time.ZoneOffset.UTC)
                                    .toLocalDate()
                                onDateSelected(date.toString())
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Confirmar", color = TextLight)
                    }
                }
            }
        }
    }
}
