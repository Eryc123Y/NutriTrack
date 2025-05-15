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
        val userIds = mutableListOf<String>()
        
        try {
            context.assets.open("testUsers.csv").use { inputStream ->
                val bufferedReader = BufferedReader(inputStream.reader())
                val lines = bufferedReader.readLines()
                val header = lines[0].split(",").map { it.trim() }
                
                // Find column indices
                val userIdIndex = header.indexOf(UserInfo.USERID.infoName)
                val phoneNumberIndex = header.indexOf(UserInfo.PHONE_NUMBER.infoName)
                val genderIndex = header.indexOf(UserInfo.GENDER.infoName)
                
                if (userIdIndex == -1 || phoneNumberIndex == -1 || genderIndex == -1) {
                    throw IllegalArgumentException("Required columns not found in CSV")
                }
                
                // Process each line (skip header)
                for (i in 1 until lines.size) {
                    val values = lines[i].split(",").map { it.trim() }
                    
                    val userId = values[userIdIndex]
                    val phoneNumber = values[phoneNumberIndex]
                    val gender = values[genderIndex]
                    
                    // Skip if we've already processed this user ID
                    if (userId in userIds) continue
                    
                    // Create and insert user entity
                    val userEntity = UserEntity(
                        userId = userId,
                        userName = null, // Default to null, update on registration
                        userPhoneNumber = phoneNumber,
                        userGender = gender,
                        userPersonaId = null, // Will be set by user later
                        userHashedCredential = null, // Will be set upon registration
                        userIsRegistered = false
                    )
                    
                    userRepository.insertUser(userEntity)
                    userIds.add(userId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return userIds
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
                    foodPrefCategoryKey = foodCategory.name, // Using enum name as key
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
            context.assets.open("testUsers.csv").use { inputStream ->
                val bufferedReader = BufferedReader(inputStream.reader())
                val lines = bufferedReader.readLines()
                val header = lines[0].split(",").map { it.trim() }
                
                // Find column indices
                val userIdIndex = header.indexOf(UserInfo.USERID.infoName)
                val genderIndex = header.indexOf(UserInfo.GENDER.infoName)
                
                if (userIdIndex == -1 || genderIndex == -1) {
                    throw IllegalArgumentException("Required columns not found in CSV")
                }
                
                // Process each line (skip header)
                for (i in 1 until lines.size) {
                    val values = lines[i].split(",").map { it.trim() }
                    val rowMap = header.zip(values).toMap()
                    
                    val userId = values[userIdIndex]
                    val gender = if (values[genderIndex].equals("Male", ignoreCase = true)) 
                        Gender.MALE else Gender.FEMALE
                    
                    // For each score type, get the value and insert a score entity
                    ScoreTypes.entries.forEach { scoreType ->
                        val columnName = scoreType.getColumnName(gender)
                        val scoreValue = rowMap[columnName]?.toFloatOrNull() ?: 0f

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
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun initFoodCategoryDefinitionRepository(
        foodCategoryDefinitionRepository: FoodCategoryDefinitionRepository
    ) {
        FoodCategory.entries.forEach { foodCategory ->
            val foodCategoryDefinition = FoodCategoryDefinitionEntity(
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
                personaName = personaType.personaName,
                personaDescription = personaType.personaDescription
            )
            personaRepository.insert(personaDefinition)
        }
    }
}


