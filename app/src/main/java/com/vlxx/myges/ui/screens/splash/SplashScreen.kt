package com.vlxx.myges.ui.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.vlxx.myges.R
import com.vlxx.myges.ui.screens.splash.viewModel.SplashViewModel
import com.vlxx.myges.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetch()
    }

    SplashContent()
}

@Composable
fun SplashContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()

        Text(
            text = stringResource(R.string.splash_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@PreviewScreenSizes
@Composable
private fun SplashContentPreview() {
    AppTheme {
        SplashContent()
    }
}