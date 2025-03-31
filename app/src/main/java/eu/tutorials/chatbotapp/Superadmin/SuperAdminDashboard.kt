//package eu.tutorials.chatbotapp.Superadmin
//
//import android.content.Context
//import android.widget.Toast
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import eu.tutorials.chatbotapp.R
//import eu.tutorials.chatbotapp.Routes
//import java.text.SimpleDateFormat
//import java.util.Date
//
//@Composable
//fun SuperAdminDashboard(navController: NavHostController) {
//    val context = LocalContext.current
//    val auth = FirebaseAuth.getInstance()
//    val user = auth.currentUser
//    var pendingDocuments by remember { mutableStateOf<List<PendingDocument>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var showLogoutDialog by remember { mutableStateOf(false) }
//
//    // Check authentication
//    if (user == null) {
//        LaunchedEffect(Unit) {
//            navController.navigate(Routes.SuperAdminLoginScreen) {
//                popUpTo(Routes.SuperAdminLoginScreen) { inclusive = true }
//            }
//        }
//        return
//    }
//
//    // Load pending documents
//    LaunchedEffect(Unit) {
//        loadPendingDocuments { documents ->
//            pendingDocuments = documents
//            isLoading = false
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(24.dp)
//        ) {
//            // Header
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    "SuperAdmin Dashboard",
//                    style = MaterialTheme.typography.headlineMedium
//                )
//
//                IconButton(
//                    onClick = { showLogoutDialog = true }
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.logout_12237328),
//                        contentDescription = "Logout"
//                    )
//                }
//            }
//
//            Text(
//                "Logged in as ${user.email}",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            if (isLoading) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator()
//                }
//            } else {
//                if (pendingDocuments.isEmpty()) {
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text("No pending documents for approval")
//                    }
//                } else {
//                    LazyColumn {
//                        items(pendingDocuments) { document ->
//                            PendingDocumentCard(
//                                document = document,
//                                onApprove = { approveDocument(context, document) { success ->
//                                    if (success) {
//                                        pendingDocuments = pendingDocuments - document
//                                        Toast.makeText(
//                                            context,
//                                            "Document approved",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                } },
//                                onReject = { rejectDocument(context, document) { success ->
//                                    if (success) {
//                                        pendingDocuments = pendingDocuments - document
//                                        Toast.makeText(
//                                            context,
//                                            "Document rejected",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                } }
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    if (showLogoutDialog) {
//        AlertDialog(
//            onDismissRequest = { showLogoutDialog = false },
//            title = { Text("Confirm Logout") },
//            text = { Text("Are you sure you want to logout?") },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        auth.signOut()
//                        navController.navigate(Routes.SuperAdminLoginScreen) {
//                            popUpTo(Routes.SuperAdminDashboard) { inclusive = true }
//                        }
//                    }
//                ) {
//                    Text("Logout", color = MaterialTheme.colorScheme.error)
//                }
//            },
//            dismissButton = {
//                TextButton(
//                    onClick = { showLogoutDialog = false }
//                ) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}
//
//@Composable
//fun PendingDocumentCard(
//    document: PendingDocument,
//    onApprove: () -> Unit,
//    onReject: () -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = document.filename,
//                style = MaterialTheme.typography.titleMedium
//            )
//
//            Text(
//                text = "Uploaded by: ${document.uploadedBy}",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//            )
//
//            Text(
//                text = "Uploaded at: ${SimpleDateFormat("MMM dd, yyyy HH:mm").format(Date(document.timestamp))}",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Button(
//                onClick = { expanded = !expanded },
//                modifier = Modifier.fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant
//                )
//            ) {
//                Text(if (expanded) "Hide Content" else "Show Content")
//            }
//
//            AnimatedVisibility(visible = expanded) {
//                Column(modifier = Modifier.padding(top = 8.dp)) {
//                    Text(
//                        text = document.extractedText.take(500) + if (document.extractedText.length > 500) "..." else "",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.End
//            ) {
//                OutlinedButton(
//                    onClick = onReject,
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = MaterialTheme.colorScheme.error
//                    )
//                ) {
//                    Text("Reject")
//                }
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                Button(
//                    onClick = onApprove
//                ) {
//                    Text("Approve")
//                }
//            }
//        }
//    }
//}
//
//data class PendingDocument(
//    val id: String,
//    val filename: String,
//    val extractedText: String,
//    val uploadedBy: String,
//    val uploadedByEmail: String,
//    val timestamp: Long
//)
//
//fun loadPendingDocuments(callback: (List<PendingDocument>) -> Unit) {
//    FirebaseFirestore.getInstance()
//        .collection("pending_approval")
//        .get()
//        .addOnSuccessListener { querySnapshot ->
//            val documents = querySnapshot.documents.map { doc ->
//                PendingDocument(
//                    id = doc.id,
//                    filename = doc.getString("filename") ?: "",
//                    extractedText = doc.getString("extracted_text") ?: "",
//                    uploadedBy = doc.getString("uploaded_by") ?: "",
//                    uploadedByEmail = doc.getString("uploaded_by_email") ?: "",
//                    timestamp = doc.getLong("timestamp") ?: 0
//                )
//            }
//            callback(documents)
//        }
//        .addOnFailureListener {
//            callback(emptyList())
//        }
//}
//
//fun approveDocument(context: Context, document: PendingDocument, callback: (Boolean) -> Unit) {
//    val db = FirebaseFirestore.getInstance()
//    val batch = db.batch()
//
//    // Create document in approved collection
//    val approvedRef = db.collection("approved_documents").document(document.id)
//    val approvedData = hashMapOf(
//        "filename" to document.filename,
//        "extracted_text" to document.extractedText,
//        "uploaded_by" to document.uploadedBy,
//        "uploaded_by_email" to document.uploadedByEmail,
//        ("approved_by" to FirebaseAuth.getInstance().currentUser?.email ?: "") as Pair<Any, Any>,
//        "approved_at" to System.currentTimeMillis(),
//        "original_timestamp" to document.timestamp
//    )
//    batch.set(approvedRef, approvedData)
//
//    // Delete from pending collection
//    val pendingRef = db.collection("pending_approval").document(document.id)
//    batch.delete(pendingRef)
//
//    batch.commit()
//        .addOnSuccessListener {
//            callback(true)
//        }
//        .addOnFailureListener {
//            Toast.makeText(context, "Approval failed: ${it.message}", Toast.LENGTH_SHORT).show()
//            callback(false)
//        }
//}
//
//fun rejectDocument(context: Context, document: PendingDocument, callback: (Boolean) -> Unit) {
//    FirebaseFirestore.getInstance()
//        .collection("pending_approval")
//        .document(document.id)
//        .delete()
//        .addOnSuccessListener {
//            // Optional: You might want to notify the admin who uploaded this
//            callback(true)
//        }
//        .addOnFailureListener {
//            Toast.makeText(context, "Rejection failed: ${it.message}", Toast.LENGTH_SHORT).show()
//            callback(false)
//        }
//}