package ec.edu.monster.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.monster.ui.viewmodels.MovementsViewModel

@Composable
fun NuevoMovimientoModal(
    cuenta: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: MovementsViewModel = viewModel()
) {
    val tipos by viewModel.tipos.collectAsState()
    val loading by viewModel.operationLoading.collectAsState()
    val error by viewModel.operationError.collectAsState()
    var tipo by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var importe by remember { mutableStateOf("") }
    var tiposExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadTiposMovimiento() }
    LaunchedEffect(tipos) { if (tipo.isBlank()) tipo = tipos.firstOrNull().orEmpty() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar movimiento") },
        text = {
            Column {
                OutlinedTextField(
                    value = tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo") },
                    modifier = Modifier.fillMaxWidth().clickable { tiposExpanded = true }
                )
                DropdownMenu(expanded = tiposExpanded, onDismissRequest = { tiposExpanded = false }) {
                    tipos.forEach { opcion ->
                        DropdownMenuItem(text = { Text(opcion) }, onClick = {
                            tipo = opcion
                            tiposExpanded = false
                        })
                    }
                }
                if (tipo.equals("TRANSFERENCIA", ignoreCase = true)) {
                    OutlinedTextField(destino, { destino = it }, label = { Text("Cuenta destino") }, modifier = Modifier.fillMaxWidth())
                }
                OutlinedTextField(
                    value = importe,
                    onValueChange = { importe = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Importe") },
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let { Text(it) }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !loading && tipo.isNotBlank() && (importe.toDoubleOrNull() ?: 0.0) > 0.0,
                onClick = {
                    viewModel.registrarMovimiento(
                        cuenta,
                        destino.takeIf { tipo.equals("TRANSFERENCIA", ignoreCase = true) },
                        tipo,
                        importe.toDouble(),
                        onSuccess = { onSuccess(); onDismiss() }
                    )
                }
            ) { Text(if (loading) "Procesando…" else "Registrar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
