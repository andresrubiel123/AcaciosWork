package com.acacioswork.ui.preguntas_ia

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.ui.theme.*

/**
 * Tarjeta individual de Pregunta Inteligente con animación pulsante en la respuesta.
 * Paridad 100% con la versión web (badge, ícono, pregunta, área de respuesta animada).
 * @author RADJ / Antigravity
 */
@Composable
fun IQCard(
    q: IQCardData,
    isEnabled: Boolean,
    answer: String?,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val isVencimiento = q.id == "proximos-vencer"
    val badgeColor = if (isVencimiento) AlertRed else Primary

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) BgCard else BgCard.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            // Fila superior: badge + ícono emoji
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = q.badge,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(text = q.icon, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Texto de la pregunta
            Text(
                text = q.question,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isEnabled) TextLight else TextMuted,
                lineHeight = 18.sp
            )

            // Área de respuesta con animación pulsante (paridad web)
            if (isLoading) {
                Spacer(modifier = Modifier.height(10.dp))
                IQLoadingIndicator()
            } else if (!answer.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                IQAnswerBox(text = answer)
            }
        }
    }
}

/**
 * Indicador de carga dentro de la tarjeta IQ.
 * @author RADJ / Antigravity
 */
@Composable
private fun IQLoadingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "iq_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iq_alpha"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        CircularProgressIndicator(
            color = AccentOrange.copy(alpha = alpha),
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp
        )
        Text(
            text = "Analizando datos...",
            color = AccentOrange.copy(alpha = alpha),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Caja de respuesta con borde naranja izquierdo y fondo oscuro (paridad web .iq-answer).
 * @author RADJ / Antigravity
 */
@Composable
private fun IQAnswerBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(BgDark)
            .border(
                width = 1.dp,
                color = AccentOrange.copy(alpha = 0.5f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(10.dp)
    ) {
        // Línea naranja izquierda (paridad web border-left)
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(AccentOrange)
                .align(Alignment.CenterStart)
        )
        Text(
            text = text,
            color = TextLight,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
