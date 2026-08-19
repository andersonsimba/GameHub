package com.example.gamehub.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * PANTALLA DE AJUSTES Y CONFIGURACIÓN
 * Permite cambiar las preferencias del usuario como el Modo Oscuro y
 * actualizar el Nombre de Jugador con validación en tiempo real.
 */
@Composable
fun PantallaAjustes(viewModel: VideojuegoViewModel) {
    // ESTADOS OBSERVABLES: Flujos de DataStore expuestos en el ViewModel como StateFlow
    val esModoOscuro by viewModel.esModoOscuro.collectAsState()
    val nombreGuardado by viewModel.nombreUsuario.collectAsState()

    // ESTADOS LOCALES REACTIVOS: Sincronizados con el nombre guardado en DataStore
    var textoNombre by remember(nombreGuardado) { mutableStateOf(nombreGuardado) }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }

    // REGEX DE VALIDACIÓN: Permite letras de la A-Z, tildes y Ñ (no números ni caracteres especiales)
    val regexNombre = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Configuración y Ajustes",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TARJETA 1: MODO OSCURO (Persistencia mediante DataStore)
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Modo Oscuro",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Persistir preferencia mediante DataStore",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // EVENTO DE CAMBIO: Llama a corrutina en el ViewModel para persistir preferencia
                Switch(
                    checked = esModoOscuro,
                    onCheckedChange = { activado ->
                        viewModel.cambiarModoOscuro(activado)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TARJETA 2: NOMBRE DE JUGADOR (Con validación de formato)
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Nombre de Jugador",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Modifica tu nombre de usuario registrado en la app",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CAMPO DE TEXTO CON INDICACIÓN VISUAL DE ERROR
                OutlinedTextField(
                    value = textoNombre,
                    onValueChange = {
                        textoNombre = it
                        mensajeError = "" // Limpia el error al escribir
                        mensajeExito = ""
                    },
                    label = { Text("Nombre de usuario") },
                    isError = mensajeError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // FEEDBACK DE ERROR
                if (mensajeError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mensajeError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // FEEDBACK DE ÉXITO
                if (mensajeExito.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mensajeExito,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ACCIÓN DE GUARDAR: Valida antes de persistir en DataStore
                Button(
                    onClick = {
                        val nombreLimpio = textoNombre.trim()

                        // LÓGICA DE VALIDACIÓN ESTRICTA
                        when {
                            nombreLimpio.length < 3 -> {
                                mensajeError = "El nombre debe tener al menos 3 caracteres."
                            }
                            !regexNombre.matches(nombreLimpio) -> {
                                mensajeError = "Solo se permiten letras, tildes y la letra Ñ."
                            }
                            else -> {
                                // Guarda el nombre validado en DataStore
                                viewModel.guardarNombreUsuario(nombreLimpio)
                                mensajeError = ""
                                mensajeExito = "¡Nombre actualizado con éxito!"
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}