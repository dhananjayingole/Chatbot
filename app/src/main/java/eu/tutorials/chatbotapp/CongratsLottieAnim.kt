package eu.tutorials.chatbotapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun CongratsLottieAnim(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        val lottieComposition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.chatbots)
        )

        // Delay and navigate after 3 seconds
        LaunchedEffect(Unit) {
            delay(3000)
            navController.navigate(Routes.BotScreen) {
                popUpTo(Routes.CongratsLottieAnim) {
                    inclusive = true
                } // removes Congrats from back stack
            }
        }

        LottieAnimation(
            modifier = modifier,
            composition = lottieComposition,
            iterations = LottieConstants.IterateForever,
            speed = 1.5f
        )
    }
}
