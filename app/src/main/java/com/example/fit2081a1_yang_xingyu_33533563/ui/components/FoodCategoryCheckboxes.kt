package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager

enum class FoodCategory(val foodName: String) {
    FRUIT("Fruit"),
    VEGETABLE("Vegetable"),
    GRAIN("Grain"),
    RED_MEAT("Red Meat"),
    SEAFOOD("Seafood"),
    POULTRY("Poultry"),
    FISH("Fish"),
    EGGS("Eggs"),
    NUTS_SEEDS("Nuts/Seeds"),
}
// todo: use sharedPreference to manage user status
@Composable
fun CheckboxWithText(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        //horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
        )
    }
}

