package eu.tutorials.chatbotapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import eu.tutorials.chatbotapp.Notifications.createNotification
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun PdfListScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val pdfList = remember { mutableStateListOf<Map<String, Any>>() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedPdfId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        fetchPdfDocuments(db, pdfList) {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Extracted PDFs",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (pdfList.isEmpty()) {
            Text(
                text = "No PDFs found",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(pdfList) { pdf ->
                    val filename = pdf["filename"] as? String ?: "Unknown"
                    val documentId = pdf["documentId"] as? String ?: ""
                    val previewText = (pdf["text"] as? String ?: "No text available")
                        .take(100) + if ((pdf["text"] as? String)?.length ?: 0 > 100) "..." else ""

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("pdfDetail/$documentId")
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = filename,
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                IconButton(
                                    onClick = {
                                        selectedPdfId = documentId
                                        showDeleteDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete PDF",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = previewText,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Uploaded: ${
                                    SimpleDateFormat("MMM dd, yyyy").format(
                                        Date(pdf["timestamp"] as? Long ?: 0)
                                    )
                                }",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this PDF? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedPdfId?.let { id ->
                            deletePdfDocument(db, id, context) { success ->
                                if (success) {
                                    pdfList.removeAll { it["documentId"] == id }
                                    Toast.makeText(context, "PDF deleted successfully", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun fetchPdfDocuments(
    db: FirebaseFirestore,
    pdfList: MutableList<Map<String, Any>>,
    onComplete: () -> Unit
) {
    db.collection("pdf_documents")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { documents ->
            pdfList.clear()
            documents.forEach { document ->
                try {
                    pdfList.add(mapOf(
                        "documentId" to document.id,
                        "filename" to (document.getString("filename") ?: "Unknown"),
                        "text" to (document.getString("extracted_text") ?: "No text available"),
                        "timestamp" to (document.getLong("timestamp") ?: 0L)
                    ))
                } catch (e: Exception) {
                    Log.e("PDF_FETCH", "Error processing document ${document.id}", e)
                }
            }
            onComplete()
        }
        .addOnFailureListener { e ->
            Log.e("PDF_FETCH", "Error fetching documents", e)
            onComplete()
        }
}

private fun deletePdfDocument(
    db: FirebaseFirestore,
    documentId: String,
    context: Context,
    onComplete: (Boolean) -> Unit
) {
    db.collection("pdf_documents")
        .document(documentId)
        .delete()
        .addOnSuccessListener {
            createNotification(context)
            onComplete(true)
        }
        .addOnFailureListener {
            onComplete(false)
        }
}
