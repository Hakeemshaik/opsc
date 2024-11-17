package com.example.part2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.contains as contains1

// Data class for TimesheetEntry
data class TimesheetEntry(
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val hoursWorked: Double
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimesheetApp()
        }
    }
}

@Composable
fun TimesheetApp() {
    var currentScreen by remember { mutableStateOf("Login") }
    val categoriesList = remember { mutableStateListOf<String>() }
    val timesheetEntriesMap = remember { mutableStateMapOf<String, MutableList<TimesheetEntry>>() }
    var selectedCategory by remember { mutableStateOf("") }
    var dailyGoalMin by remember { mutableStateOf(4) }
    var dailyGoalMax by remember { mutableStateOf(8) }

    when (currentScreen) {
        "Login" -> LoginScreen(onLoginSuccess = { currentScreen = "Categories" })
        "Categories" -> CategoriesScreen(
            categoriesList = categoriesList,
            onCategorySelected = { category ->
                selectedCategory = category
                currentScreen = "TimesheetEntry"
            },
            onAddCategory = { category ->
                if (category.isNotBlank() && !categoriesList.contains(category)) {
                    categoriesList.add(category)
                }
            },
            onViewProgress = { currentScreen = "Progress" }
        )
        "TimesheetEntry" -> TimesheetEntryScreen(
            category = selectedCategory,
            timesheetEntriesMap = timesheetEntriesMap,
            onBack = { currentScreen = "Categories" }
        )
        "Progress" -> ProgressScreen(
            timesheetEntriesMap = timesheetEntriesMap,
            dailyGoalMin = dailyGoalMin,
            dailyGoalMax = dailyGoalMax,
            onBack = { currentScreen = "Categories" },
            onGoalUpdated = { minGoal, maxGoal ->
                dailyGoalMin = minGoal
                dailyGoalMax = maxGoal
            }
        )
    }
}


@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (username == "user" && password == "password") { // Replace with real authentication logic
                onLoginSuccess()
            } else {
                errorMessage = "Invalid username or password."
            }
        }) {
            Text("Login")
        }
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red)
        }
    }
}

@Composable
fun CategoriesScreen(
    categoriesList: List<String>,
    onCategorySelected: (String) -> Unit,
    onAddCategory: (String) -> Unit,
    onViewProgress: () -> Unit
) {
    var newCategory by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = newCategory,
            onValueChange = { newCategory = it },
            label = { Text("New Category") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onAddCategory(newCategory)
            newCategory = ""
        }) {
            Text("Add Category")
        }
        Spacer(modifier = Modifier.height(16.dp))
        categoriesList.forEach { category ->
            Text(
                text = category,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategorySelected(category) }
                    .padding(8.dp)
                    .background(Color.LightGray)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onViewProgress) {
            Text("View Progress")
        }
    }
}

@Composable
fun TimesheetEntryScreen(
    category: String,
    timesheetEntriesMap: MutableMap<String, MutableList<TimesheetEntry>>,
    onBack: () -> Unit
) {
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Add Timesheet Entry for $category")
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (yyyy-MM-dd)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = startTime,
            onValueChange = { startTime = it },
            label = { Text("Start Time (HH:mm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = endTime,
            onValueChange = { endTime = it },
            label = { Text("End Time (HH:mm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            val hoursWorked = calculateHoursWorked(startTime, endTime)
            if (hoursWorked > 0) {
                val entry = TimesheetEntry(date, startTime, endTime, description, hoursWorked)
                timesheetEntriesMap.getOrPut(category) { mutableListOf() }.add(entry)
                onBack()
            }
        }) {
            Text("Save Entry")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}

@Composable
fun ProgressScreen(
    timesheetEntriesMap: MutableMap<String, MutableList<TimesheetEntry>>,
    dailyGoalMin: Int,
    dailyGoalMax: Int,
    onBack: () -> Unit,
    onGoalUpdated: (Int, Int) -> Unit
) {
    var newGoalMin by remember { mutableStateOf(dailyGoalMin) }
    var newGoalMax by remember { mutableStateOf(dailyGoalMax) }

    val dailyHours = calculateDailyHours(timesheetEntriesMap)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Progress")
        Spacer(modifier = Modifier.height(8.dp))

        // Display and update goal fields
        Text("Set Daily Work Goal:")
        Row {
            Text("Min Goal: ")
            TextField(
                value = newGoalMin.toString(),
                onValueChange = {
                    newGoalMin = it.toIntOrNull() ?: 0
                    onGoalUpdated(newGoalMin, newGoalMax)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(100.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Max Goal: ")
            TextField(
                value = newGoalMax.toString(),
                onValueChange = {
                    newGoalMax = it.toIntOrNull() ?: 0
                    onGoalUpdated(newGoalMin, newGoalMax)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the worked hours with color based on goals
        dailyHours.forEach { (date, hours) ->
            val progressColor = when {
                if ((dailyGoalMin..dailyGoalMax).contains1<Any>(element = hours)) {
                    true
                } else {
                    false
                } -> {
                    Color.Green
                }
                hours < dailyGoalMin -> Color.Red
                else -> Color.Yellow
            }

            Text(
                text = "$date: ${hours}h",
                color = progressColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}


fun calculateHoursWorked(startTime: String, endTime: String): Double {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return try {
        val start = format.parse(startTime)?.time ?: 0
        val end = format.parse(endTime)?.time ?: 0
        (end - start) / (1000 * 60 * 60).toDouble()
    } catch (e: Exception) {
        0.0
    }
}

fun calculateDailyHours(
    timesheetEntriesMap: MutableMap<String, MutableList<TimesheetEntry>>
): Map<String, Double> {
    return timesheetEntriesMap.values.flatten().groupBy { it.date }
        .mapValues { (_, entries) -> entries.sumOf { it.hoursWorked } }
}









