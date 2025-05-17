package com.example.fit2081a1_yang_xingyu_33533563.view.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity
import com.example.fit2081a1_yang_xingyu_33533563.util.getPersonaImagePainter

@Composable
fun PersonaInfoModal(
    persona: PersonaEntity,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = persona.personaName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = getPersonaImagePainter(personaId = persona.personaID),
                        contentDescription = persona.personaName,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        text = persona.personaDescription.toString(),
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
