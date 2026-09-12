package com.example

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.PilatesApp
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.ReminderManager
import com.example.utils.ThemePreferencesManager
import com.example.ui.theme.AppThemeColor
import java.util.concurrent.Executor

class MainActivity : FragmentActivity() {

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Notification Channel for Reminders
        ReminderManager.createNotificationChannel(this)
        
        // Request POST_NOTIFICATIONS permission for Android 13+ using 16-bit request code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }

        enableEdgeToEdge()
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemTheme) }
            
            val themePrefs = remember { ThemePreferencesManager(this) }
            var appThemeColor by remember { mutableStateOf(themePrefs.themeColor) }

            var isAuthenticated by remember { mutableStateOf(false) }
            
            // Trigger authentication on startup
            androidx.compose.runtime.LaunchedEffect(Unit) {
                if (!isAuthenticated) {
                    authenticateBiometrics { success ->
                        isAuthenticated = success
                    }
                }
            }

            MyApplicationTheme(
                darkTheme = isDarkTheme,
                themeColor = appThemeColor
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (isAuthenticated) {
                        PilatesApp(
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = !isDarkTheme },
                            currentThemeColor = appThemeColor,
                            onThemeColorChange = { newColor ->
                                appThemeColor = newColor
                                themePrefs.themeColor = newColor
                            }
                        )
                    } else {
                        LockScreen(onUnlockClicked = {
                            authenticateBiometrics { success ->
                                isAuthenticated = success
                            }
                        })
                    }
                }
            }
        }
    }

    private fun authenticateBiometrics(onResult: (Boolean) -> Unit) {
        val biometricManager = BiometricManager.from(this)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        val canAuthenticate = biometricManager.canAuthenticate(authenticators)

        // If no biometric or device credential is enrolled, allow entry gracefully
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            onResult(true)
            return
        }

        val executor: Executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS ||
                        errorCode == BiometricPrompt.ERROR_HW_NOT_PRESENT ||
                        errorCode == BiometricPrompt.ERROR_HW_UNAVAILABLE
                    ) {
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onResult(false)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acesso Restrito")
            .setSubtitle("Autentique-se para acessar os prontuários dos pacientes")
            .setAllowedAuthenticators(authenticators)
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            // Fallback if exception occurs
            onResult(true)
        }
    }
}

@Composable
fun LockScreen(onUnlockClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Bloqueado",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Aplicativo Bloqueado",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Prontuários e dados de pacientes são protegidos.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onUnlockClicked,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Desbloquear")
        }
    }
}
