package com.example.techstoreapp

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techstoreapp.data.local.SessionManager
import com.example.techstoreapp.data.model.LoginRequest
import com.example.techstoreapp.data.model.RegisterRequest
import com.example.techstoreapp.data.remote.RetrofitClient
import com.example.techstoreapp.ui.theme.TechStoreAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TechStoreAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val sessionManager = remember {
                        SessionManager(this@MainActivity)
                    }

                    AuthScreen(sessionManager = sessionManager)
                }
            }
        }
    }
}

@Composable
fun AuthScreen(
    sessionManager: SessionManager
) {
    var showRegister by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(sessionManager.isLoggedIn()) }

    if (isLoggedIn) {
        CatalogScreen(
            sessionManager = sessionManager,
            onLogout = {
                sessionManager.clearSession()
                isLoggedIn = false
                showRegister = false
            }
        )
    } else {
        if (showRegister) {
            RegisterScreen(
                onGoToLogin = {
                    showRegister = false
                }
            )
        } else {
            LoginScreen(
                sessionManager = sessionManager,
                onLoginSuccess = {
                    isLoggedIn = true
                },
                onGoToRegister = {
                    showRegister = true
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    sessionManager: SessionManager,
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val purple = Color(0xFF4F16E8)
    val darkPurple = Color(0xFF16003A)
    val textDark = Color(0xFF111827)
    val textGray = Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                darkPurple,
                                Color(0xFF24105F)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(82.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF6D28D9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛍",
                            fontSize = 42.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Tech")
                            }
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF8B5CF6),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Store")
                            }
                        },
                        fontSize = 32.sp
                    )

                    Text(
                        text = "Tu tienda de tecnología",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .align(Alignment.TopCenter)
                .padding(top = 265.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingresa tus credenciales para continuar",
                    fontSize = 14.sp,
                    color = textGray
                )

                Spacer(modifier = Modifier.height(26.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Correo electrónico") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = purple
                            )
                        )

                        Text(
                            text = "Recordarme",
                            fontSize = 13.sp,
                            color = textDark
                        )
                    }

                    TextButton(
                        onClick = {
                            message = "Función de recuperación pendiente."
                        }
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = purple,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val validationMessage = validateLogin(email, password)

                        if (validationMessage != null) {
                            message = validationMessage
                            return@Button
                        }

                        coroutineScope.launch {
                            isLoading = true
                            message = "Conectando con el servidor..."

                            try {
                                val response = RetrofitClient.authApi.login(
                                    LoginRequest(
                                        email = email.trim(),
                                        password = password
                                    )
                                )

                                if (response.isSuccessful) {
                                    val authResponse = response.body()

                                    if (authResponse?.token != null) {
                                        sessionManager.saveSession(
                                            token = authResponse.token,
                                            email = authResponse.email ?: email.trim(),
                                            role = authResponse.role ?: "CUSTOMER"
                                        )

                                        onLoginSuccess()
                                    } else {
                                        message = "Login correcto, pero no se recibió token."
                                    }
                                } else {
                                    message = "Credenciales inválidas o error ${response.code()}."
                                }
                            } catch (e: Exception) {
                                message = "No se pudo conectar con la API: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = purple
                    )
                ) {
                    Text(
                        text = if (isLoading) "Validando..." else "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (message.contains("correcto") || message.contains("Token")) {
                            Color(0xFF16A34A)
                        } else {
                            Color(0xFFDC2626)
                        },
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No tienes cuenta?",
                        color = textDark,
                        fontSize = 13.sp
                    )

                    TextButton(
                        onClick = onGoToRegister
                    ) {
                        Text(
                            text = "Regístrate",
                            color = purple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onGoToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val purple = Color(0xFF4F16E8)
    val darkPurple = Color(0xFF16003A)
    val textDark = Color(0xFF111827)
    val textGray = Color(0xFF6B7280)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(245.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                darkPurple,
                                Color(0xFF24105F)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color(0xFF6D28D9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛍",
                            fontSize = 38.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Crear cuenta",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Regístrate para comprar de forma segura",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .align(Alignment.TopCenter)
                .padding(top = 215.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registro",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Completa tus datos para crear una cuenta",
                    fontSize = 14.sp,
                    color = textGray
                )

                Spacer(modifier = Modifier.height(22.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nombre completo") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Correo electrónico") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Confirmar contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purple,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val validationMessage = validateRegister(
                            fullName = fullName,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword
                        )

                        if (validationMessage != null) {
                            message = validationMessage
                            return@Button
                        }

                        coroutineScope.launch {
                            isLoading = true
                            message = "Registrando usuario..."

                            try {
                                val response = RetrofitClient.authApi.register(
                                    RegisterRequest(
                                        fullName = fullName.trim(),
                                        email = email.trim(),
                                        password = password
                                    )
                                )

                                if (response.isSuccessful) {
                                    message = "Usuario registrado correctamente. Ahora puedes iniciar sesión."
                                } else {
                                    message = "No se pudo registrar. Error ${response.code()}."
                                }
                            } catch (e: Exception) {
                                message = "No se pudo conectar con la API: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = purple
                    )
                ) {
                    Text(
                        text = if (isLoading) "Registrando..." else "Registrarme",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (message.contains("correctamente")) {
                            Color(0xFF16A34A)
                        } else {
                            Color(0xFFDC2626)
                        },
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tienes cuenta?",
                        color = textDark,
                        fontSize = 13.sp
                    )

                    TextButton(
                        onClick = onGoToLogin
                    ) {
                        Text(
                            text = "Inicia sesión",
                            color = purple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun validateLogin(
    email: String,
    password: String
): String? {
    if (email.isBlank()) {
        return "Debes ingresar tu correo electrónico."
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return "Ingresa un correo electrónico válido."
    }

    if (password.isBlank()) {
        return "Debes ingresar tu contraseña."
    }

    if (password.length < 6) {
        return "La contraseña debe tener al menos 6 caracteres."
    }

    return null
}

fun validateRegister(
    fullName: String,
    email: String,
    password: String,
    confirmPassword: String
): String? {
    if (fullName.isBlank()) {
        return "Debes ingresar tu nombre completo."
    }

    if (email.isBlank()) {
        return "Debes ingresar tu correo electrónico."
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return "Ingresa un correo electrónico válido."
    }

    if (password.isBlank()) {
        return "Debes ingresar una contraseña."
    }

    if (password.length < 6) {
        return "La contraseña debe tener al menos 6 caracteres."
    }

    if (password != confirmPassword) {
        return "Las contraseñas no coinciden."
    }

    return null
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TechStoreAppTheme {
        Text("Preview no disponible con SessionManager")
    }
}