package com.example.donutchart

import android.os.Bundle
import android.provider.MediaStore.Video
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.donutchart.ui.theme.*
import com.example.donutchart.ui.theme.components.DonutChart
import com.example.donutchart.ui.theme.components.DonutChartData
import com.example.donutchart.ui.theme.components.DonutChartDataCollection

val viewData = DonutChartDataCollection(
        listOf(
            DonutChartData(1200.0f, Sapphire, title = "Food & Groceries"),
            DonutChartData(1500.0f, RobingEggBlue, title = "Rent"),
            DonutChartData(300.0f, MetallicYellow, title = "Gas"),
            DonutChartData(700.0f, OxfordBlue, title = "Online Purchases"),
            DonutChartData(300.0f, VividOrange, title = "Clothing")
        )
    )


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DonutChartTheme {
                Content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content() {
    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Fancy Donut Chart Example") })
        }
    ) { paddingValues ->
        DonutChart(Modifier.padding(paddingValues), data = viewData)
    }
}