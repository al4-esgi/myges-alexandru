package com.vlxx.myges.ui.screens.authenticated.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vlxx.myges.R
import com.vlxx.myges.data.dtos.EmergencyContactDto
import com.vlxx.myges.data.dtos.LinkDto
import com.vlxx.myges.data.dtos.ProfileLinksDto
import com.vlxx.myges.data.dtos.ProfileResponseDto
import com.vlxx.myges.ui.screens.authenticated.profile.viewModel.ProfileUiState
import com.vlxx.myges.ui.screens.authenticated.profile.viewModel.ProfileViewModel
import com.vlxx.myges.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ProfileUiState.Loading -> LoadingContent()
        is ProfileUiState.Success -> ProfileContent(profile = state.profile, onLogout = viewModel::logout)
        is ProfileUiState.Error -> ErrorContent(message = state.message, onRetry = viewModel::loadProfile)
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.profile_loading),
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
            Text(text = stringResource(R.string.profile_error), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onRetry) { Text(stringResource(R.string.profile_retry)) }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: ProfileResponseDto,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {

        // ── Top header banner ─────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar circle with photo or initials fallback
                    val photoUrl = profile.links?.photo?.href
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photoUrl)
                                    .build(),
                                contentDescription = "Photo de profil",
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "${profile.firstname?.firstOrNull() ?: ""}${profile.name?.firstOrNull() ?: ""}",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Text(
                        text = "${profile.firstname.orEmpty()} ${profile.name.orEmpty()}".trim(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    if (!profile.email.isNullOrBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = profile.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                            )
                        }
                    }
                    if (!profile.studentId.isNullOrBlank()) {
                        Text(
                            text = stringResource(R.string.profile_student_number, profile.studentId!!),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        // ── Personal information ──────────────────────────────────────
        item {
            InfoSection(
                title = stringResource(R.string.profile_section_personal_info),
                icon = Icons.Default.Person,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                InfoRow(icon = Icons.Default.Badge, label = stringResource(R.string.profile_ine), value = profile.ine)
                InfoRow(icon = Icons.Default.Wc, label = stringResource(R.string.profile_civility), value = profile.civility)
                InfoRow(icon = Icons.Default.Cake, label = stringResource(R.string.profile_birthday), value = profile.birthday?.let { formatDate(it) })
                InfoRow(icon = Icons.Default.Place, label = stringResource(R.string.profile_birthplace), value = profile.birthplace)
                InfoRow(icon = Icons.Default.Public, label = stringResource(R.string.profile_birth_country), value = profile.birthCountry)
                InfoRow(icon = Icons.Default.Flag, label = stringResource(R.string.profile_nationality), value = profile.nationality)
            }
        }

        // ── Contact ───────────────────────────────────────────────────
        item {
            InfoSection(
                title = stringResource(R.string.profile_section_contact),
                icon = Icons.Default.ContactPhone,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                InfoRow(icon = Icons.Default.Email, label = stringResource(R.string.profile_email), value = profile.email)
                InfoRow(icon = Icons.Default.AlternateEmail, label = stringResource(R.string.profile_personal_email), value = profile.personalMail)
                InfoRow(icon = Icons.Default.PhoneAndroid, label = stringResource(R.string.profile_mobile), value = profile.mobile)
                InfoRow(icon = Icons.Default.Phone, label = stringResource(R.string.profile_telephone), value = profile.telephone)
            }
        }

        // ── Address ───────────────────────────────────────────────────
        item {
            InfoSection(
                title = stringResource(R.string.profile_section_address),
                icon = Icons.Default.Home,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val fullAddress = buildString {
                    profile.address1?.let { append(it) }
                    if (!profile.address2.isNullOrBlank()) append("\n${profile.address2}")
                }
                InfoRow(icon = Icons.Default.LocationOn, label = stringResource(R.string.profile_address), value = fullAddress.takeIf { it.isNotBlank() })
                InfoRow(icon = Icons.Default.LocationCity, label = stringResource(R.string.profile_city), value = profile.city)
                InfoRow(icon = Icons.Default.MarkunreadMailbox, label = stringResource(R.string.profile_zipcode), value = profile.zipcode)
                InfoRow(icon = Icons.Default.Public, label = stringResource(R.string.profile_country), value = profile.country)
            }
        }

        // ── Emergency contact ─────────────────────────────────────────
        item {
            val emergency = profile.emergencyContact
            InfoSection(
                title = stringResource(R.string.profile_section_emergency),
                icon = Icons.Default.LocalHospital,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (emergency != null && (!emergency.firstname.isNullOrBlank() || !emergency.name.isNullOrBlank())) {
                    InfoRow(
                        icon = Icons.Default.Person,
                        label = stringResource(R.string.profile_emergency_name),
                        value = "${emergency.firstname.orEmpty()} ${emergency.name.orEmpty()}".trim()
                    )
                    InfoRow(icon = Icons.Default.FamilyRestroom, label = stringResource(R.string.profile_emergency_type), value = emergency.type)
                    InfoRow(icon = Icons.Default.PhoneAndroid, label = stringResource(R.string.profile_emergency_mobile), value = emergency.mobile)
                    InfoRow(icon = Icons.Default.Phone, label = stringResource(R.string.profile_emergency_telephone), value = emergency.telephone)
                    InfoRow(icon = Icons.Default.Work, label = stringResource(R.string.profile_emergency_work_phone), value = emergency.workPhone)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.profile_no_emergency_contact),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ── Logout button ─────────────────────────────────────────────
        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.profile_logout),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Section header
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(6.dp).size(18.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp).padding(top = 2.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(date)
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    AppTheme {
        ProfileContent(
            profile = ProfileResponseDto(
                uid = 637027,
                studentId = "2025-ESGI-Aix-en-Provence-637027",
                ine = "143199959HE",
                civility = "M",
                firstname = "Daniel Alexandru",
                name = "RUSESCU",
                maidenName = null,
                birthday = 1056837600000L,
                birthplace = "Bucarest",
                birthCountry = "Roumanie",
                address1 = "35.Bis Avenue Du Général Raoul Salan",
                address2 = "Salan",
                city = "Marignane",
                zipcode = "13700",
                country = "France",
                telephone = null,
                mobile = "0645565270",
                email = "a.rusescu@myskolae.fr",
                nationality = "roumaine",
                personalMail = "alexmonac13@gmail.com",
                emergencyContact = EmergencyContactDto(
                    emergencyId = 637027,
                    type = null,
                    typeDetails = null,
                    firstname = null,
                    name = null,
                    telephone = null,
                    mobile = null,
                    workPhone = null
                ),
                links = ProfileLinksDto(
                    photo = LinkDto(href = "https://ges-dl.kordis.fr/public/dEkj-aOcIw52B9RsgY-op2htNPxbE4A4CZSMwXEBz3M")
                )
            ),
            onLogout = {}
        )
    }
}
