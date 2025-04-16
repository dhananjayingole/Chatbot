package eu.tutorials.chatbotapp.Notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import eu.tutorials.chatbotapp.MainActivity
import eu.tutorials.chatbotapp.MyApp
import eu.tutorials.chatbotapp.R

@Composable
fun ShowNotification(){
    val context = LocalContext.current
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){

    }

    LaunchedEffect(key1 = true){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Button(onClick = {
            createNotification(context)
        }){
            Text("Show Notifications")
        }
    }
}
fun createNotification(context: Context) {
    val notificationManager = MyApp.notificationManager

    val notification = NotificationCompat.Builder(context, "channel_id")
        .setContentTitle("PRAVESH")
        .setContentText("Update Your College Data!!")
        .setSmallIcon(R.drawable.chattbot)
        .setAutoCancel(true)
        .setContentIntent(createPendingIntent(context))
        .build()

    notificationManager.notify(100, notification)
}


private fun createPendingIntent(context:Context) :PendingIntent {

    val flag = PendingIntent.FLAG_IMMUTABLE

    val intent = Intent(context, MainActivity::class.java).apply {
        putExtra("data", "hey this is Notification")
    }

    return PendingIntent.getActivities(context, 100, arrayOf(intent), flag)
}