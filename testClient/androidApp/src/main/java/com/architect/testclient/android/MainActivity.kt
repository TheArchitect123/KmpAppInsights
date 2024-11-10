package com.architect.testclient.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.architect.kmpappinsights.InsightsClient
import com.architect.kmpappinsights.contracts.TraceSeverityLevel
import com.architect.kmpessentials.KmpAndroid
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KmpAndroid.initializeApp(this) {

        }

        InsightsClient.configureInsightsClient("MY_INSIGHTS_KEY", 20)
        
        InsightsClient.writeCustomEvent(
            mapOf("" to ""),
            "MY CUSTOM EVENT"
        )

        InsightsClient.writeException(
            Exception("SOME CATASTROPHIC EXCEPTION TO LOG"),
            mapOf("" to ""),
        )

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
