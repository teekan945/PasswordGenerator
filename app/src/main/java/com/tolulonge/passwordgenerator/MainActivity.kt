package com.tolulonge.passwordgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.tolulonge.passwordgenerator.ui.components.PasswordGeneratorScreen
import com.tolulonge.passwordgenerator.ui.theme.PassWordGeneratorTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PassWordGeneratorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PasswordGeneratorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

