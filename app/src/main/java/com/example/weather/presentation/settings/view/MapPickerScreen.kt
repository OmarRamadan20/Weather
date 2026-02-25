import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapPickerScreen(
    onLocationSelected: (LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            if (activity == null) {
        MapLayout(
            isPermissionGranted = false,
            onLocationSelected = onLocationSelected,
            onDismiss = onDismiss
        )
    } else {
        val locationPermissionState = rememberPermissionState(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        LaunchedEffect(Unit) {
            locationPermissionState.launchPermissionRequest()
        }

        MapLayout(
            isPermissionGranted = locationPermissionState.status.isGranted,
            onLocationSelected = onLocationSelected,
            onDismiss = onDismiss
        )
    }
        }
}

@Composable
fun MapLayout(
    isPermissionGranted: Boolean,
    onLocationSelected: (LatLng) -> Unit,
    onDismiss: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 10f)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = isPermissionGranted
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = isPermissionGranted
            )
        )

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(50.dp).align(Alignment.Center).padding(bottom = 25.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancel")
                }
                Button(

                    onClick = { val target = cameraPositionState.position.target
                        Log.d("MapDebug", "Button Clicked at: ${target.latitude}")
                        onLocationSelected(target) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirm Location")
                }
            }
        }
    }
}