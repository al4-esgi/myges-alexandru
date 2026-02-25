package com.vlxx.myges.ui.screens.authenticated.agenda.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        is AgendaUiState.Loading -> LoadingContent()
        is AgendaUiState.Success -> AgendaContent(events = state.events, onRefresh = viewModel::loadAgenda)
        is AgendaUiState.Error -> ErrorContent(message = state.message, onRetry = viewModel::loadAgenda)
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator()
            Text(text = stringResource(R.string.agenda_loading), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            Text(text = stringResource(R.string.agenda_error), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onRetry) { Text(stringResource(R.string.agenda_retry)) }
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
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                calendar.timeInMillis
            } ?: 0L
        }
        .toSortedMap()

    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {

        // ── Top header ────────────────────────────────────────────────
        item {
            Box(
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
                            text = stringResource(R.string.agenda_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = stringResource(R.string.agenda_subtitle, events.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.agenda_refresh),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // ── Empty state ───────────────────────────────────────────────
        if (events.isEmpty()) {
            item {
                Box(modifier = Modifier.fillParentMaxWidth().padding(64.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = stringResource(R.string.agenda_empty), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // ── Days grouped ──────────────────────────────────────────────
        eventsByDay.forEach { (dayTimestamp, dayEvents) ->
            // Day header
            item(key = "day_$dayTimestamp") {
                DayHeader(dayTimestamp = dayTimestamp, eventCount = dayEvents.size)
            }
            // Events for this day
            items(dayEvents, key = { it.reservationId ?: it.startDate ?: 0L }) { event ->
                EventCard(event = event, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun DayHeader(dayTimestamp: Long, eventCount: Int, modifier: Modifier = Modifier) {
    val dateFormat = SimpleDateFormat("EEEE dd MMMM", Locale.getDefault())
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val tomorrow = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }

    val todayLabel = stringResource(R.string.agenda_today)
    val tomorrowLabel = stringResource(R.string.agenda_tomorrow)
    val dayLabel = when (dayTimestamp) {
        today.timeInMillis -> todayLabel
        tomorrow.timeInMillis -> tomorrowLabel
        else -> null
    }
    val dateText = dateFormat.format(Date(dayTimestamp)).replaceFirstChar { it.uppercase() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            if (dayLabel != null) {
                Text(
                    text = dayLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = dateText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = stringResource(R.string.agenda_day_classes_count, eventCount),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

@Composable
private fun EventCard(event: AgendaEventDto, modifier: Modifier = Modifier) {
    val typeColor = eventTypeColor(event.type)
    val displayName = event.name
        ?.replace(Regex("^M\\d+ - t\\d+ - ", RegexOption.IGNORE_CASE), "")
        ?.replaceFirstChar { it.uppercase() }
        ?: event.name ?: stringResource(R.string.agenda_event_untitled)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent bar colored by event type
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(typeColor, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Time block + course name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Time pill
                    Surface(
                        color = typeColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = typeColor
                            )
                            Text(
                                text = formatEventTime(event.startDate, event.endDate),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = typeColor
                            )
                        }
                    }

                    // Type badge
                    if (!event.type.isNullOrBlank()) {
                        Surface(
                            color = typeColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = event.type,
                                style = MaterialTheme.typography.labelSmall,
                                color = typeColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Course name
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Teacher + room row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!event.teacher.isNullOrBlank() && event.teacher.trim().isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Text(text = event.teacher, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    val firstRoom = event.rooms?.firstOrNull()
                    if (firstRoom != null && (!firstRoom.name.isNullOrBlank() || !firstRoom.campus.isNullOrBlank())) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Text(
                                text = firstRoom.name ?: firstRoom.campus ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Modality chip + comment
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (!event.modality.isNullOrBlank()) {
                        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp)) {
                            Text(text = event.modality, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                    if (!event.comment.isNullOrBlank()) {
                        Text(text = stringResource(R.string.agenda_comment_prefix, event.comment), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

private fun eventTypeColor(type: String?): Color = when (type?.lowercase()) {
    "cours" -> Color(0xFF2196F3)
    "examen", "partiel" -> Color(0xFFF44336)
    "tp" -> Color(0xFF4CAF50)
    "td" -> Color(0xFF8BC34A)
    else -> Color(0xFF9E9E9E)
}

private fun formatEventTime(startDate: Long?, endDate: Long?): String {
    if (startDate == null) return ""
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startTime = timeFormat.format(Date(startDate))
    return if (endDate != null) "$startTime – ${timeFormat.format(Date(endDate))}" else startTime
}

@Preview(showBackground = true)
@Composable
private fun AgendaScreenPreview() {
    AppTheme {
        AgendaContent(
            events = listOf(
                AgendaEventDto(
                    reservationId = 1, name = "M1 - t2 - jetpack compose android",
                    teacher = "M. AZIZA", type = "Cours", modality = "Présentiel",
                    startDate = System.currentTimeMillis(), endDate = System.currentTimeMillis() + 7200000,
                    rooms = listOf(RoomDto(roomId = 996, name = "AIX - SALLE 113", floor = "1er étage", campus = "AIX-EN-PROVENCE", color = "#676767", latitude = "43.5135492", longitude = "5.4258471")),
                    discipline = DisciplineDto(name = "M1 - t2 - jetpack compose android", teacher = "M. AZIZA", trimester = "Semestre 2", year = 2025, studentGroupName = "4ESGI ALT - AL - AIX - T2"),
                    promotion = "", comment = null
                ),
                AgendaEventDto(
                    reservationId = 2, name = "Partiel - Soutenance Frameworks JEE",
                    teacher = "M. FAESSEL", type = "Examen", modality = "Présentiel",
                    startDate = System.currentTimeMillis() + 86400000, endDate = System.currentTimeMillis() + 86400000 + 7200000,
                    rooms = listOf(RoomDto(roomId = 667, name = "AIX - SALLE 112", floor = "1er étage", campus = "AIX-EN-PROVENCE", color = "#676767", latitude = "43.5135492", longitude = "5.4258471")),
                    discipline = DisciplineDto(name = "M1 - t2 - frameworks jee", teacher = "M. FAESSEL", trimester = "Semestre 2", year = 2025, studentGroupName = "4ESGI ALT - AL - AIX - T2"),
                    promotion = "", comment = null
                )
            ),
            onRefresh = {}
        )
    }
}
