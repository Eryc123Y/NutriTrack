# NutriTrack Database Documentation

## Overview

The NutriTrack application uses Room database for local data persistence. The database follows a normalized schema design with proper foreign key relationships and indexes for optimal performance.

## Database Configuration

### Database Class
```kotlin
@Database(
    entities = [
        UserEntity::class,
        PersonaEntity::class,
        UserFoodPreferenceEntity::class,
        UserScoreEntity::class,
        UserTimePreferenceEntity::class,
        FoodCategoryDefinitionEntity::class,
        ScoreTypeDefinitionEntity::class,
        ChatMessageEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase()
```

### Migration Strategy
The database includes migration support from version 2 to 4:
- **Migration 2→3**: Added `userFruitServingsize` column to users table
- **Migration 3→4**: Modified chat_messages table to change userId from INTEGER to TEXT

## Entities

### UserEntity
**Table**: `users`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| userId | String | PRIMARY KEY | Unique user identifier |
| userName | String? | | User display name |
| userPhoneNumber | String | NOT NULL | User phone number |
| userGender | String? | | User gender |
| userFruitServingsize | Float? | | Preferred fruit serving size |
| userPersonaId | String? | FOREIGN KEY | Reference to PersonaEntity |
| userHashedCredential | String? | | BCrypt hashed password |
| userIsRegistered | Boolean | DEFAULT false | Registration status |

**Relationships**:
- Foreign key to PersonaEntity (userPersonaId → personaID)
- One-to-many with UserFoodPreferenceEntity
- One-to-many with UserScoreEntity
- One-to-many with UserTimePreferenceEntity
- One-to-many with ChatMessageEntity

### PersonaEntity
**Table**: `personas`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| personaID | String | PRIMARY KEY | Unique persona identifier |
| personaName | String | NOT NULL | Persona display name |
| personaDescription | String? | | Persona description |

### UserFoodPreferenceEntity
**Table**: `user_food_preferences`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| userId | String | PRIMARY KEY, FOREIGN KEY | User identifier |
| foodCategoryId | String | PRIMARY KEY, FOREIGN KEY | Food category identifier |
| preferenceLevel | Int | NOT NULL | Preference level (1-5) |
| lastUpdated | Long | NOT NULL | Timestamp of last update |

**Composite Primary Key**: (userId, foodCategoryId)

### UserScoreEntity
**Table**: `user_scores`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | Long | PRIMARY KEY AUTOINCREMENT | Unique score record ID |
| userId | String | FOREIGN KEY, NOT NULL | User identifier |
| scoreTypeId | String | FOREIGN KEY, NOT NULL | Score type identifier |
| userScore | Float | NOT NULL | Score value |
| scoreTypeName | String | NOT NULL | Score type display name |
| timestamp | Long | NOT NULL | Score timestamp |

### UserTimePreferenceEntity
**Table**: `user_time_preferences`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| userId | String | PRIMARY KEY, FOREIGN KEY | User identifier |
| mealTime | String | PRIMARY KEY | Meal time (breakfast, lunch, dinner) |
| preferredTime | String | NOT NULL | Preferred time in HH:mm format |
| isEnabled | Boolean | DEFAULT true | Whether preference is active |
| lastUpdated | Long | NOT NULL | Timestamp of last update |

**Composite Primary Key**: (userId, mealTime)

### ChatMessageEntity
**Table**: `chat_messages`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | Long | PRIMARY KEY AUTOINCREMENT | Unique message ID |
| message | String | NOT NULL | Message content |
| isUserMessage | Boolean | NOT NULL | True if from user, false if from AI |
| userId | String? | FOREIGN KEY | User identifier |
| timestamp | Long | NOT NULL | Message timestamp |
| sessionId | String? | | Chat session identifier |

### FoodCategoryDefinitionEntity
**Table**: `food_category_definitions`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| foodCategoryId | String | PRIMARY KEY | Unique category identifier |
| categoryName | String | NOT NULL | Category display name |
| categoryDescription | String? | | Category description |

### ScoreTypeDefinitionEntity
**Table**: `score_type_definitions`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| scoreTypeId | String | PRIMARY KEY | Unique score type identifier |
| scoreTypeName | String | NOT NULL | Score type display name |
| scoreTypeDescription | String? | | Score type description |

## Data Access Objects (DAOs)

### UserDao
```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE userIsRegistered = 1")
    suspend fun getAllRegisteredUsers(): List<UserEntity>
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)
    
    @Query("SELECT * FROM users WHERE userPhoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByPhoneNumber(phoneNumber: String): UserEntity?
    
    @Query("UPDATE users SET userIsRegistered = :isRegistered WHERE userId = :userId")
    suspend fun updateUserRegistrationStatus(userId: String, isRegistered: Boolean)
}
```

### UserScoreDao
```kotlin
@Dao
interface UserScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserScore(userScore: UserScoreEntity)
    
    @Query("SELECT * FROM user_scores WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getUserScores(userId: String): List<UserScoreEntity>
    
    @Query("SELECT * FROM user_scores WHERE userId = :userId AND scoreTypeId = :scoreTypeId ORDER BY timestamp DESC")
    suspend fun getUserScoresByType(userId: String, scoreTypeId: String): List<UserScoreEntity>
    
    @Query("SELECT * FROM user_scores WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestUserScore(userId: String): UserScoreEntity?
}
```

### ChatMessageDao
```kotlin
@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insertChatMessage(chatMessage: ChatMessageEntity): Long
    
    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp ASC")
    fun getChatHistory(userId: String): Flow<List<ChatMessageEntity>>
    
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getChatMessagesBySession(sessionId: String): List<ChatMessageEntity>
    
    @Query("DELETE FROM chat_messages WHERE userId = :userId")
    suspend fun deleteChatHistoryForUser(userId: String)
    
    @Query("SELECT COUNT(*) FROM chat_messages WHERE userId = :userId")
    suspend fun getChatMessageCount(userId: String): Int
    
    @Query("DELETE FROM chat_messages WHERE id IN (SELECT id FROM chat_messages WHERE userId = :userId ORDER BY timestamp DESC LIMIT -1 OFFSET :keepCount)")
    suspend fun limitChatHistory(userId: String, keepCount: Int)
}
```

### UserFoodCategoryPreferenceDao
```kotlin
@Dao
interface UserFoodCategoryPreferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(preference: UserFoodPreferenceEntity)
    
    @Query("SELECT * FROM user_food_preferences WHERE userId = :userId")
    suspend fun getUserPreferences(userId: String): List<UserFoodPreferenceEntity>
    
    @Query("UPDATE user_food_preferences SET preferenceLevel = :level, lastUpdated = :timestamp WHERE userId = :userId AND foodCategoryId = :categoryId")
    suspend fun updatePreferenceLevel(userId: String, categoryId: String, level: Int, timestamp: Long)
}
```

### UserTimePreferenceDao
```kotlin
@Dao
interface UserTimePreferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimePreference(preference: UserTimePreferenceEntity)
    
    @Query("SELECT * FROM user_time_preferences WHERE userId = :userId")
    suspend fun getUserTimePreferences(userId: String): List<UserTimePreferenceEntity>
    
    @Query("SELECT * FROM user_time_preferences WHERE userId = :userId AND mealTime = :mealTime")
    suspend fun getTimePreferenceForMeal(userId: String, mealTime: String): UserTimePreferenceEntity?
    
    @Update
    suspend fun updateTimePreference(preference: UserTimePreferenceEntity)
}
```

## Repository Pattern

Each entity has a corresponding repository that encapsulates data access logic:

### UserRepository
```kotlin
class UserRepository(private val userDao: UserDao) {
    suspend fun getUserById(userId: String): UserEntity? = userDao.getUserById(userId)
    
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    
    suspend fun getAllRegisteredUsers(): List<UserEntity> = userDao.getAllRegisteredUsers()
    
    suspend fun authenticateUser(userId: String, password: String): Boolean {
        val user = userDao.getUserById(userId)
        return user?.let { 
            EncryptionUtils.verifyPassword(password, it.userHashedCredential ?: "") 
        } ?: false
    }
    
    suspend fun registerUser(userId: String, password: String): Boolean {
        val existingUser = userDao.getUserById(userId)
        return if (existingUser != null && !existingUser.userIsRegistered) {
            val hashedPassword = EncryptionUtils.hashPassword(password)
            val updatedUser = existingUser.copy(
                userHashedCredential = hashedPassword,
                userIsRegistered = true
            )
            userDao.updateUser(updatedUser)
            true
        } else {
            false
        }
    }
}
```

## Database Initialization

### Initial Data Loading
The `InitDataUtils` class handles initial database population:

```kotlin
object InitDataUtils {
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
        // Load CSV data and populate database
        loadUsersFromCsv(context, userRepository)
        loadFoodCategoriesFromCsv(context, foodCategoryDefinitionRepository)
        loadScoreTypesFromCsv(context, scoreTypeDefinitionRepository)
        loadPersonasFromCsv(context, personaRepository)
        // ... other initialization methods
    }
}
```

## Type Converters

Custom type converters for complex data types:

```kotlin
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }
}
```

## Indexes and Performance

### Indexes
The database uses several indexes for optimal query performance:

```kotlin
// User entity indexes
@Entity(indices = [Index(value = ["userPersonaId"])])

// Chat message indexes  
@Entity(indices = [Index(value = ["userId"]), Index(value = ["sessionId"])])

// User score indexes
@Entity(indices = [Index(value = ["userId"]), Index(value = ["scoreTypeId"]), Index(value = ["timestamp"])])
```

### Query Optimization
- Use of compound indexes for frequently queried column combinations
- Proper foreign key constraints with cascade operations
- Efficient pagination for large datasets
- Background thread execution for all database operations

## Usage Examples

### Creating a New User with Preferences
```kotlin
suspend fun createUserWithPreferences(
    userId: String,
    userName: String,
    phoneNumber: String,
    preferences: List<FoodPreference>
) {
    // Insert user
    val user = UserEntity(
        userId = userId,
        userName = userName,
        userPhoneNumber = phoneNumber,
        userIsRegistered = false
    )
    userRepository.insertUser(user)
    
    // Insert preferences
    preferences.forEach { pref ->
        val preferenceEntity = UserFoodPreferenceEntity(
            userId = userId,
            foodCategoryId = pref.categoryId,
            preferenceLevel = pref.level,
            lastUpdated = System.currentTimeMillis()
        )
        userFoodCategoryPreferenceRepository.insertPreference(preferenceEntity)
    }
}
```

### Retrieving User Dashboard Data
```kotlin
suspend fun getUserDashboardData(userId: String): UserDashboardData {
    val user = userRepository.getUserById(userId)
    val latestScores = userScoreRepository.getLatestUserScore(userId)
    val preferences = userFoodCategoryPreferenceRepository.getUserPreferences(userId)
    val timePreferences = userTimePreferenceRepository.getUserTimePreferences(userId)
    
    return UserDashboardData(
        user = user,
        scores = latestScores,
        foodPreferences = preferences,
        timePreferences = timePreferences
    )
}
```

This database documentation provides a comprehensive overview of the data layer architecture, including entity relationships, DAO methods, and best practices for database operations in the NutriTrack application.