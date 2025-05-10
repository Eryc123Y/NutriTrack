package com.example.fit2081a1_yang_xingyu_33533563.util

import android.content.Context
import com.example.fit2081a1_yang_xingyu_33533563.data.csv.readColumn
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.UserInfo


/**
 * Created by Xingyu Yang
 * SharedPreferencesManager takes care of input validation for the app.
 * including login, registration, and questionnaire responses.
 */

/**
 * Get a map of UserId to a given attribute
 */
fun getLoginValidationData(
    context: Context,
    targetColumn: String = UserInfo.PHONENUMBER.infoName,
    filePath: String = "testUsers.csv"
): Map<String, Any> {
    var userData = mapOf<String, Any>()
    val idCol = readColumn(context, UserInfo.USERID.infoName,  filePath)
    val targetCol = readColumn(context, targetColumn, filePath)
    userData = idCol.zip(targetCol).toMap() as Map<String, Any>
    return userData
}