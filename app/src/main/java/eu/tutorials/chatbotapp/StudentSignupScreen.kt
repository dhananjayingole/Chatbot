package eu.tutorials.chatbotapp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun StudentSignupScreen(navController: NavHostController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person4,
            contentDescription = "Sign up",
            modifier = Modifier.size(64.dp)
        )

        Text("Student Sign Up", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Student ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isLoading = true
                registerStudent(context, auth, db, email, password, name, studentId, navController) {
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate(Routes.StudentLoginScreen) }
        ) {
            Text("Already have an account? Login")
        }
    }
}

fun registerStudent(
    context: Context,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    email: String,
    password: String,
    name: String,
    studentId: String,
    navController: NavHostController,
    onComplete: () -> Unit
) {
    if (email.isEmpty() || password.isEmpty() || name.isEmpty() || studentId.isEmpty()) {
        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        onComplete()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val userId = authTask.result?.user?.uid ?: ""
                val student = hashMapOf(
                    "name" to name,
                    "studentId" to studentId,
                    "email" to email,
                    "userId" to userId
                )

                db.collection("students")
                    .document(userId)
                    .set(student)
                    .addOnSuccessListener {
                        onComplete()
                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.ChatScreen) {
                            popUpTo(Routes.StudentSignupScreen) { inclusive = true }
                        }
                    }
                    .addOnFailureListener { e ->
                        onComplete()
                        navController.navigate(Routes.StudentLoginScreen)
                    }
            } else {
                onComplete()
                authTask.exception?.message?.let { errorMessage ->
                    Toast.makeText(context, "Authentication failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
        }
}