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
import com.example.fit2081a1_yang_xingyu_33533563.data.model.Persona
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager






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
fun PersonaSelectionDropdownField(prefManager: SharedPreferencesManager, userID: String) {
    var userPersona by remember { mutableStateOf(prefManager.getUserPersona(userID)) }
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
    ) {
        OutlinedTextField(
            value = userPersona,
            onValueChange = {userPersona = it},
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
            onDismissRequest = { expanded = false },
        ) {
            Persona.entries.forEach { persona ->
                DropdownMenuItem(
                    text = { Text(persona.personaName) },
                    onClick = {
                        userPersona = persona.personaName
                        expanded = false
                        prefManager.setUserPersona(userID, persona.personaName)
                    }
                )
            }
        }
    }
}
