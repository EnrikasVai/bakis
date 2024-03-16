package com.example.bakis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.bakis.AnimatedNPieChart
import com.example.bakis.R
import com.example.bakis.composables.CustomBottomNavigationBar
import com.example.bakis.composables.CustomTopAppBar
import com.example.bakis.viewmodel.HomeViewModel

data class InfoData(
    val iconId: Int,
    val text: String,
    val value: String,
    val color: Long,
    val nav: String
)
data class StepData(
    val iconId: Int,
    val text: String,
    val value: String,
    val color: Long,
    val nav: String,
    val goal: Int
)
data class PieData(
    val label: String,
    val value: Int,
    val color: Color,
)
//Calculate goal for now dummy data
const val goal = 5000
const val goalAchieved = 2000
const val goalLeft = goal- goalAchieved

val pieDataPoints = listOf(
    PieData("Goal", goalAchieved, color = Color.Green),
    PieData("Goal-left", goalLeft, color = Color.Yellow)
)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel(), navController: NavHostController) {
    val items = listOf("Dashboard", "Health", "Me")
    val icons = listOf(Icons.Default.Build, Icons.Default.Favorite, Icons.Default.Person)
    val userName by homeViewModel.userName.collectAsState()
    val data = listOf(
        //InfoData(R.drawable.footsteps, "Steps", "2000", 0xFFFF7518, "stepData"),
        //InfoData(R.drawable.bed, "Time In Bed", "8hr 35min",0xFF09bfe8,"sleepData"),
        InfoData(R.drawable.heart_beat, "Heart Rate", "67 bpm",0xFFFF3131,"bpmData"),
        InfoData(R.drawable.bed, "Time In Bed", "20hr 35min", 0xFF09bfe8,"sleepData") ,
        InfoData(R.drawable.bed, "Time In Bed", "20hr 35min", 0xFF09bfe8,"sleepData"),
        InfoData(R.drawable.heart_beat, "Heart Rate", "67 bpm",0xFFFF3131,"bpmData"),
    )
    val stepBoxData = StepData(R.drawable.footsteps, "Steps", "2000", 0xFFFF7518, "stepData", 5000)
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Health Dashboard"
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                navController = navController,
                items = items,
                icons = icons
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .background(Color(0xFF262626)), // Set the background color here

        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = "Welcome, ${userName.ifEmpty { "User" }}", color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                StepBox(stepBoxData, navController)
                data.chunked(2).forEach { pairList ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        pairList.forEach { infoData ->
                            InfoBox(
                                infoData = infoData,
                                navController = navController
                            )
                            if (infoData != pairList.last()) Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
@Composable
fun StepBox(
    infoData: StepData,
    navController: NavHostController
){
    val iconColor = Color(infoData.color)
    val textColor = Color(infoData.color)
    val navTag = infoData.nav
    val goal = infoData.goal
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(start = 28.dp, end = 28.dp, bottom = 8.dp)
            .shadow(8.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF333333))
            .clickable { navController.navigate(navTag) },
    ) {
        Box(modifier = Modifier.height(200.dp).width(170.dp)){
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(id = infoData.iconId),
                    contentDescription = infoData.text,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = infoData.text,
                    color = textColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp)
                )
            }
            Text(
                text = infoData.value,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "Today",
                color = iconColor,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            )
        }
        //Donut CHART
        Box(modifier = Modifier
            .width(190.dp)
            .height(187.dp)
            .padding(16.dp)
        ) {
            AnimatedNPieChart(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(200.dp),
                pieDataPoints = pieDataPoints
            ) {
                Row(
                    modifier = it
                        .padding(20.dp)
                        .fillMaxSize()
                        .padding(16.dp)
                        .scale(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Goal",
                                color = Color.Green,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "2000/\n$goal",
                                color = Color.White,
                                textAlign = TextAlign.End
                            )
                        }
                }
            }
        }
    }
}
@Composable
fun InfoBox(
    infoData: InfoData,
    navController: NavHostController
) {
    val iconColor = Color(infoData.color)
    val textColor = Color(infoData.color)
    val navTag = infoData.nav
    Box(
        modifier = Modifier
            .size(170.dp)
            .shadow(8.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF333333))
            .clickable { navController.navigate(navTag) },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Icon(
                painter = painterResource(id = infoData.iconId),
                contentDescription = infoData.text,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = infoData.text,
                color = textColor,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 4.dp)
            )
        }
        Text(
            text = infoData.value,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
        Text(
            text = "Today",
            color = iconColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        )
    }
}