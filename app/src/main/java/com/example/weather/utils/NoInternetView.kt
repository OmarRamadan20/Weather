package com.example.weather.utils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.presentation.home.view.AzureBlue
import com.example.weather.presentation.home.view.SoftBlue
import com.example.weather.presentation.home.view.SoftPink

@Composable
fun NoInternetView(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SoftBlue, SoftPink))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(120.dp).shadow(24.dp, CircleShape),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    modifier = Modifier.padding(30.dp),
                    tint = AzureBlue
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Whoops! No Connection",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Black)
            )

            Text(
                text = "Please check your internet settings and try again to get the latest weather updates.",
                style = TextStyle(fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzureBlue),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text("Retry Connection", fontWeight = FontWeight.Bold)
            }
        }
    }
}