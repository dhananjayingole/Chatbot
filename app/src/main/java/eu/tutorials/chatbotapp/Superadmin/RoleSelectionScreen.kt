//package eu.tutorials.chatbotapp.Superadmin
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AdminPanelSettings
//import androidx.compose.material.icons.filled.Security
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import eu.tutorials.chatbotapp.Routes
//
//@Composable
//fun RoleSelectionScreen(navController: NavHostController) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Select Your Role",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(bottom = 48.dp)
//        )
//
//        Button(
//            onClick = { navController.navigate(Routes.AdminLoginScreen) },
//            modifier = Modifier.fillMaxWidth(),
//            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.AdminPanelSettings,
//                contentDescription = "Admin",
//                modifier = Modifier.padding(end = 8.dp)
//            )
//            Text("Continue as Admin")
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = { navController.navigate(Routes.SuperAdminLoginScreen) },
//            modifier = Modifier.fillMaxWidth(),
//            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
//            )
//        ) {
//            Icon(
//                imageVector = Icons.Default.Security,
//                contentDescription = "SuperAdmin",
//                modifier = Modifier.padding(end = 8.dp)
//            )
//            Text("Continue as SuperAdmin")
//        }
//    }
//}