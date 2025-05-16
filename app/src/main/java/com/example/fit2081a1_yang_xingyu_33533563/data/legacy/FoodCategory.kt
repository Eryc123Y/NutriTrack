package com.example.fit2081a1_yang_xingyu_33533563.data.legacy

enum class FoodCategory(val foodName: String, val foodDefId: String) {
    FRUIT("Fruit", 1.toString()),
    VEGETABLE("Vegetable", 2.toString()),
    GRAIN("Grain", 3.toString()),
    RED_MEAT("Red Meat", 4.toString()),
    SEAFOOD("Seafood", 5.toString()),
    POULTRY("Poultry", 6.toString()),
    FISH("Fish", 7.toString()),
    EGGS("Eggs", 8.toString()),
    NUTS_SEEDS("Nuts/Seeds", 9.toString());

    companion object {
        fun fromFoodDefId(string: String): FoodCategory {
            return FoodCategory.entries.firstOrNull { it.foodDefId == string }
                ?: throw IllegalArgumentException("Invalid foodDefId: $string")
        }

    }
}
