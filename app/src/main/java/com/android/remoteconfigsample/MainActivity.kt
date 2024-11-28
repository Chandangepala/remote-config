package com.android.remoteconfigsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.remoteconfigsample.ui.theme.RemoteConfigSampleTheme
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RemoteConfigSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainUI(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updated = task.result
                println("Config params updated: $updated")
            } else {
                println("Fetch failed")
            }
        }
    }
}

@Composable
fun MainUI(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        Greeting(name = "Chandan")
        TypeSomething()
        ButtonDoSomething(snackbarHostState = snackbarHostState)
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(true) }
    isVisible = Firebase.remoteConfig.getBoolean("show_background_image")
    Box(modifier = Modifier.fillMaxWidth()) {
        if(isVisible){
            Text(
                text = "Hello $name!",
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
                    .padding(56.dp),
            )
        }
    }
}

@Composable
fun TypeSomething(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = text,
            onValueChange = {
                text = it
            },
            label = { Text("Type something...") },
            modifier = Modifier.align(Alignment.Center).padding(50.dp)
        )
    }
}

@Composable
fun ButtonDoSomething(modifier: Modifier = Modifier, snackbarHostState: SnackbarHostState) {
    ElevatedButton(
        onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                snackbarHostState.showSnackbar(
                    message = "Button clicked",
                    duration = SnackbarDuration.Short
                )
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .size(width = 150.dp, height = 48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Blue,
            contentColor = Color.White
        )
    ) {
        Text(text = "Do Something")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RemoteConfigSampleTheme {
        Greeting("Android")
        TypeSomething()
    }
}