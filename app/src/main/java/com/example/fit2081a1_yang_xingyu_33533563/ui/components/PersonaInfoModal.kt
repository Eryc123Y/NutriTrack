package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.R
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.Persona

@Composable
fun PersonaInfoModal(
    persona: Persona,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = persona.personaName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)
            },
            text = {
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Load image based on persona type
                    val resourceId = when (persona.personaName) {
                        "Health Devotee" -> R.drawable.persona_1
                        "Mindful Eater" -> R.drawable.persona_2
                        "Wellness Striver" -> R.drawable.persona_3
                        "Balance Seeker" -> R.drawable.persona_4
                        "Health Procrastinator" -> R.drawable.persona_5
                        "Food Carefree" -> R.drawable.persona_6
                        else -> R.drawable.persona_1
                    }

                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = persona.personaName,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        text = persona.personaDescription,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }
        )
    }
}
