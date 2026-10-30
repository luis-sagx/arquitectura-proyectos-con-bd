// ui/screens/MovementsScreen.kt
package ec.edu.monster.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ec.edu.monster.ui.theme.EurekaBankSOAPJava
import ec.edu.monster.ui.viewmodels.MovementsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.monster.R
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import ec.edu.monster.ui.components.NuevoMovimientoModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementsScreen(
    cuenta: String,
    onBack: () -> Unit,
    viewModel: MovementsViewModel = viewModel()
) {
    val movimientos by viewModel.movimientos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showModal by remember { mutableStateOf(false) }
    LaunchedEffect(cuenta) {
        viewModel.loadMovimientos(cuenta)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // ÍCONO: sulli_general.png
                    Image(
                        painter = painterResource(id = R.drawable.sulli_general),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 8.dp)
                    )
                    Text("Movimientos - $cuenta", fontWeight = FontWeight.Bold)
                } },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = EurekaBankSOAPJava.primary,
                    titleContentColor = EurekaBankSOAPJava.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showModal = true },
                containerColor = EurekaBankSOAPJava.primary
            ) {
                Icon(Icons.Filled.Add, "Nuevo")
            }
        }
    ) { padding ->
        if (showModal) {
            NuevoMovimientoModal(
                cuenta = cuenta,
                onDismiss = { showModal = false },
                onSuccess = {
                    // Recargar movimientos
                    viewModel.loadMovimientos(cuenta)
                }
            )
        }

        Box(modifier = Modifier.padding(padding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
                movimientos.isEmpty() -> {
                    Text("No hay movimientos", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn {
                        // CORREGIDO: items(movimientos) no items(size)
                        items(movimientos) { mov ->
                            MovementItem(mov)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovementItem(mov: ec.edu.monster.models.MovimientoData) {
    ListItem(
        headlineContent = {
            Column {
                // DETECTAR ENTRADA / SALIDA POR EL TIPO
                val esEntrada = mov.tipo.contains("Entrada", ignoreCase = true)
                val esSalida = mov.tipo.contains("Salida", ignoreCase = true)

                val prefijo = if (esEntrada) "+" else if (esSalida) "-" else ""
                val colorImporte = if (esEntrada) {
                    EurekaBankSOAPJava.success // VERDE
                } else if (esSalida) {
                    EurekaBankSOAPJava.error   // ROJO
                } else {
                    MaterialTheme.colorScheme.onSurface // gris por defecto
                }

                Text(
                    text = "${mov.tipo} $prefijo${mov.importe}",
                    fontWeight = FontWeight.Bold,
                    color = colorImporte
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(mov.fecha ?: Date()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        supportingContent = {
            Column {
                if (!mov.referencia.isNullOrBlank()) {
                    Text("Ref: ${mov.referencia}")
                }
                Text("Saldo: S/ ${mov.saldoActual}")
            }
        }
    )
}