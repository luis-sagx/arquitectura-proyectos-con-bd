// app/src/main/java/ec/edu/monster/ui/screens/AccountsScreen.kt

package ec.edu.monster.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.monster.ui.theme.EurekaBankSOAPJava
import ec.edu.monster.ui.viewmodels.AccountsViewModel
import ec.edu.monster.R // ← OBLIGATORIO
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onAccountClick: (String) -> Unit,
    viewModel: AccountsViewModel = viewModel()
) {
    val cuentas = viewModel.cuentas.collectAsState().value
    val loading = viewModel.loading.collectAsState().value
    val error = viewModel.error.collectAsState().value

    // RECARGAR AL INICIAR
    LaunchedEffect(Unit) {
        viewModel.loadCuentas()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sulli_general),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text("Cuentas", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = EurekaBankSOAPJava.primary,
                    titleContentColor = EurekaBankSOAPJava.onPrimary
                ),
                actions = {
                    // BOTÓN DE RECARGAR
                    IconButton(onClick = { viewModel.loadCuentas() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar",
                            tint = EurekaBankSOAPJava.onPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadCuentas() }) {
                            Text("Reintentar")
                        }
                    }
                }
                cuentas.isEmpty() -> {
                    Text("No hay cuentas disponibles", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn {
                        items(cuentas) { cuenta ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(EurekaBankSOAPJava.spacingMedium)
                                    .clickable { onAccountClick(cuenta.codigo) },
                                colors = CardDefaults.cardColors(containerColor = EurekaBankSOAPJava.surface)
                            ) {
                                ListItem(
                                    headlineContent = { Text(cuenta.codigo, fontWeight = FontWeight.Bold) },
                                    supportingContent = {
                                        Column {
                                            Text("${cuenta.nombreCliente} • ${cuenta.moneda}")
                                            Text("Saldo: S/ ${cuenta.saldo}", color = EurekaBankSOAPJava.primary, fontWeight = FontWeight.Medium)
                                            Text("${cuenta.estado} • ${cuenta.emailCliente}")
                                        }
                                    },
                                    trailingContent = {
                                        Icon(Icons.Filled.KeyboardArrowRight, "Ver detalles")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}