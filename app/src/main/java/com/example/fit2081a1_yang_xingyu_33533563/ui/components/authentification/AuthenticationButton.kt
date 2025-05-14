package com.example.fit2081a1_yang_xingyu_33533563.ui.components.authentification

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fit2081a1_yang_xingyu_33533563.ui.screens.ShowLoadingEffect

@Composable
fun AuthenticationButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            ShowLoadingEffect()
    } else {
            Text(text)
        }
    }
}
