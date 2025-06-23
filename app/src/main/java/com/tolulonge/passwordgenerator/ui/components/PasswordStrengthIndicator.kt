package com.tolulonge.passwordgenerator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.nulabinc.zxcvbn.Zxcvbn

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = calculatePasswordStrengthFromLib(password)
    val color = when (strength) {
        0 -> Color.Red
        1 -> Color.Red
        2 -> Color.Yellow
        3 -> Color.Green
        else -> Color.Green
    }

    val strengthText = when (strength) {
        0 -> "Very Weak"
        1 -> "Weak"
        2 -> "Moderate"
        3 -> "Strong"
        else -> "Very Strong"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Strength: $strengthText")
        LinearProgressIndicator(
            progress = { (strength + 1) / 5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

fun calculatePasswordStrengthFromLib(password: String): Int {
    if (password.isEmpty()) return 0
    val zxcvbn = Zxcvbn()
    return zxcvbn.measure(password).score
}