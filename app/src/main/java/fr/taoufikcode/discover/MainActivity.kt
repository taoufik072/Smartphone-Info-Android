package fr.taoufikcode.discover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import fr.taoufikcode.discover.navigation.NavGraph
import fr.taoufikcode.discover.ui.theme.DiscoverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiscoverTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}
