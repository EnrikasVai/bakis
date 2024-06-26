package com.example.bakis.graphscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar
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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun HeartRateScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val today = LocalDate.now()

    // Updated logic for rotating the daysOfWeek2 and months2 lists
    val daysOfWeek2 = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val currentDayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
    val currentDayIndex = (daysOfWeek2.indexOf(currentDayOfWeek) + 1) % daysOfWeek2.size
    val rotatedDaysOfWeek = (daysOfWeek2 + daysOfWeek2).slice(currentDayIndex until currentDayIndex + daysOfWeek2.size)
    val months2 = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val currentMonth = today.month.getDisplayName(TextStyle.SHORT, Locale.US)
    val currentMonthIndex = (months2.indexOf(currentMonth) + 1) % months2.size
    val rotatedMonths = (months2 + months2).slice(currentMonthIndex until currentMonthIndex + months2.size)

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        rotatedDaysOfWeek[(x % rotatedDaysOfWeek.size).toInt()]
    }

    val bottomAxisValueFormatterMonth = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        rotatedMonths[(x % rotatedMonths.size).toInt()]
    }
    val heartRateDataList by viewModel.weeklyHeartRateCountsMinMax.collectAsState()

    //data example
    val bpmPerDay by viewModel.weeklyHeartRateCounts.collectAsState()
    val bpmPerMonth by viewModel.monthlyHeartRateCounts.collectAsState()
    val averageBpmDay = bpmPerDay.filter { it > 0 }.average()
    val averageBpmMonth = bpmPerMonth.filter { it > 0 }.average()

    val markerText = "Average BPM:"
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Heart Rate Data",
                onEditClick = { /* ... */ },
                showEditIcon = false,
                showBackButton = true,
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
                .padding(start = 10.dp)
                .padding(paddingValues) // Apply the padding here
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                ) {
                    Box(modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        if(selectedLabel.value == "Week")
                            Text(
                                text="Average BPM: ${averageBpmDay.toInt()}",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        else
                            Text(
                                text="Average BPM: ${averageBpmMonth.toInt()}",
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
                            axisFormatter = bottomAxisValueFormatter, // Week formatter
                            markerText = markerText
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart1(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp) ,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            bpmData = bpmPerMonth,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xFFFF3131) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp)) {
                            Text("Heart rate is measured in beats per minute (bpm), and can be elevated by things like activity, stress, or excitement.",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                HeartRateList(heartRateDataList = heartRateDataList)
            }
        }
    }
}
@Composable
fun HeartRateRestingScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val today = LocalDate.now()

    // Updated logic for rotating the daysOfWeek2 and months2 lists
    val daysOfWeek2 = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val currentDayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
    val currentDayIndex = (daysOfWeek2.indexOf(currentDayOfWeek) + 1) % daysOfWeek2.size
    val rotatedDaysOfWeek = (daysOfWeek2 + daysOfWeek2).slice(currentDayIndex until currentDayIndex + daysOfWeek2.size)
    val months2 = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val currentMonth = today.month.getDisplayName(TextStyle.SHORT, Locale.US)
    val currentMonthIndex = (months2.indexOf(currentMonth) + 1) % months2.size
    val rotatedMonths = (months2 + months2).slice(currentMonthIndex until currentMonthIndex + months2.size)

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        rotatedDaysOfWeek[(x % rotatedDaysOfWeek.size).toInt()]
    }

    val bottomAxisValueFormatterMonth = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->
        rotatedMonths[(x % rotatedMonths.size).toInt()]
    }


    //data example
    val bpmRestingWeek by viewModel.weeklyHeartRateCountsResting.collectAsState()
    val bpmPerMonthResting by viewModel.monthlyHeartRateCountsResting.collectAsState()
    val averageBpmDay = bpmRestingWeek.filter { it > 0 }.average()
    val averageBpmMonth = bpmPerMonthResting.filter { it > 0 }.average()

    val markerText = "Average BPM:"
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Heart Rate Resting",
                onEditClick = { /* ... */ },
                showEditIcon = false,
                showBackButton = true,
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
                .background(Color(0xFF262626))
                .padding(start = 10.dp)
                .padding(paddingValues)
        ) {
            item {
                val labels = listOf("Week", "Month")
                val selectedLabel = remember { mutableStateOf("Week") }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                ) {
                    Box(modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        if(selectedLabel.value == "Week")
                            Text(
                                text="Average Resting BPM: ${averageBpmDay.toInt()}",
                                color = Color.White,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        else
                            Text(
                                text="Average Resting BPM: ${averageBpmMonth.toInt()}",
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
                            bpmData = bpmRestingWeek,
                            axisFormatter = bottomAxisValueFormatter,
                            markerText = markerText
                        )
                    }
                    if(selectedLabel.value == "Month") {
                        Chart1(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(350.dp) ,
                            axisFormatter = bottomAxisValueFormatterMonth,
                            bpmData = bpmPerMonthResting,
                            markerText = markerText
                        )
                    }
                    Row(modifier = Modifier
                        .padding(top = 20.dp, end = 10.dp)
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)) {
                        labels.forEachIndexed { index, label ->
                            val shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                labels.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RectangleShape
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (label == selectedLabel.value) Color(0xFFFF3131) else Color.DarkGray,
                                        shape = shape
                                    )
                                    .clickable { selectedLabel.value = label }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = label, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp)) {
                            Text("Resting heart rate is measured in beats per minute, and can be useful way to gauge your overall health and fitness",
                                color = Color.White,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
@Composable
fun HeartRateList(heartRateDataList: List<Triple<String, Float, Float>>) {
    Column {
        Text("BPM Range This Week",color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        heartRateDataList.forEach { (date, min, max) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, end = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.DarkGray)
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = date, Modifier.padding(bottom = 8.dp),color = Color.LightGray)
                    if(min > 0)
                        Text(text = "${min.toInt()} - ${max.toInt()} bpm", fontSize = 20.sp)
                    else
                        Text(text = "No Data", fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
internal fun Chart1(
    modifier: Modifier,
    bpmData: List<Float>, // Parameter to accept BPM data
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    markerText: String
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
    ComposeChart1(modelProducer, modifier, axisFormatter, bpmData, markerText)
}
@Composable
private fun ComposeChart1(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier,
    axisFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>,
    bpmData: List<Float>, // Parameter to accept BPM data
    markerText: String
) {
    val marker = rememberMarker(data = bpmData, markerText = markerText)
    val isBPMData = markerText.contains("BPM", ignoreCase = true)
    val startAxisH =if(isBPMData){
        AxisValueFormatter { value, _, _ ->
            "${value.toInt()}bpm"
        }
    }else {
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _, _ ->
            "${value.toInt()}"
        }
    }
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
                guideline = rememberAxisGuidelineComponent(Color.White),
                valueFormatter = startAxisH
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