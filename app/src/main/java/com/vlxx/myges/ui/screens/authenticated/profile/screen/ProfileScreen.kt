package com.vlxx.myges.ui.screens.authenticated.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
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
        is ProfileUiState.Loading -> {
            LoadingContent()
        }
        is ProfileUiState.Success -> {
            ProfileContent(profile = state.profile)
        }
        is ProfileUiState.Error -> {
            ErrorContent(
                message = state.message,
                onRetry = viewModel::loadProfile
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
                text = stringResource(R.string.profile_loading),
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
                text = stringResource(R.string.profile_error),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.profile_retry))
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: ProfileResponseDto,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Profile Header with Photo
        ProfileHeader(profile = profile)

        // Personal Information Section
        ProfileSection(title = stringResource(R.string.profile_section_personal_info)) {
            ProfileInfoItem(
                label = stringResource(R.string.profile_student_id),
                value = profile.studentId
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_ine),
                value = profile.ine
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_civility),
                value = profile.civility
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_firstname),
                value = profile.firstname
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_lastname),
                value = profile.name
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_birthday),
                value = profile.birthday?.let { formatDate(it) }
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_birthplace),
                value = profile.birthplace
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_birth_country),
                value = profile.birthCountry
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_nationality),
                value = profile.nationality
            )
        }

        // Contact Section
        ProfileSection(title = stringResource(R.string.profile_section_contact)) {
            ProfileInfoItem(
                label = stringResource(R.string.profile_email),
                value = profile.email
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_personal_email),
                value = profile.personalMail
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_mobile),
                value = profile.mobile
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_telephone),
                value = profile.telephone
            )
        }

        // Address Section
        ProfileSection(title = stringResource(R.string.profile_section_address)) {
            ProfileInfoItem(
                label = stringResource(R.string.profile_address),
                value = buildString {
                    profile.address1?.let { append(it) }
                    if (!profile.address2.isNullOrBlank()) {
                        append("\n${profile.address2}")
                    }
                }
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_city),
                value = profile.city
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_zipcode),
                value = profile.zipcode
            )
            ProfileInfoItem(
                label = stringResource(R.string.profile_country),
                value = profile.country
            )
        }

        // Emergency Contact Section
        ProfileSection(title = stringResource(R.string.profile_section_emergency)) {
            val emergency = profile.emergencyContact
            if (emergency != null && (!emergency.firstname.isNullOrBlank() || !emergency.name.isNullOrBlank())) {
                ProfileInfoItem(
                    label = stringResource(R.string.profile_emergency_name),
                    value = "${emergency.firstname.orEmpty()} ${emergency.name.orEmpty()}".trim()
                )
                ProfileInfoItem(
                    label = stringResource(R.string.profile_emergency_type),
                    value = emergency.type
                )
                ProfileInfoItem(
                    label = stringResource(R.string.profile_emergency_mobile),
                    value = emergency.mobile
                )
                ProfileInfoItem(
                    label = stringResource(R.string.profile_emergency_telephone),
                    value = emergency.telephone
                )
                ProfileInfoItem(
                    label = stringResource(R.string.profile_emergency_work_phone),
                    value = emergency.workPhone
                )
            } else {
                Text(
                    text = stringResource(R.string.profile_no_emergency_contact),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileHeader(profile: ProfileResponseDto) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Photo
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${profile.firstname?.firstOrNull() ?: ""}${profile.name?.firstOrNull() ?: ""}",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Name
        Text(
            text = "${profile.firstname.orEmpty()} ${profile.name.orEmpty()}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Email
        Text(
            text = profile.email.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    if (!value.isNullOrBlank()) {
        Column(modifier = modifier) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
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
            )
        )
    }
}
