package eu.tutorials.chatbotapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.tutorials.chatbotapp.Notifications.ShowNotification

@Composable
fun NavGraph(navController: NavHostController, viewModel: ChatViewModel) {
    NavHost(navController = navController, startDestination = Routes.CongratsLottieAnim) {
        composable(Routes.BotScreen) {
            BotScreen(navController = navController)
        }
        composable(Routes.AdminScreen) {
            AdminScreen(navController = navController)
        }
        composable(Routes.PdfListScreen) {
            PdfListScreen(navController = navController)
        }
        composable(Routes.ChatScreen) {
            ChatScreen(viewModel = viewModel, navController = navController)
        }
        composable(Routes.AdminLoginScreen) {
            AdminLoginScreen(navController = navController)
        }
        composable(Routes.CreateAdminProfile) {
            CreateAdminProfile(navController = navController)
        }
        composable(Routes.StudentLoginScreen){
            StudentLoginScreen(navController)
        }
        composable(Routes.StudentSignupScreen){
            StudentSignupScreen(navController)
        }
        composable(Routes.CongratsLottieAnim){
            CongratsLottieAnim(modifier = Modifier , navController)
        }
        composable(Routes.ShowNotification){
            ShowNotification()
        }
    }
}















