package com.vlxx.myges.ui.screens.unauthenticated.signInScreen.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vlxx.myges.R
import com.vlxx.myges.ui.common.BasicButton
import com.vlxx.myges.ui.screens.unauthenticated.signInScreen.viewModel.SignInViewModel
import com.vlxx.myges.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    SignInContent(
        isLoading = isLoading,
        onSignInClick = viewModel::connection
    )
}

@Composable
fun SignInContent(
    isLoading: Boolean,
    onSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BasicButton(
            title = R.string.signin_connection,
            isLoading = isLoading,
            onClick = onSignInClick
        )
    }
}

@PreviewScreenSizes
@Composable
private fun SignInContentPreview() {
    AppTheme {
        SignInContent(
            isLoading = false,
            onSignInClick = {}
        )
    }
}