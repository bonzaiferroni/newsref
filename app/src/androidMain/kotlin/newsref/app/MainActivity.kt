package newsref.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val dataStore = remember { createDataStore(this)}

            App(StartRoute, dataStore, { }, { })
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // App(StartRoute, null, { }, { })
}