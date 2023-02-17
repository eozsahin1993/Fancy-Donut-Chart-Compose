package com.example.donutchart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.donutchart.ui.theme.DonutChartTheme
import com.example.donutchart.ui.theme.components.DonutChart
import com.example.donutchart.ui.theme.components.DonutChartData
import com.example.donutchart.ui.theme.components.DonutChartDataCollection

val data = DonutChartDataCollection(listOf(
    DonutChartData(1200.0f, Color.Black),
    DonutChartData(2300.0f, Color.Gray),
    DonutChartData(300.0f, Color.Red),
    DonutChartData(700.0f, Color.Blue),
    DonutChartData(1200.0f, Color.Magenta)
))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DonutChartTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DonutChart(data)
                }
            }
        }
    }
}