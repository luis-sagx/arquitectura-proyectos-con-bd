
package ec.edu.monster.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ec.edu.monster.ui.viewmodels.LoginViewModel
import ec.edu.monster.ui.theme.EurekaBankSOAPJava
import ec.edu.monster.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onLoginError: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensajeLocal by remember { mutableStateOf<String?>(null) }
    val loginState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = EurekaBankSOAPJava.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(EurekaBankSOAPJava.spacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_background),
                contentDescription = "Monstruo de EuBank en una oficina bancaria",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Text(
                    text = "JAVA SOAP",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = EurekaBankSOAPJava.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = "Iniciar sesión en EuBank",
                    style = MaterialTheme.typography.titleMedium,
                    color = EurekaBankSOAPJava.secondary
                )
            }

            // Campo Usuario
            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario") },
                enabled = !loginState.loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EurekaBankSOAPJava.primary,
                    unfocusedBorderColor = EurekaBankSOAPJava.border
                )
            )

            Spacer(modifier = Modifier.height(EurekaBankSOAPJava.spacingMedium))

            // Campo Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                enabled = !loginState.loading,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EurekaBankSOAPJava.primary,
                    unfocusedBorderColor = EurekaBankSOAPJava.border
                )
            )

            Spacer(modifier = Modifier.height(EurekaBankSOAPJava.spacingLarge))

            // Botón Login
            Button(
                onClick = {
                    if (usuario.isBlank() || contrasena.isBlank()) {
                        mensajeLocal = "Complete todos los campos"
                        onLoginError("Complete todos los campos")
                        return@Button
                    }
                    mensajeLocal = null
                    viewModel.iniciarSesion(usuario, contrasena) { success, msg ->
                        if (success) {
                            onLoginSuccess()
                        } else {
                            mensajeLocal = msg
                            onLoginError(msg ?: "Error desconocido")
                        }
                    }
                },
                enabled = !loginState.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EurekaBankSOAPJava.primary,
                    disabledContainerColor = EurekaBankSOAPJava.border
                )
            ) {
                if (loginState.loading) {
                    CircularProgressIndicator(
                        color = EurekaBankSOAPJava.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Iniciar Sesión", color = EurekaBankSOAPJava.onPrimary)
                }
            }

            // Mensaje de error o éxito
            (loginState.message ?: mensajeLocal)?.let {
                Spacer(modifier = Modifier.height(EurekaBankSOAPJava.spacingMedium))
                Text(
                    text = it,
                    color = if (it.contains("exitoso")) EurekaBankSOAPJava.success else EurekaBankSOAPJava.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(
        onLoginSuccess = {},
        onLoginError = {}
    )
}
