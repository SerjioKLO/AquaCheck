package com.example.aquacheck.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquacheck.R
import com.example.aquacheck.ui.theme.AquaCheckTheme

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Boolean,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8F4))
            .imePadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo de aquacheck
            Image(
                painter = painterResource(id = R.drawable.ic_logo_aquacheck),
                contentDescription = "Logo AquaCheck",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // AQUACHECK bajo el logo
            Text(
                text = "AquaCheck",
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight(50),
                    fontSize = 32.sp,
                    letterSpacing = 6.sp,
                    color = Color(0xFF12260E)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Cajita de "usuario"
            OutlinedTextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    errorMessage = null
                },
                label = { Text("Usuario (supervisor / jefe)", color = Color(0xFF12260E)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Icono usuario",
                        tint = Color(0xFF12260E)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF12260E),
                    unfocusedTextColor = Color(0xFF12260E),
                    focusedBorderColor = Color(0xFF12260E),
                    unfocusedBorderColor = Color(0xFF12260E),
                    focusedLabelColor = Color(0xFF12260E),
                    unfocusedLabelColor = Color(0xFF12260E)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cajita de "contraseña"
            OutlinedTextField(
                value = contrasena,
                onValueChange = {
                    contrasena = it
                    errorMessage = null
                },
                label = { Text("Contraseña", color = Color(0xFF12260E)) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color(0xFF12260E)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF12260E),
                    unfocusedTextColor = Color(0xFF12260E),
                    focusedBorderColor = Color(0xFF12260E),
                    unfocusedBorderColor = Color(0xFF12260E),
                    focusedLabelColor = Color(0xFF12260E),
                    unfocusedLabelColor = Color(0xFF12260E)
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Boton iniciar sesion
            Button(
                onClick = {
                    if (usuario.isBlank() || contrasena.isBlank()) {
                        errorMessage = "Por favor ingrese usuario y contraseña"
                    } else {
                        val success = onLoginClick(usuario, contrasena)
                        if (success) {
                            onLoginSuccess()
                        } else {
                            errorMessage = "Usuario o contraseña incorrectos"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF12260E),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-titulo hasta abajo de la pantalla con alto contraste
            Text(
                text = "Seguridad industrial · Operación sin conexión",
                style = TextStyle(
                    color = Color(0xFF12260E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AquaCheckTheme {
        LoginScreen(
            onLoginClick = { _, _ -> true },
            onLoginSuccess = {}
        )
    }
}
