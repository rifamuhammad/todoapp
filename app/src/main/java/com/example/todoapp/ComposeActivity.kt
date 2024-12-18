package com.example.todoapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.kaushalvasava.org.apps.qrscanner.ui.navhost.AppNavHost
import com.kaushalvasava.org.apps.qrscanner.ui.theme.QRScannerTheme

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RequestComposePermission()
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}


@Composable
fun RequestComposePermission() {
    val launcher: ManagedActivityResultLauncher<String, Boolean> =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission Accepted: Do something
                Log.d("TAG", "PERMISSION GRANTED")
//                startService(intent)
//                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }

    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            LocalContext.current,
            android.Manifest.permission.CAMERA
        ) -> {
            // Some works that require permission
            Log.d("TAG", "Code requires permission")
        }

        else -> {
            // Asking for permission
            SideEffect {
                launcher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

}

