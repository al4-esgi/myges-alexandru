package com.vlxx.myges.ui.screens.authenticated.grades.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vlxx.myges.R
import com.vlxx.myges.data.dtos.CourseDto
import com.vlxx.myges.ui.screens.authenticated.grades.viewModel.GradesUiState
import com.vlxx.myges.ui.screens.authenticated.grades.viewModel.GradesViewModel
import com.vlxx.myges.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import java.util.*

@Composable
fun GradesScreen(
    viewModel: GradesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is GradesUiState.Loading -> LoadingContent()
        is GradesUiState.Success -> {
            GradesContent(
                courses = state.filteredCourses,
                allCourses = state.allCourses,
                selectedYear = state.selectedYear,
                selectedTrimester = state.selectedTrimester,
                availableTrimesters = state.availableTrimesters,
                onYearChange = viewModel::loadGrades,
                onTrimesterChange = viewModel::filterByTrimester
            )
        }
        is GradesUiState.Error -> {
            ErrorContent(
                message = state.message,
                onRetry = { viewModel.loadGrades(2025) }
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.grades_loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.grades_error),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onRetry) { Text(stringResource(R.string.grades_retry)) }
        }
    }
}

@Composable
private fun GradesContent(
    courses: List<CourseDto>,
    allCourses: List<CourseDto>,
    selectedYear: Int,
    selectedTrimester: String?,
    availableTrimesters: List<String>,
    onYearChange: (Int) -> Unit,
    onTrimesterChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Compute overall average from courses with a valid average
    val coursesWithGrades = allCourses.filter { (it.average ?: 0.0) > 0.0 }
    val overallAverage = if (coursesWithGrades.isNotEmpty())
        coursesWithGrades.mapNotNull { it.average }.average()
    else null

    // Group visible courses by trimester
    val grouped = courses.groupBy { it.trimesterName ?: "Autre" }
        .entries.sortedBy { it.key }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Top header ──────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        if (overallAverage != null) {
                            Text(
                                text = "Moyenne générale : ${String.format(Locale.getDefault(), "%.2f", overallAverage)}/20",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        }
                    }
                    YearSelector(selectedYear = selectedYear, onYearChange = onYearChange)
                }
            }
        }

        // ── Trimester filter chips ───────────────────────────────────
        if (availableTrimesters.isNotEmpty()) {
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedTrimester == null,
                            onClick = { onTrimesterChange(null) },
                            label = { Text("Tous") }
                        )
                    }
                    items(availableTrimesters) { trimester ->
                        FilterChip(
                            selected = selectedTrimester == trimester,
                            onClick = { onTrimesterChange(trimester) },
                            label = { Text(trimester) }
                        )
                    }
                }
            }
        }

        // ── Empty state ──────────────────────────────────────────────
        if (courses.isEmpty()) {
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.grades_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ── Grouped sections ─────────────────────────────────────────
        grouped.forEach { (trimesterName, trimesterCourses) ->
            // Section header
            item(key = "header_$trimesterName") {
                val trimesterAvg = trimesterCourses.filter { (it.average ?: 0.0) > 0.0 }
                    .mapNotNull { it.average }.let { list ->
                        if (list.isNotEmpty()) list.average() else null
                    }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = trimesterName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    if (trimesterAvg != null) {
                        Surface(
                            color = gradeColorFor(trimesterAvg).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Moy. ${String.format(Locale.getDefault(), "%.2f", trimesterAvg)}/20",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = gradeColorFor(trimesterAvg),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            // Course cards
            items(trimesterCourses, key = { it.rcId ?: it.course.orEmpty() }) { course ->
                CourseCard(
                    course = course,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun YearSelector(selectedYear: Int, onYearChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val years = (2025 downTo 2020).toList()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f))
            )
        ) {
            Text(text = selectedYear.toString(), fontWeight = FontWeight.Bold)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = { onYearChange(year); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun CourseCard(course: CourseDto, modifier: Modifier = Modifier) {
    val hasGrades = !course.grades.isNullOrEmpty()
    val hasAverage = (course.average ?: 0.0) > 0.0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent bar colored by grade
            val accentColor = if (hasAverage) gradeColorFor(course.average!!)
            else MaterialTheme.colorScheme.outlineVariant
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Course name + average badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Name (clean up "M1 - t1 -" prefix for readability)
                    val displayName = course.course
                        ?.replace(Regex("^M\\d+ - t\\d+ - ", RegexOption.IGNORE_CASE), "")
                        ?.replaceFirstChar { it.uppercase() }
                        ?: "—"

                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.width(8.dp))

                    // Average pill
                    if (hasAverage) {
                        val avgColor = gradeColorFor(course.average!!)
                        Surface(
                            color = avgColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%.2f", course.average),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    } else if (!course.letterMark.isNullOrBlank() && course.letterMark == "F") {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "En attente",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Teacher
                val teacherName = listOfNotNull(
                    course.teacherCivility?.takeIf { it.isNotBlank() },
                    course.teacherFirstName?.takeIf { it.isNotBlank() && it.lowercase() != "temporaire" },
                    course.teacherLastName?.takeIf { it.isNotBlank() && it.lowercase() != "temporaire" }
                ).joinToString(" ")

                if (teacherName.isNotBlank()) {
                    Text(
                        text = teacherName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // ECTS / Coef chips + letter mark
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!course.ects.isNullOrBlank() && course.ects != "N.C.") {
                        MiniChip(text = "ECTS ${course.ects}")
                    }
                    if (!course.coef.isNullOrBlank() && course.coef != "N.C.") {
                        MiniChip(text = "Coef ${course.coef}")
                    }
                    if (!course.letterMark.isNullOrBlank() && course.letterMark != "F") {
                        MiniChip(
                            text = course.letterMark,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Grades breakdown (only if there are grades)
                if (hasGrades || (course.exam ?: 0.0) > 0.0 || (course.ccAverage ?: 0.0) > 0.0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // CC grades
                        if (hasGrades) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Notes CC",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    course.grades.forEach { g ->
                                        GradePill(grade = g)
                                    }
                                }
                            }
                        }

                        // CC average
                        if ((course.ccAverage ?: 0.0) > 0.0) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Moy. CC",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f", course.ccAverage),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = gradeColorFor(course.ccAverage!!)
                                )
                            }
                        }

                        // Exam
                        if ((course.exam ?: 0.0) > 0.0) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Examen",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f", course.exam),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = gradeColorFor(course.exam!!)
                                )
                            }
                        }
                    }
                }

                // Absences / lates warning
                if ((course.absences ?: 0) > 0 || (course.lates ?: 0) > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if ((course.absences ?: 0) > 0) {
                            Text(
                                text = "⚠️ ${course.absences} absence(s)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        if ((course.lates ?: 0) > 0) {
                            Text(
                                text = "⏰ ${course.lates} retard(s)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GradePill(grade: Double) {
    val color = gradeColorFor(grade)
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = String.format(Locale.getDefault(), "%.1f", grade),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun MiniChip(
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun gradeColorFor(grade: Double): Color = when {
    grade >= 16 -> Color(0xFF2196F3)  // Blue – excellent
    grade >= 14 -> Color(0xFF4CAF50)  // Green – good
    grade >= 12 -> Color(0xFF8BC34A)  // Light green – above average
    grade >= 10 -> Color(0xFFFF9800)  // Orange – passing
    else        -> Color(0xFFF44336)  // Red – failing
}

@Preview(showBackground = true)
@Composable
private fun GradesContentPreview() {
    AppTheme {
        GradesContent(
            courses = listOf(
                CourseDto(
                    course = "M1 - t1 - clean code", code = "", grades = listOf(15.0, 18.0, 17.5),
                    bonus = 0.0, exam = 18.8, average = 17.7, trimester = 489,
                    trimesterName = "Semestre 1", year = 2025, rcId = 330963,
                    ects = "3.0", coef = "3.0", teacherCivility = "M.", teacherFirstName = "Léo",
                    teacherLastName = "CHIRON", absences = 0, lates = 0, letterMark = "A", ccAverage = 16.6
                ),
                CourseDto(
                    course = "M1 - t1 - anglais professionnel", code = "", grades = listOf(14.0, 15.0),
                    bonus = 0.0, exam = 16.5, average = 15.25, trimester = 489,
                    trimesterName = "Semestre 1", year = 2025, rcId = 324129,
                    ects = "2.0", coef = "2.0", teacherCivility = "Mme", teacherFirstName = "Mia",
                    teacherLastName = "FAULQUIER", absences = 0, lates = 0, letterMark = "B+", ccAverage = 14.0
                ),
                CourseDto(
                    course = "M1 - t2 - frameworks jee", code = "", grades = emptyList(),
                    bonus = 0.0, exam = null, average = null, trimester = 557,
                    trimesterName = "Semestre 2", year = 2025, rcId = 330966,
                    ects = "4.0", coef = "4.0", teacherCivility = "M.", teacherFirstName = "Nicolas",
                    teacherLastName = "FAESSEL", absences = 0, lates = 0, letterMark = "F", ccAverage = 0.0
                )
            ),
            allCourses = emptyList(),
            selectedYear = 2025,
            selectedTrimester = null,
            availableTrimesters = listOf("Semestre 1", "Semestre 2"),
            onYearChange = {},
            onTrimesterChange = {}
        )
    }
}
