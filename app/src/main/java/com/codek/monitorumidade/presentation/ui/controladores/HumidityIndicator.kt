package com.codek.monitorumidade.presentation.ui.controladores

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codek.monitorumidade.R
import com.codek.monitorumidade.presentation.ui.theme.GreenGrade
import com.codek.monitorumidade.presentation.ui.theme.GreenGrade2
import com.codek.monitorumidade.presentation.ui.theme.GreenGrade3
import com.codek.monitorumidade.presentation.ui.theme.OrangeGrade
import com.codek.monitorumidade.presentation.ui.theme.OrangeGrade2
import com.codek.monitorumidade.presentation.ui.theme.RedGrade
import com.codek.monitorumidade.presentation.ui.theme.RedGrade2
import com.codek.monitorumidade.presentation.ui.theme.YellowGrade
import com.codek.monitorumidade.presentation.viewmodel.AgroViewModel
import com.codek.monitorumidade.ui.viewmodels.MonitorViewModel
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@Composable
fun HumidityIndicator(
    agroViewModel: AgroViewModel
) {

    val humidityValue by agroViewModel.humidityValue.collectAsState()
    val timeData by agroViewModel.timeData.collectAsState()
    var consulta by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            agroViewModel.loadAgroData(1)

            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            consulta = currentDateTime.format(formatter)

            delay(timeData)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            CircularIndicator(value = humidityValue?.div(100f) ?: 0f)
            IndicatorValue(value = "${humidityValue}", consultaValue = consulta)
        }
    }
}

@Composable
fun IndicatorValue(value: String, consultaValue: String) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.humidity2),
                contentDescription = "Umidade do solo",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "$value%",
            fontSize = 40.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "UMIDADE DO SOLO",
            fontSize = 18.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Última consulta: " + consultaValue,
            fontSize = 12.sp,
            color = Color.LightGray,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun CircularIndicator(value: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        drawArcs(value, 240f)
    }
}

fun DrawScope.drawArcs(progress: Float, maxValue: Float) {
    val startAngle = 270 - maxValue / 2
    val sweepAngle = maxValue * progress

    val topLeft = Offset(50f, 80f)
    val size = Size(size.width - 100f, size.height - 100f)

    val sweepGradient = Brush.sweepGradient(
        colors = listOf(
            OrangeGrade, // 0°
            RedGrade2, // 15°
            RedGrade, // 30°
            RedGrade, // 45°
            RedGrade, // 60°
            RedGrade, // 75°
            RedGrade, // 90°
            RedGrade, // 105°
            RedGrade, // 120°
            RedGrade, // 135°
            RedGrade2, // 150°
            OrangeGrade, // 165°
            OrangeGrade2, // 180°
            YellowGrade, // 195°
            GreenGrade, // 210°
            GreenGrade, // 225°
            GreenGrade2, // 240°
            GreenGrade3, // 255°
            GreenGrade3, // 270°
            GreenGrade3, // 285°
            GreenGrade2, // 300°
            GreenGrade, // 315°
            GreenGrade, // 330°
            YellowGrade, // 345°
            OrangeGrade2, // 360°
        ),
        center = Offset(size.width / 2, size.height / 2) // Define o centro para a varredura do gradiente
    )

    fun drawBlur() {
        for (i in 0..20) {
            drawArc(
                color = Color.LightGray.copy(alpha = i / 900f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = 20f + (18 - i) * 15, cap = StrokeCap.Round)
            )
        }
    }

//    fun drawStroke() {
//        drawArc(
//            color = Blue500,
//            startAngle = startAngle,
//            sweepAngle = sweepAngle,
//            useCenter = false,
//            topLeft = topLeft,
//            size = size,
//            style = Stroke(width = 86f, cap = StrokeCap.Round)
//        )
//    }

    fun drawGradient() {
        drawArc(
            brush = sweepGradient,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = 80f, cap = StrokeCap.Round)
        )
    }

    drawBlur()
//    drawStroke()
    drawGradient()
}

//@Preview
//@Composable
//private fun HumidityIndicatorPreview() {
//    HumidityIndicator()
//}