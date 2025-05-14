package com.example.fit2081a1_yang_xingyu_33533563.ui.components.authentification

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AuthenticationButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
) {
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text)
        }
    }
}
