package com.example.gamehub.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaInicio(
    viewModel: VideojuegoViewModel,
    onIrCatalogo: () -> Unit
) {
    // Escucha el nombre almacenado en DataStore a través del ViewModel
    val nombreGuardado by viewModel.nombreUsuario.collectAsState()

    // Estado local para controlar el campo de texto
    var textoNombre by remember { mutableStateOf("") }

    // Rellena automáticamente el campo si ya existía un nombre previo
    LaunchedEffect(nombreGuardado) {
        if (nombreGuardado.isNotEmpty()) {
            textoNombre = nombreGuardado
        }
    }

    // VALIDACIÓN:
    // 1. Mínimo 3 caracteres (sin contar espacios en bordes).
    // 2. Solo letras y espacios (permite tildes y Ñ/ñ).
    val esNombreValido = textoNombre.trim().length >= 3 &&
            textoNombre.trim().matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎮 Bienvenido a GameHub",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra saludo personalizado si existe registro
        if (nombreGuardado.isNotEmpty()) {
            Text(
                text = "¡Hola de nuevo, $nombreGuardado!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Campo para ingresar o modificar el nombre con validación en vivo
        OutlinedTextField(
            value = textoNombre,
            onValueChange = { textoNombre = it },
            label = { Text("¿Cómo te llamas?") },
            singleLine = true,
            isError = textoNombre.isNotEmpty() && !esNombreValido,
            supportingText = {
                if (textoNombre.isNotEmpty() && !esNombreValido) {
                    Text(
                        text = "Solo letras y mínimo 3 caracteres.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // El botón solo se habilita si esNombreValido es true
        Button(
            onClick = {
                if (esNombreValido) {
                    viewModel.guardarNombreUsuario(textoNombre.trim())
                    onIrCatalogo()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = esNombreValido
        ) {
            Text(
                text = if (nombreGuardado.isEmpty()) "Ingresar y ver catálogo" else "Ir al catálogo"
            )
        }
    }
}