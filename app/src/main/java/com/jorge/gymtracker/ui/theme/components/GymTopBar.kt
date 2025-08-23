package com.jorge.gymtracker.ui.theme.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymTopBar(title: String, canNavigateBack: Boolean, onBack: () -> Unit = {}) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        }
    )
}
