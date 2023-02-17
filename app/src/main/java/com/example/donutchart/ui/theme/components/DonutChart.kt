package com.example.donutchart.ui.theme.components

import android.util.Log
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt

private const val TOTAL_ANGLE = 360.0f

enum class DonutChartGapSize(var percentage: Float) {
    SMALL(0.04f),
    STANDARD(0.04f),
    LARGE(0.04f)
}
private const val GAP_PERCENTAGE_STANDARD = 0.04f
private const val GAP_PERCENTAGE_SMALL = 0.04f
private const val GAP_PERCENTAGE_LARGE = 0.04f


data class DonutChartData(
    val amount: Float,
    val color: Color,
    val title: String = ""
)

data class DonutChartDataCollection(
    var items: List<DonutChartData>
) {
    internal var totalAmount: Float = items.sumOf { it.amount.toDouble() }.toFloat()
        private set
}

private fun DonutChartDataCollection.calculateGap(gapPercentage: Float): Float {
    if (this.items.isEmpty()) return 0f

    return (this.totalAmount / this.items.size) * gapPercentage
}

private fun DonutChartDataCollection.getTotalAmountWithGapIncluded(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    return this.totalAmount + (this.items.size * gap)
}

private fun DonutChartDataCollection.calculateGapAngle(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    val totalAmountWithGap = this.getTotalAmountWithGapIncluded(gapPercentage)

    return (gap / totalAmountWithGap) * TOTAL_ANGLE
}

private fun DonutChartDataCollection.findSweepAngle(
    index: Int,
    gapPercentage: Float
): Float {
    val amount = items[index].amount
    val gap = this.calculateGap(gapPercentage)
    val totalWithGap = getTotalAmountWithGapIncluded(gapPercentage)
    val gapAngle = this.calculateGapAngle(gapPercentage)
    return ((((amount + gap) / totalWithGap) * TOTAL_ANGLE)) - gapAngle
}

private data class Angles(val start: Float, val end: Float, val color: Color)
private var anglesList: MutableList<Angles> = mutableListOf()

private val STROKE_SIZE_UNSELECTED = 40.dp
private val STROKE_SIZE_SELECTED = 50.dp

private class DonutChartState(
    val state: State = State.Unselected
) {
    val stroke: Dp
        get() = when (state) {
            State.Selected -> STROKE_SIZE_SELECTED
            State.Unselected -> STROKE_SIZE_UNSELECTED
        }

    enum class State {
        Selected, Unselected
    }
}


@Preview(showBackground = true)
@Composable
fun DonutChart(
    @PreviewParameter(SampleDonutCharProvider::class) data: DonutChartDataCollection,
    gapPercentage: Float = GAP_PERCENTAGE_STANDARD
) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val animationTargetState = (0..data.items.size).map {
        remember { mutableStateOf(DonutChartState()) }
    }
    val animValues = (0..data.items.size).map {
        animateDpAsState(
            targetValue = animationTargetState[it].value.stroke,//animationTargetState[it].value,
            animationSpec = TweenSpec(700)
        )
    }
    val gapAngle = data.calculateGapAngle(gapPercentage)
    var center = Offset(0f, 0f)

    Column {
        Box(Modifier.size(400.dp), contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .border(1.dp, Green)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { tapOffset ->
                                val normalized = tapOffset.findNormalizedPointFromTouch(center)
                                val touchAngle = calculateTouchAngleAccordingToCanvas(center, normalized)
                                val distance = findTouchDistanceFromCenter(center, normalized)

                                val prevSelected = selectedIndex
                                var newDataTapped = false
                                anglesList.forEachIndexed { ind, angle ->
                                    if (touchAngle > angle.start && touchAngle < angle.start + angle.end) {
                                        if (distance > (center.x - animationTargetState[ind].value.stroke.toPx())) { // since it's a square x or y should be the same
                                            selectedIndex = ind
                                            newDataTapped = true
                                        }
                                    }
                                }

                                if (selectedIndex >= 0 && newDataTapped) {
                                    animationTargetState[selectedIndex].value = DonutChartState(DonutChartState.State.Selected)
                                }
                                if (prevSelected >= 0) {
                                    animationTargetState[prevSelected].value = DonutChartState(DonutChartState.State.Unselected)
                                }
                            }
                        )
                    },
                onDraw = {
                    center = this.center
                    anglesList.clear()
                    // Head
                    var lastAngle = 0f
                    data.items.forEachIndexed { ind, item ->
                        val sweepAngle = data.findSweepAngle(ind, gapPercentage)
                        anglesList.add(Angles(lastAngle, sweepAngle, item.color))
                        drawArc(
                            color = item.color,
                            startAngle = lastAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(animValues[ind].value.toPx(), cap = StrokeCap.Butt),
                            size = Size(size.width, size.height)
                        )
                        lastAngle += sweepAngle + gapAngle
                    }
                }
            )
            Column {
                Text("Value is ${if (selectedIndex >= 0) data.items[selectedIndex].amount else "NONE"}")
            }
        }
    }
}

private fun findTouchDistanceFromCenter(center: Offset, touch: Offset) =
    sqrt((touch.x - center.x).pow(2) + (touch.y - center.y).pow(2))

private fun Offset.findNormalizedPointFromTouch(canvasCenter: Offset) =
    Offset(this.x, canvasCenter.y + (canvasCenter.y - this.y))

private fun calculateTouchAngleAccordingToCanvas(canvasCenter: Offset, normalizedPoint: Offset): Float {
    val angle = calculateTouchAngleInDegrees(canvasCenter, normalizedPoint)
    return adjustAngleToCanvas(angle).toFloat()
}

private fun calculateTouchAngleInDegrees(canvasCenter: Offset, normalizedPoint: Offset): Double {
    val touchInRadian = kotlin.math.atan2(normalizedPoint.y - canvasCenter.y,
        normalizedPoint.x - canvasCenter.x)
    return touchInRadian * -180 / Math.PI // Convert radians to angel in degrees
}

// Start from 4th quadrant going to 1st quadrant, degrees ranging from 0 to 360
private fun adjustAngleToCanvas(angle: Double) = (angle + TOTAL_ANGLE) % TOTAL_ANGLE

class SampleDonutCharProvider: PreviewParameterProvider<DonutChartDataCollection> {
    override val values = sequenceOf(
        DonutChartDataCollection(
            listOf(
                DonutChartData(1200.0f, Color.Black),
                DonutChartData(2300.0f, Color.Gray),
                DonutChartData(300.0f, Color.Red),
                DonutChartData(700.0f, Color.Blue),
                DonutChartData(1200.0f, Color.Magenta)
            )
        )
    )
}

