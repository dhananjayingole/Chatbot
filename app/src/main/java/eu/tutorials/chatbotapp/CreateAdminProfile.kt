package eu.tutorials.chatbotapp

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun CreateAdminProfile(navController: NavHostController) {
    val context = LocalContext.current

    var directorName by remember { mutableStateOf("") }
    var phoneNo by remember { mutableStateOf("") }
    var collegeName by remember { mutableStateOf("") }
    var collegeCode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var naacNo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference("AdminProfiles")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Create Admin Profile", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = directorName,
            onValueChange = { directorName = it },
            label = { Text("Director Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNo,
            onValueChange = { phoneNo = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = collegeName,
            onValueChange = { collegeName = it },
            label = { Text("College Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = collegeCode,
            onValueChange = { collegeCode = it },
            label = { Text("College Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = naacNo,
            onValueChange = { naacNo = it },
            label = { Text("NAAC No") },
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    createAdminProfile(
                        context, auth, database, directorName, phoneNo, collegeName,
                        collegeCode, city, naacNo, email, password, navController
                    )
                } else {
                    Toast.makeText(context, "Email and Password cannot be empty!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Profile")
        }
    }
}

fun createAdminProfile(
    context: Context,
    auth: FirebaseAuth,
    database: com.google.firebase.database.DatabaseReference,
    directorName: String,
    phoneNo: String,
    collegeName: String,
    collegeCode: String,
    city: String,
    naacNo: String,
    email: String,
    password: String,
    navController: NavHostController
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    Log.d("Firebase", "User authenticated with UID: $userId") // Debugging
                    val adminData = mapOf(
                        "directorName" to directorName,
                        "phoneNo" to phoneNo,
                        "collegeName" to collegeName,
                        "collegeCode" to collegeCode,
                        "city" to city,
                        "naacNo" to naacNo,
                        "email" to email
                    )
                    database.child(userId).setValue(adminData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Profile Created Successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate(Routes.AdminScreen) {
                                popUpTo(Routes.CreateAdminProfile) { inclusive = true }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error writing to database", e)
                            Toast.makeText(context, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Error: User ID not found!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("Firebase", "Registration Failed", task.exception)
                Toast.makeText(context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}