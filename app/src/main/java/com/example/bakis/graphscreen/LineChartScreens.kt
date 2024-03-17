package com.example.bakis.graphscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar
import com.example.bakis.rememberMarker
import com.example.bakis.viewmodel.HomeViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//data example
val bpmPerDay = listOf(60f, 50f, 70f, 50f, 80f, 60f, 87f)
val bpmPerMonth = listOf(60f, 50f, 70f, 50f, 80f, 60f, 87f,60f, 50f, 70f, 50f, 80f)
val averageBpmDay = bpmPerDay.average()
val averageBpmMonth = bpmPerMonth.average()
var averageBpmCount = averageBpmMonth.toInt()

@Composable
fun HeartRateScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "BPM Data",
                onEditClick = { /* ... */ },
                showEditIcon = false // Only show the edit icon in the ProfileScreen
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = listOf("Dashboard", "Health", "Me"),
                icons = listOf(Icons.Default.Home, Icons.Default.Favorite, Icons.Default.Person)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .background(Color(0xFF262626)) // Set the background color here
                .padding(top = 40.dp, start = 10.dp)
                .padding(paddingValues) // Apply the padding here
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                if(selectedLabel.value == "Week")
                    averageBpmCount = averageBpmDay.toInt()

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                ) {
                    Box(modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            text="Average BPM: $averageBpmCount",
                            color = Color.White,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    if(selectedLabel.value == "Week") {
                        Chart1(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp),
                            bpmData = bpmPerDay, // Pass bpmPerDay data
                            axisFormatter = bottomAxisValueFormatter // Week formatter
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart1(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp) ,
                            bpmData = bpmPerMonth,
                            axisFormatter = bottomAxisValueFormatterMonth
                        )
                    }
                    Row(modifier = Modifier.padding(top = 20.dp, end = 10.dp).fillMaxWidth().padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (label == selectedLabel.value) Color(0xFFFF3131) else Color.DarkGray, shape = shape)
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
internal fun Chart1(
    modifier: Modifier,
    bpmData: List<Float>, // Parameter to accept BPM data
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(bpmData) { // Trigger re-composition on bpmData change
        withContext(Dispatchers.Default) {
            modelProducer.tryRunTransaction {
                lineSeries {
                    series(
                        x.indices.toList(), // Assuming 'x' aligns with the size of bpmData
                        bpmData
                    )
                }
            }
        }
    }
    ComposeChart1(modelProducer, modifier, axisFormatter)
}
@Composable
private fun ComposeChart1(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>
) {
    val marker = rememberMarker()
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                listOf(
                    rememberLineSpec(
                        DynamicShaders
                            .color(Color(0xFFFF3131)
                            )
                    )
                )
            ),
            startAxis = rememberStartAxis(
                label = rememberAxisLabelComponent(Color.White),
                axis = rememberAxisLineComponent(Color.White),
                guideline = rememberAxisGuidelineComponent(Color.White)
            ),
            bottomAxis = rememberBottomAxis(
                label = rememberAxisLabelComponent(Color.White),
                axis = rememberAxisLineComponent(Color.White),
                guideline = rememberAxisGuidelineComponent(Color.White),
                tickLength = 10.dp,
                valueFormatter = axisFormatter,
                ),
            //persistentMarkers = mapOf(PERSISTENT_MARKER_X to marker),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        marker = marker,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}
//private const val PERSISTENT_MARKER_X = 7f

private val x = (1..50).toList()
val daysOfWeek2 = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val bottomAxisValueFormatter =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        // Use daysOfWeek to get the label for each x value
        daysOfWeek2[x.toInt() % daysOfWeek2.size]
    }
val months2 = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Aug","Sep","Oct", "Nov", "Dec")
private val bottomAxisValueFormatterMonth =
    AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        // Use daysOfWeek to get the label for each x value
        months2[x.toInt() % months2.size]
    }