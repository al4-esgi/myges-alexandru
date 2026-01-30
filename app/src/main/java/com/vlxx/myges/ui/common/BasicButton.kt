package com.vlxx.myges.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun BasicButton(title: Int, isLoading: Boolean, onClick: () -> Unit) {
    Button(onClick, enabled = !isLoading) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
        } else {
            Text(stringResource(title))
        }
    }
}
