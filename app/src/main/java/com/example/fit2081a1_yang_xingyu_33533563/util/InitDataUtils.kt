package com.example.fit2081a1_yang_xingyu_33533563.util

import android.content.Context
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.FoodCategory
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.Gender
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.Persona
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.ScoreTypes
import com.example.fit2081a1_yang_xingyu_33533563.data.legacy.UserInfo
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.FoodCategoryDefinitionEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.ScoreTypeDefinitionEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserFoodPreferenceEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserScoreEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserTimePreferenceEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.FoodCategoryDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserFoodCategoryPreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserTimePreferenceRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.csv.readColumn
import java.io.BufferedReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

object InitDataUtils {
    /**
     * Check if database initialization is needed by checking if any users exist
     */
    suspend fun isDatabaseInitializationNeeded(userRepository: UserRepository): Boolean {
        return userRepository.getAllUsers().first().isEmpty()
        //return true // todo uncomment this line after testing
    }

    /**
     * Initialize database with data from CSV files if needed
     * This should be called from the Application class during startup
     */
    suspend fun initializeDatabaseAsNeeded(
        context: Context,
        userRepository: UserRepository,
        foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository,
        scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository,
        personaRepository: PersonaRepository,
        userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository,
        userTimePreferenceRepository: UserTimePreferenceRepository,
        userScoreRepository: UserScoreRepository
    ) {
        if (isDatabaseInitializationNeeded(userRepository)) {
            initAllRepositories(
                context,
                userRepository,
                foodCategoryDefinitionRepository,
                scoreTypeDefinitionRepository,
                personaRepository,
                userFoodCategoryPreferenceRepository,
                userTimePreferenceRepository,
                userScoreRepository
            )
        }
    }

    /**
     * Initialize all repositories from CSV data
     */
    suspend fun initAllRepositories(
        context: Context,
        userRepository: UserRepository,
        foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository,
        scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository,
        personaRepository: PersonaRepository,
        userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository,
        userTimePreferenceRepository: UserTimePreferenceRepository,
        userScoreRepository: UserScoreRepository
    ) {
        withContext(Dispatchers.IO) {
            // Initialize lookup tables first
            initFoodCategoryDefinitionRepository(foodCategoryDefinitionRepository)
            initScoreTypeDefinitionRepository(scoreTypeDefinitionRepository)
            initPersonaRepository(personaRepository)
            
            // Then initialize user data
            val userIds = initUserRepository(context, userRepository)
            
            // Initialize user-related data
            initUserFoodCategoryPreferences(userIds, userFoodCategoryPreferenceRepository)
            initUserTimePreferences(userIds, userTimePreferenceRepository)
            initUserScores(context, userScoreRepository, scoreTypeDefinitionRepository)
        }
    }

    /**
     * Initialize user repository from CSV data
     * @return List of user IDs for further initialization
     */
    private suspend fun initUserRepository(
        context: Context,
        userRepository: UserRepository,
    ): List<String> {
        val userIdsToReturn = mutableListOf<String>()
        
        try {
            // Fetch columns using CsvParser.readColumn
            val userIdCol = readColumn(context, UserInfo.USERID.infoName, "testUsers.csv")
            val phoneNumberCol = readColumn(context, UserInfo.PHONE_NUMBER.infoName, "testUsers.csv")
            val genderCol = readColumn(context, UserInfo.GENDER.infoName, "testUsers.csv")
            val fruitServeSizeCol = readColumn(context, "Fruitservesize", "testUsers.csv")

            // Ensure all columns have the same size (number of users)
            if (userIdCol.isEmpty()) {
                // No users found or error in reading UserID column
                return userIdsToReturn
            }
            val userCount = userIdCol.size
            if (phoneNumberCol.size != userCount || genderCol.size != userCount || fruitServeSizeCol.size != userCount) {
                throw IllegalArgumentException("CSV column size mismatch during user initialization.")
            }

            for (i in 0 until userCount) {
                val userId = userIdCol[i]
                val phoneNumber = phoneNumberCol[i]
                val gender = genderCol[i]
                val fruitServeSize = fruitServeSizeCol[i].toFloatOrNull()

                // Create and insert user entity
                val userEntity = UserEntity(
                    userId = userId,
                    userName = null, // Default to null, update on registration
                    userPhoneNumber = phoneNumber,
                    userGender = gender,
                    userFruitServingsize = fruitServeSize,
                    userPersonaId = null, // Will be set by user later
                    userHashedCredential = null, // Will be set upon registration
                    userIsRegistered = false
                )
                
                userRepository.insertUser(userEntity)
                userIdsToReturn.add(userId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return userIdsToReturn
    }

    /**
     * Initialize user food category preferences
     * This creates default (unchecked) preferences for all users and all food categories
     */
    private suspend fun initUserFoodCategoryPreferences(
        userIds: List<String>,
        userFoodCategoryPreferenceRepository: UserFoodCategoryPreferenceRepository
    ) {
        // For each user, create preferences for all food categories (initially unchecked)
        for (userId in userIds) {
            FoodCategory.entries.forEach { foodCategory ->
                val preference = UserFoodPreferenceEntity(
                    foodPrefUserId = userId,
                    foodPrefCategoryKey = foodCategory.foodDefId, // Using foodDefId as key instead of enum name
                    foodPrefCheckedStatus = false // Default to unchecked
                )
                userFoodCategoryPreferenceRepository.insert(preference)
            }
        }
    }

    /**
     * Initialize user time preferences with default values
     * These will be updated by the user later
     */
    private suspend fun initUserTimePreferences(
        userIds: List<String>,
        userTimePreferenceRepository: UserTimePreferenceRepository
    ) {
        // Create default time preferences for each user
        for (userId in userIds) {
            val timePreference = UserTimePreferenceEntity(
                timePrefUserId = userId,
                biggestMealTime = null, // Will be set by user later
                sleepTime = null,       // Will be set by user later
                wakeUpTime = null       // Will be set by user later
            )
            userTimePreferenceRepository.insert(timePreference)
        }
    }

    /**
     * Initialize user scores from CSV data
     */
    private suspend fun initUserScores(
        context: Context,
        userScoreRepository: UserScoreRepository,
        scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository,
    ) {
        try {
            // Fetch UserID and Gender columns
            val userIdCol = readColumn(context, UserInfo.USERID.infoName, "testUsers.csv")
            val genderCol = readColumn(context, UserInfo.GENDER.infoName, "testUsers.csv") // UserInfo.GENDER.infoName is "Sex"

            if (userIdCol.isEmpty()) {
                // No users found or error in reading UserID column
                return
            }
            val userCount = userIdCol.size
            if (genderCol.size != userCount) {
                throw IllegalArgumentException("CSV column size mismatch for UserID and Gender during score initialization.")
            }

            // Pre-fetch all unique score columns
            val uniqueScoreColumnNames = mutableSetOf<String>()
            ScoreTypes.entries.forEach { scoreType ->
                uniqueScoreColumnNames.add(scoreType.getColumnName(Gender.MALE))
                uniqueScoreColumnNames.add(scoreType.getColumnName(Gender.FEMALE))
            }

            val scoreDataMap = mutableMapOf<String, List<String>>()
            uniqueScoreColumnNames.forEach { columnName ->
                try {
                    scoreDataMap[columnName] = readColumn(context, columnName, "testUsers.csv")
                    // Validate column size
                    if (scoreDataMap[columnName]?.size != userCount) {
                         // Log this or handle, but don't stop all initialization if one optional score column is problematic
                        System.err.println("Warning: Column '$columnName' size mismatch or missing. Expected $userCount rows.")
                        // Fill with empty strings to prevent crashes, default score will be 0f
                        scoreDataMap[columnName] = List(userCount) { "" }
                    }
                } catch (e: IllegalArgumentException) {
                    // Column not found, likely this score type is not in the CSV for any gender
                    System.err.println("Warning: Column '$columnName' not found in CSV. Users will get default 0f for this score where applicable.")
                    // Fill with empty strings to prevent crashes, default score will be 0f
                    scoreDataMap[columnName] = List(userCount) { "" }
                }
            }

            // Process each user
            for (i in 0 until userCount) {
                val userId = userIdCol[i]
                val genderString = genderCol[i]
                val gender = if (genderString.equals("Male", ignoreCase = true)) Gender.MALE else Gender.FEMALE

                // For each score type, get the value and insert a score entity
                ScoreTypes.entries.forEach { scoreType ->
                    val columnNameForScore = scoreType.getColumnName(gender)
                    val scoreValueString = scoreDataMap[columnNameForScore]?.getOrNull(i)
                    val scoreValue = scoreValueString?.toFloatOrNull() ?: 0f

                    val scoreDefinitionId = scoreTypeDefinitionRepository
                        .getScoreTypeKeyByName(scoreType.displayName)

                    val scoreEntity = UserScoreEntity(
                        scoreUserId = userId,
                        scoreTypeKey = scoreDefinitionId,
                        scoreValue = scoreValue
                    )
                    
                    userScoreRepository.insert(scoreEntity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Keep existing error handling for major issues
        }
    }

    private suspend fun initFoodCategoryDefinitionRepository(
        foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository
    ) {
        FoodCategory.entries.forEach { foodCategory ->
            val foodCategoryDefinition = FoodCategoryDefinitionEntity(
                foodDefId = foodCategory.foodDefId,
                foodCategoryName = foodCategory.foodName
            )
            foodCategoryDefinitionRepository.insert(foodCategoryDefinition)
        }
    }

    private suspend fun initScoreTypeDefinitionRepository(
        scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository
    ) {
        ScoreTypes.entries.forEach { scoreType ->
            val scoreTypeDefinition = ScoreTypeDefinitionEntity(
                scoreDefId = scoreType.scoreId,
                scoreTypeName = scoreType.displayName,
                scoreMaximum = scoreType.maxScore
            )
            scoreTypeDefinitionRepository.insert(scoreTypeDefinition)
        }
    }

    private suspend fun initPersonaRepository(
        personaRepository: PersonaRepository
    ) {
        Persona.entries.forEach { personaType ->
            val personaDefinition = PersonaEntity(
                personaID = personaType.personaId,
                personaName = personaType.personaName,
                personaDescription = personaType.personaDescription
            )
            personaRepository.insert(personaDefinition)
        }
    }
}


