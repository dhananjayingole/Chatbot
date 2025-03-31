//package eu.tutorials.chatbotapp.Superadmin
//
//import android.util.Log
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Approval
//import androidx.compose.material.icons.filled.Warning
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.Query
//import eu.tutorials.chatbotapp.Routes
//import java.text.SimpleDateFormat
//import java.util.Date
//
//@Composable
//fun PendingApprovalsScreen(navController: NavHostController) {
//    val db = FirebaseFirestore.getInstance()
//    val pendingDocs = remember { mutableStateListOf<PendingDocument>() }
//    val isLoading = remember { mutableStateOf(true) }
//    val auth = FirebaseAuth.getInstance()
//    val showDemoLogin = remember { mutableStateOf(false) }
//
//    // Auto-navigate if using demo credentials
//    LaunchedEffect(Unit) {
//        if (auth.currentUser?.email == "2023BCS@sggs.ac.in") {
//            loadPendingDocuments(db, pendingDocs, isLoading)
//        } else {
//            showDemoLogin.value = true
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        if (showDemoLogin.value) {
//            DemoLoginView(
//                onLoginSuccess = {
//                    showDemoLogin.value = false
//                    loadPendingDocuments(db, pendingDocs, isLoading)
//                },
//                navController = navController
//            )
//        } else {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Approval,
//                        contentDescription = "Approvals",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = "Pending Approvals",
//                        style = MaterialTheme.typography.headlineSmall,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                if (isLoading.value) {
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        CircularProgressIndicator()
//                    }
//                } else if (pendingDocs.isEmpty()) {
//                    Column(
//                        modifier = Modifier.fillMaxSize(),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Warning,
//                            contentDescription = "No documents",
//                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//                            modifier = Modifier.size(48.dp)
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = "No pending documents for approval",
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                        )
//                    }
//                } else {
//                    LazyColumn {
//                        items(pendingDocs) { doc ->
//                            PendingDocumentCard(doc, navController)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun DemoLoginView(
//    onLoginSuccess: () -> Unit,
//    navController: NavHostController
//) {
//    val auth = FirebaseAuth.getInstance()
//    val isLoading = remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            "SuperAdmin Demo Login",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(bottom = 24.dp)
//        )
//
//        Button(
//            onClick = {
//                isLoading.value = true
//                auth.signInWithEmailAndPassword("2023BCS@sggs.ac.in", "Dhanu#123")
//                    .addOnCompleteListener { task ->
//                        isLoading.value = false
//                        if (task.isSuccessful) {
//                            onLoginSuccess()
//                        } else {
//                            navController.popBackStack()
//                        }
//                    }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !isLoading.value
//        ) {
//            if (isLoading.value) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(20.dp),
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
//            } else {
//                Text("Login as Demo SuperAdmin")
//            }
//        }
//    }
//}
//
//@Composable
//private fun PendingDocumentCard(
//    doc: PendingDocument,
//    navController: NavHostController
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant
//        )
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = doc.filename,
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Uploaded by: ${doc.uploadedBy}",
//                style = MaterialTheme.typography.bodySmall
//            )
//            Text(
//                text = "On: ${SimpleDateFormat("MMM dd, yyyy HH:mm").format(Date(doc.uploadedAt))}",
//                style = MaterialTheme.typography.bodySmall
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = doc.textPreview,
//                style = MaterialTheme.typography.bodyMedium,
//                maxLines = 3
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.End
//            ) {
//                TextButton(
//                    onClick = {
//                        navController.navigate("${Routes.ApprovalDetail}/${doc.id}")
//                    }
//                ) {
//                    Text("Review Document")
//                }
//            }
//        }
//    }
//}
//
//private fun loadPendingDocuments(
//    db: FirebaseFirestore,
//    pendingDocs: MutableList<PendingDocument>,
//    isLoading: MutableState<Boolean>
//) {
//    db.collection("pdf_documents")
//        .whereEqualTo("status", "pending")
//        .orderBy("timestamp", Query.Direction.DESCENDING)
//        .get()
//        .addOnSuccessListener { documents ->
//            pendingDocs.clear()
//            documents.forEach { doc ->
//                pendingDocs.add(
//                    PendingDocument(
//                        id = doc.id,
//                        filename = doc.getString("filename") ?: "Unknown",
//                        uploadedBy = doc.getString("uploaded_by") ?: "Unknown",
//                        uploadedAt = doc.getLong("timestamp") ?: 0,
//                        textPreview = doc.getString("extracted_text")?.take(200) ?: ""
//                    )
//                )
//            }
//            isLoading.value = false
//        }
//        .addOnFailureListener { e ->
//            Log.e("PendingApprovals", "Error loading documents", e)
//            isLoading.value = false
//        }
//}
//
//data class PendingDocument(
//    val id: String,
//    val filename: String,
//    val uploadedBy: String,
//    val uploadedAt: Long,
//    val textPreview: String
//)