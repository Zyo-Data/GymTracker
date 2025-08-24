package com.jorge.gymtracker.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.jorge.gymtracker.R
import com.jorge.gymtracker.ui.theme.history.HistoryDetailScreen
import com.jorge.gymtracker.ui.theme.history.HistoryScreen
import com.jorge.gymtracker.ui.theme.navigation.BottomNavBar
import com.jorge.gymtracker.ui.theme.navigation.Routes
import com.jorge.gymtracker.ui.theme.workout.WorkoutScreen
import androidx.compose.ui.draw.paint
// Para evitar conflicto de nombres con tus Routes internos:
import com.jorge.gymtracker.auth.nav.Routes as AuthRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(rootNav: NavHostController) {
    val sectionNav = rememberNavController()   // Nav interno de las pestañas inferiores
    val auth = remember { FirebaseAuth.getInstance() }
    val userEmail = auth.currentUser?.email ?: "Usuario"
    var menuOpen by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Inicio") },
                actions = {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Usuario"
                        )
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(userEmail) },
                            onClick = { /* solo informativo */ }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                menuOpen = false
                                auth.signOut()
                                // Navega al Login en el grafo raíz y limpia back stack
                                rootNav.navigate(AuthRoutes.LOGIN) {
                                    popUpTo(AuthRoutes.HOME) { inclusive = true }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Logout,
                                    contentDescription = "Logout"
                                )
                            }
                        )
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(sectionNav) }  // SIEMPRE visible
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painter = painterResource(id = R.drawable.gym_background),
                    contentScale = ContentScale.Crop
                )
                .padding(innerPadding)
        ) {
            NavHost(
                navController = sectionNav,
                startDestination = Routes.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                /* ---------- INICIO ---------- */
                composable(Routes.Home.route) {
                    HomeContent(
                        onStartWorkout = { sectionNav.navigate(Routes.Workout.route) },
                        onOpenHistory  = { sectionNav.navigate("history") }
                    )
                }

                /* ---------- ENTRENAMIENTO ---------- */
                composable(Routes.Workout.route) { WorkoutScreen() }

                /* ---------- RUTINAS ---------- */
                composable(Routes.Routines.route) {
                    RoutinesContent(
                        onCreateRoutine = { sectionNav.navigate(Routes.CreateRoutine.route) }
                    )
                }

                /* ---------- AJUSTES ---------- */
                composable(Routes.Settings.route) { SettingsContent() }

                /* ---------- HISTORIAL (lista) ---------- */
                composable("history") {
                    HistoryScreen(navController = sectionNav)
                }

                /* ---------- HISTORIAL (detalle) ---------- */
                composable("sessionDetail/{sessionId}") { backStack ->
                    val id = backStack.arguments?.getString("sessionId")?.toLongOrNull()
                    if (id != null) {
                        HistoryDetailScreen(
                            sessionId = id,
                            onBack = { sectionNav.navigateUp() }
                        )
                    }
                }

                /* ---------- CREAR RUTINA ---------- */
                composable(Routes.CreateRoutine.route) { CreateRoutineScreen() }
            }
        }
    }
}

/* ---------- Pantalla de portada ---------- */
@Composable
private fun HomeContent(
    onStartWorkout: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Inicio", style = MaterialTheme.typography.headlineSmall)
        Text("Bienvenido al Gym Tracker")
        Button(onClick = onStartWorkout) { Text("Empezar entrenamiento") }
        OutlinedButton(onClick = onOpenHistory) { Text("Ver historial") }
    }
}

/* ---------- Resto de pantallas “placeholder” ---------- */
@Composable
private fun RoutinesContent(onCreateRoutine: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Rutinas", style = MaterialTheme.typography.headlineSmall)
        Text("Aquí listarás tus rutinas guardadas.")
        Button(onClick = onCreateRoutine) { Text("Nueva rutina") }
    }
}

@Composable
private fun SettingsContent() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ajustes", style = MaterialTheme.typography.headlineSmall)
        Text("Tema, unidades, recordatorios…")
    }
}

@Composable
private fun CreateRoutineScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nueva rutina", style = MaterialTheme.typography.headlineSmall)
        Text("Formulario para crear rutina (placeholder).")
    }
}
