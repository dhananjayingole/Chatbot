package eu.tutorials.chatbotapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BotScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.backgroun), // Replace with your image
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Circular Buttons at Bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CircularButton(text = "Student", onClick = {
                navController.navigate(Routes.StudentLoginScreen)
            })
            CircularButton(text = "Admin", onClick = {
                navController.navigate(Routes.AdminLoginScreen)
            })
        }
    }
}

@Composable
fun CircularButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(100.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Text(text, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}
