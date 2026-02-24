package com.vlxx.myges.ui.screens.authenticated.agenda.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vlxx.myges.R
import com.vlxx.myges.data.dtos.AgendaEventDto
import com.vlxx.myges.data.dtos.DisciplineDto
import com.vlxx.myges.data.dtos.RoomDto
import com.vlxx.myges.ui.screens.authenticated.agenda.viewModel.AgendaUiState
import com.vlxx.myges.ui.screens.authenticated.agenda.viewModel.AgendaViewModel
import com.vlxx.myges.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AgendaScreen(
    viewModel: AgendaViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is AgendaUiState.Loading -> {
            LoadingContent()
        }
        is AgendaUiState.Success -> {
            AgendaContent(
                events = state.events,
                onRefresh = viewModel::loadAgenda
            )
        }
        is AgendaUiState.Error -> {
            ErrorContent(
                message = state.message,
                onRetry = viewModel::loadAgenda
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.agenda_loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.agenda_error),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.agenda_retry))
            }
        }
    }
}

@Composable
private fun AgendaContent(
    events: List<AgendaEventDto>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Group events by day
    val eventsByDay = events
        .sortedBy { it.startDate ?: 0L }
        .groupBy { event ->
            event.startDate?.let { timestamp ->
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = timestamp
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                calendar.timeInMillis
            } ?: 0L
        }
        .toSortedMap()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.agenda_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = stringResource(R.string.agenda_refresh)
                )
            }
        }

        if (events.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.agenda_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Events list grouped by day
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                eventsByDay.forEach { (dayTimestamp, dayEvents) ->
                    // Day header
                    item {
                        DayHeader(dayTimestamp = dayTimestamp)
                    }

                    // Events for this day
                    items(dayEvents) { event ->
                        EventCard(event = event)
                    }

                    // Spacer between days
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(
    dayTimestamp: Long,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault())
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val tomorrow = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val dateText = when (dayTimestamp) {
        today.timeInMillis -> "Aujourd'hui • ${dateFormat.format(Date(dayTimestamp))}"
        tomorrow.timeInMillis -> "Demain • ${dateFormat.format(Date(dayTimestamp))}"
        else -> dateFormat.format(Date(dayTimestamp))
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = dateText.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun EventCard(
    event: AgendaEventDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Event name
            Text(
                text = event.name ?: stringResource(R.string.agenda_event_untitled),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Time
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = formatEventTime(event.startDate, event.endDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Teacher
            if (!event.teacher.isNullOrBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = event.teacher,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Room & Campus (from rooms array)
            val firstRoom = event.rooms?.firstOrNull()
            if (firstRoom != null && (!firstRoom.name.isNullOrBlank() || !firstRoom.campus.isNullOrBlank())) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = buildString {
                            firstRoom.name?.let { append(it) }
                            if (!firstRoom.name.isNullOrBlank() && !firstRoom.campus.isNullOrBlank()) {
                                append(" - ")
                            }
                            firstRoom.campus?.let { append(it) }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Discipline (from discipline object)
            if (!event.discipline?.name.isNullOrBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = event.discipline.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Type and Modality badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!event.type.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = event.type,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (!event.modality.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = event.modality,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatEventTime(startDate: Long?, endDate: Long?): String {
    if (startDate == null) return ""

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val start = Date(startDate)
    val startTime = timeFormat.format(start)

    return if (endDate != null) {
        val end = Date(endDate)
        val endTime = timeFormat.format(end)
        "$startTime - $endTime"
    } else {
        startTime
    }
}

@Preview(showBackground = true)
@Composable
private fun AgendaScreenPreview() {
    AppTheme {
        AgendaContent(
            events = listOf(
                AgendaEventDto(
                    reservationId = 1,
                    name = "M1 - t2 - jetpack compose android",
                    teacher = "M. AZIZA",
                    type = "Cours",
                    modality = "Présentiel",
                    startDate = System.currentTimeMillis(),
                    endDate = System.currentTimeMillis() + 7200000,
                    rooms = listOf(
                        RoomDto(
                            roomId = 996,
                            name = "AIX - SALLE 113",
                            floor = "1er étage",
                            campus = "AIX-EN-PROVENCE",
                            color = "#676767",
                            latitude = "43.5135492",
                            longitude = "5.4258471"
                        )
                    ),
                    discipline = DisciplineDto(
                        name = "M1 - t2 - jetpack compose android",
                        teacher = "M. AZIZA",
                        trimester = "Semestre 2",
                        year = 2025,
                        studentGroupName = "4ESGI ALT - AL - AIX - T2"
                    ),
                    promotion = "",
                    comment = null
                ),
                AgendaEventDto(
                    reservationId = 2,
                    name = "M1 - t2 - frameworks jee",
                    teacher = "M. FAESSEL",
                    type = "Cours",
                    modality = "Présentiel",
                    startDate = System.currentTimeMillis() + 86400000,
                    endDate = System.currentTimeMillis() + 86400000 + 7200000,
                    rooms = listOf(
                        RoomDto(
                            roomId = 667,
                            name = "AIX - SALLE 112",
                            floor = "1er étage",
                            campus = "AIX-EN-PROVENCE",
                            color = "#676767",
                            latitude = "43.5135492",
                            longitude = "5.4258471"
                        )
                    ),
                    discipline = DisciplineDto(
                        name = "M1 - t2 - frameworks jee",
                        teacher = "M. FAESSEL",
                        trimester = "Semestre 2",
                        year = 2025,
                        studentGroupName = "4ESGI ALT - AL - AIX - T2"
                    ),
                    promotion = "",
                    comment = null
                )
            ),
            onRefresh = {}
        )
    }
}
