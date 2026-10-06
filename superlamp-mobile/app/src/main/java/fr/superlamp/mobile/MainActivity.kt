package fr.superlamp.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fr.superlamp.mobile.ui.SuperLampNavHost
import fr.superlamp.mobile.ui.theme.SuperLampTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperLampTheme {
                SuperLampNavHost()
            }
        }
    }
}
