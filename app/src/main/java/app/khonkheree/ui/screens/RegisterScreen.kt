package app.khonkheree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.khonkheree.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    vm: AuthViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibleA by remember { mutableStateOf(false) }
    var passwordVisibleB by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onRegisterSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Бүртгүүлэх",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Шинэ бүртгэл үүсгэхийн тулд дор өгсөн өгөгдлийг нөхнө үү",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))

        // Name field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Нэр *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null && name.isBlank(),
        )
        Spacer(Modifier.height(12.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Имэйл *") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null && email.isBlank(),
        )
        Spacer(Modifier.height(12.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Нууц үг *") },
            singleLine = true,
            visualTransformation = if (passwordVisibleA) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisibleA = !passwordVisibleA }) {
                    Icon(
                        if (passwordVisibleA) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        "Нууц үг харуулах"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null && password.isBlank(),
        )
        Spacer(Modifier.height(12.dp))

        // Confirm password field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Нууц үг давтах *") },
            singleLine = true,
            visualTransformation = if (passwordVisibleB) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisibleB = !passwordVisibleB }) {
                    Icon(
                        if (passwordVisibleB) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        "Нууц үг харуулах"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = password.isNotEmpty() && password != confirmPassword,
        )
        if (password.isNotEmpty() && password != confirmPassword) {
            Text(
                "Нууц үг тэлэхгүй байна",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start).padding(start = 16.dp)
            )
        }
        Spacer(Modifier.height(24.dp))

        // Error message
        state.error?.let {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // Register button
        Button(
            onClick = {
                vm.register(name, email, password)
            },
            enabled = !state.isLoading
                && name.isNotBlank()
                && email.isNotBlank()
                && password.isNotBlank()
                && password == confirmPassword,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Бүртгүүлэх")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Back to login
        TextButton(onClick = onBackToLogin) {
            Text("Бүртгэлтэй бол нэвтрэх")
        }
    }
}

// Icons for visibility toggle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

// Note: Make sure to import Icons properly in actual usage
