package com.example.donutchart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.donutchart.misc.toMoneyFormat
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun Content() {
    Scaffold(
        topBar = {
            Text("Fancy Donut Chart",
                style = Typography.displaySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp))
        }
    ) { paddingValues ->
        DonutChart(Modifier.padding(paddingValues), data = viewData) { selected ->
            AnimatedContent(targetState = selected) {
                val amount = it?.amount ?: viewData.totalAmount
                val text = it?.title ?: "Total"

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$${amount.toMoneyFormat(true)}",
                        style = moneyAmountStyle, color = PetroleumGray)
                    Text(text, style = itemTextStyle, color = PetroleumLightGray)
                }
            }
        }
    }
}