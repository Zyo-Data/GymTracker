package com.jorge.gymtracker.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jorge.gymtracker.R

@Composable
fun AuthScaffold(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gym_background), // tu fondo
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize().alpha(0.85f)
        )
        Box(
            Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.35f))
        )
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_launcher), // tu logo
                contentDescription = "Logo",
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(16.dp))
            content()
            Spacer(Modifier.height(24.dp))
        }
    }
}
