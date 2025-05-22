package com.example.fit2081a1_yang_xingyu_33533563.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity
import com.example.fit2081a1_yang_xingyu_33533563.util.getPersonaImagePainter

@Composable
fun PersonaCard(
    persona: PersonaEntity,
    isSelected: Boolean,
    onPersonaSelect: (String) -> Unit
) {
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1.0f, label = "scale")
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "borderColor"
    )
    val borderWidth by animateDpAsState(targetValue = if (isSelected) 2.dp else 0.dp, label = "borderWidth")

    var showInfoModal by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .scale(scale)
            .border(borderWidth, borderColor, CardDefaults.shape)
            .clip(CardDefaults.shape)
            .clickable {
                showInfoModal = true
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = getPersonaImagePainter(personaId = persona.personaID),
                contentDescription = persona.personaName,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = persona.personaName,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }

    if (showInfoModal) {
        PersonaInfoModal(
            persona = persona,
            showDialog = showInfoModal,
            onDismiss = { showInfoModal = false },
            onConfirm = { personaId ->
                onPersonaSelect(personaId)
            }
        )
    }
} 