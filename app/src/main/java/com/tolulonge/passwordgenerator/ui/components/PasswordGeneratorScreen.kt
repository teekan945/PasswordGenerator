package com.tolulonge.passwordgenerator.ui.components

/**
 * Main screen for password generation with comprehensive customization options.
 * 
 * This composable provides a complete user interface for generating secure passwords with the following features:
 * - Password length configuration via slider (4-32 characters)
 * - Character type selection including uppercase letters, lowercase letters, numbers, and symbols
 * - Real-time password generation using cryptographically secure random generation
 * - Password strength analysis and visual indicator powered by the zxcvbn library
 * - One-tap clipboard functionality for easy password copying
 * - Material3 design system integration with proper theming and accessibility
 * - State management handled by Hilt-injected ViewModel using StateFlow for reactive updates
 * 
 * The screen follows Android architecture best practices with clear separation of concerns
 * between UI presentation and business logic handled in the ViewModel layer.
 */

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tolulonge.passwordgenerator.viewmodel.PasswordGeneratorViewModel

@Composable
fun PasswordGeneratorScreen(
    modifier: Modifier = Modifier,
    viewModel: PasswordGeneratorViewModel = hiltViewModel()
) {
    val passwordState by viewModel.passwordState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Password Generator",
            style = MaterialTheme.typography.headlineMedium
        )

        // Password Display
        if (passwordState.password.isNotEmpty()) {
            Text(
                text = passwordState.password,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            // Password Strength Indicator
            PasswordStrengthIndicator(password = passwordState.password)
        }

        // Length Slider
        Text("Password Length: ${passwordState.length}")
        Slider(
            value = passwordState.length.toFloat(),
            onValueChange = { viewModel.updateLength(it.toInt()) },
            valueRange = 4f..32f,
            steps = 27
        )

        // Character Type Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CheckboxWithLabel(
                checked = passwordState.includeLowercase,
                onCheckedChange = { viewModel.updateIncludeLowercase(it) },
                label = "Lowercase"
            )
            CheckboxWithLabel(
                checked = passwordState.includeUppercase,
                onCheckedChange = { viewModel.updateIncludeUppercase(it) },
                label = "Uppercase"
            )
            CheckboxWithLabel(
                checked = passwordState.includeNumbers,
                onCheckedChange = { viewModel.updateIncludeNumbers(it) },
                label = "Numbers"
            )
            CheckboxWithLabel(
                checked = passwordState.includeSymbols,
                onCheckedChange = { viewModel.updateIncludeSymbols(it) },
                label = "Symbols"
            )
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.generatePassword() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Generate Password")
            }

            Button(
                onClick = {
                    val clip = ClipData.newPlainText("password", passwordState.password)
                    clipboardManager.setPrimaryClip(clip)
                    Toast.makeText(context, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                enabled = passwordState.password.isNotEmpty()
            ) {
                Text("Copy")
            }
        }
    }
}