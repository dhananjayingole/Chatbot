package eu.tutorials.chatbotapp

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID

@Composable
fun AdminScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (user == null) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.AdminLoginScreen) {
                popUpTo(Routes.AdminLoginScreen) { inclusive = true }
            }
        }
        return
    }

    val pickPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedPdfUri = uri }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.browser_2525936),
                    contentDescription = "Admin",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Admin Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Logged in as ${user.email?.takeWhile { it != '@' }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // PDF Operations Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PDF Operations",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedButton(
                        onClick = { pickPdfLauncher.launch("application/pdf") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_cloud_upload_24),
                            contentDescription = "Upload",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select PDF File")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    selectedPdfUri?.let { uri ->
                        Text(
                            text = "Selected: ${uri.lastPathSegment?.takeLast(30) ?: "Unknown file"}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Button(
                            onClick = {
                                isProcessing = true
                                processAndUploadPdf(context, uri) { success ->
                                    isProcessing = false
                                    if (success) {
                                        Toast.makeText(context, "PDF processed successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = !isProcessing,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_cloud_upload_24),
                                    contentDescription = "Upload",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Process & Upload")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilledTonalButton(
                    onClick = { navController.navigate(Routes.PdfListScreen) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewAgenda,
                        contentDescription = "View PDFs",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Extracted PDFs")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.logout_12237328),
                        contentDescription = "Logout",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        auth.signOut()
                        navController.navigate(Routes.AdminLoginScreen) {
                            popUpTo(Routes.AdminScreen) { inclusive = true }
                        }
                    }
                ) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
fun processAndUploadPdf(context: Context, pdfUri: Uri, onComplete: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val inputStream = context.contentResolver.openInputStream(pdfUri)
                ?: throw Exception("Failed to open PDF file")

            val extractedText = extractTextFromPdf(inputStream)

            if (extractedText.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No text could be extracted", Toast.LENGTH_SHORT).show()
                }
                onComplete(false)
                return@launch
            }

            val fileName = pdfUri.lastPathSegment ?: UUID.randomUUID().toString()
            val pdfData = hashMapOf(
                "filename" to fileName,
                "extracted_text" to extractedText,
                "full_text_available" to true,
                "text_length" to extractedText.length,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("pdf_documents").document(fileName).set(pdfData).await()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "PDF processed successfully!", Toast.LENGTH_SHORT).show()
            }
            onComplete(true)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            onComplete(false)
        }
    }
}
private suspend fun storeLargeTextInChunks(db: FirebaseFirestore, fileName: String, fullText: String, chunkSize: Int = 900_000) {
    val chunks = fullText.chunked(chunkSize)
    val documentRef = db.collection("pdf_documents").document(fileName)

    documentRef.set(mapOf(
        "filename" to fileName,
        "full_text_available" to true,
        "text_length" to fullText.length,
        "chunk_count" to chunks.size,
        "timestamp" to System.currentTimeMillis()
    )).await()

    chunks.forEachIndexed { index, chunk ->
        documentRef.collection("text_chunks")
            .document(index.toString())
            .set(mapOf("content" to chunk))
            .await()
    }
}

fun extractTextFromPdf(inputStream: InputStream): String {
    return try {
        PdfDocument(PdfReader(inputStream)).use { pdfDocument ->
            buildString {
                for (i in 1..pdfDocument.numberOfPages) {
                    append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i)))
                    if (i < pdfDocument.numberOfPages) append("\n")
                }
            }
        }
    } catch (e: Exception) {
        Log.e("PDF_EXTRACT", "Error extracting text", e)
        ""
    }
}