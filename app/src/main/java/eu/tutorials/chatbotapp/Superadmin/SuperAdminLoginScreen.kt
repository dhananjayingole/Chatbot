//package eu.tutorials.chatbotapp.Superadmin
//
//import android.content.Context
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import eu.tutorials.chatbotapp.Routes
//
//@Composable
//fun SuperAdminLoginScreen(navController: NavHostController) {
//    val context = LocalContext.current
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var isLoading by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//
//    // Hardcoded demo credentials
//    val demoCredentials = remember {
//        mapOf(
//            "email" to "2023BCS@sggs.ac.in",
//            "password" to "Dhanu#123"
//        )
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            "SuperAdmin Portal",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(bottom = 24.dp)
//        )
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("SuperAdmin Email") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        errorMessage?.let {
//            Text(
//                text = it,
//                color = MaterialTheme.colorScheme.error,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = {
//                if (email.isBlank() || password.isBlank()) {
//                    errorMessage = "Please enter both email and password"
//                } else {
//                    isLoading = true
//                    loginSuperAdmin(context, email, password, navController) { error ->
//                        isLoading = false
//                        errorMessage = error
//                    }
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !isLoading
//        ) {
//            if (isLoading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(20.dp),
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
//            } else {
//                Text("Login as SuperAdmin")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Demo login button
//        Button(
//            onClick = {
//                email = demoCredentials["email"] ?: ""
//                password = demoCredentials["password"] ?: ""
//                navController.navigate(Routes.PendingApprovalsScreen)
//                isLoading = true
//                loginSuperAdmin(context, email, password, navController) { error ->
//                    isLoading = false
//                    errorMessage = error
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.secondaryContainer,
//                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
//            )
//        ) {
//            Text("Use Demo SuperAdmin Account")
//        }
//    }
//}
//
//fun loginSuperAdmin(
//    context: Context,
//    email: String,
//    password: String,
//    navController: NavHostController,
//    onError: (String?) -> Unit
//) {
//    val auth = FirebaseAuth.getInstance()
//
//    auth.signInWithEmailAndPassword(email, password)
//        .addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                // Verify this is actually a SuperAdmin
//                val user = auth.currentUser
//                if (user != null) {
//                    checkSuperAdminStatus(user.uid) { isSuperAdmin ->
//                        if (isSuperAdmin) {
//                            navController.navigate(Routes.SuperAdminDashboard) {
//                                popUpTo(Routes.SuperAdminLoginScreen) { inclusive = true }
//                            }
//                        } else {
//                            auth.signOut()
//                            onError("This account doesn't have SuperAdmin privileges")
//                        }
//                    }
//                }
//            } else {
//                onError(task.exception?.message ?: "Login failed")
//            }
//        }
//}
//
//fun checkSuperAdminStatus(uid: String, callback: (Boolean) -> Unit) {
//    FirebaseFirestore.getInstance()
//        .collection("users")
//        .document(uid)
//        .get()
//        .addOnSuccessListener { document ->
//            callback(document.getBoolean("isSuperAdmin") ?: false)
//        }
//        .addOnFailureListener {
//            callback(false)
//        }
//}