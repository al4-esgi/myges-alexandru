package com.vlxx.myges.ui.screens.authenticated.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.vlxx.myges.R
import com.vlxx.myges.ui.theme.AppTheme
import com.vlxx.myges.ui.theme.spacing

@Composable
fun HomeScreen() {
    HomeContent()
}

@Composable
fun HomeContent() {
    Box (modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.home_title),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(MaterialTheme.spacing.small)
        )
    }

}

@PreviewScreenSizes
@Composable
private fun HomeContentPreview() {
    AppTheme {
        HomeContent()
    }
}