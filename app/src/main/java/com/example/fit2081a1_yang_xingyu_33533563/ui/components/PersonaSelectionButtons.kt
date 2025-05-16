package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.Persona
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity

@Composable
fun PersonaButton(persona: Persona) {
    var showDialog by remember { mutableStateOf(false) }
    Button(
        onClick = { showDialog = true }, // activate corresponding persona modal
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = persona.personaName,
            style = TextStyle(fontSize = 12.sp),
            textAlign = TextAlign.Center,
        )
    }
    PersonaInfoModal(
        persona = persona,
        showDialog = showDialog,
        onDismiss = { showDialog = false }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaSelectionDropdownField(
    selectedPersona: String,
    onPersonaSelected: (String) -> Unit,
    personas: List<PersonaEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Find the selected persona name based on ID
    val displayName = remember(selectedPersona, personas) {
        // Direct string comparison since personaID is now a String
        personas.find { it.personaID == selectedPersona }?.personaName ?:
        if (selectedPersona.isNotEmpty()) selectedPersona else ""
    }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth().padding(bottom = 4.dp)
    ) {
        OutlinedTextField(
            value = displayName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            shape = MaterialTheme.shapes.medium,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            label = { Text("Select your Persona") },
            placeholder = { Text("Select your Persona") }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (personas.isEmpty()) {
                // Fallback to enum if no personas from database
                Persona.entries.forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona.personaName) },
                        onClick = {
                            onPersonaSelected(persona.personaId)
                            expanded = false
                        }
                    )
                }
            } else {
                personas.forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona.personaName) },
                        onClick = {
                            onPersonaSelected(persona.personaID)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
