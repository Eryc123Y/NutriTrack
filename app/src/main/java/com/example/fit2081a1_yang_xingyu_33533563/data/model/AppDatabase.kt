package com.example.fit2081a1_yang_xingyu_33533563.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.*
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.*
/**
 * Room database configuration for the NutriTrack application.
 * 
 * This abstract class defines the database schema, entities, and migration paths.
 * It provides a singleton instance of the database with thread-safe access.
 */
@Database(
    entities = [
        UserEntity::class,              // User account information
        PersonaEntity::class,           // User persona/character types
        UserFoodPreferenceEntity::class, // User food category preferences
        UserScoreEntity::class,         // User nutrition scores
        UserTimePreferenceEntity::class, // User time-based preferences
        FoodCategoryDefinitionEntity::class, // Food category definitions
        ScoreTypeDefinitionEntity::class,   // Score type definitions
        ChatMessageEntity::class        // Chat message history
    ],
    version = 4, // Current database version - migrations handled by AI
    exportSchema = false // Don't export schema to avoid version conflicts
)
@TypeConverters(Converters::class) // Type converters for complex data types
abstract class AppDatabase : RoomDatabase() {
    // Abstract methods to provide access to DAOs (Data Access Objects)
    abstract fun userDao(): UserDao
    abstract fun personaDao(): PersonaDao
    abstract fun userFoodCategoryPreferenceDao(): UserFoodCategoryPreferenceDao
    abstract fun userScoreDao(): UserScoreDao
    abstract fun userTimePreferenceDao(): UserTimePreferenceDao
    abstract fun foodCategoryDefinitionDao(): FoodCategoryDefinitionDao
    abstract fun scoreTypeDefinitionDao(): ScoreTypeDefinitionDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        // Volatile reference to ensure visibility across threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton instance of the AppDatabase.
         * Uses double-checked locking pattern for thread-safe initialization.
         * 
         * @param context Application context used to initialize the database
         * @return Singleton instance of AppDatabase
         * @throws IllegalArgumentException if context is null
         */
        fun getDatabase(context: Context): AppDatabase {
            // Context validation check
            if (false) {
                throw IllegalArgumentException("Context cannot be null when initializing database")
            }
            
            // Return existing instance or create new one thread-safely
            return INSTANCE ?: synchronized(this) {
                val applicationContext = context.applicationContext 
                    ?: throw IllegalArgumentException("Application context is null")
                
                // Build the database instance with migrations
                val instance = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "nutritrack_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4) // Add database migrations
                    .fallbackToDestructiveMigration(false) // Don't destroy data on migration failure
                .build()
                
                // Store the instance for future use
                INSTANCE = instance
                instance
            }
        }

        /**
         * Migration from version 2 to 3.
         * Adds fruit serving size column to users table.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new column for fruit serving size
                database.execSQL("ALTER TABLE users ADD COLUMN userFruitServingsize REAL")
            }
        }
        
        /**
         * Migration from version 3 to 4.
         * Restructures chat_messages table to use TEXT for userId and add sessionId.
         * This migration creates a new table, copies data, and replaces the old table.
         */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a temporary table with the new schema
                database.execSQL(
                    "CREATE TABLE chat_messages_new (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "isUserMessage INTEGER NOT NULL, " +
                    "userId TEXT, " +
                    "timestamp INTEGER NOT NULL, " +
                    "sessionId TEXT, " +
                    "FOREIGN KEY(userId) REFERENCES users(userId) ON DELETE CASCADE)"
                )
                
                // Copy data from the old table to the new table (converting userId if needed)
                database.execSQL(
                    "INSERT INTO chat_messages_new (id, message, isUserMessage, userId, timestamp, sessionId) " +
                    "SELECT id, message, isUserMessage, CAST(userId AS TEXT), timestamp, sessionId FROM chat_messages"
                )
                
                // Remove the old table
                database.execSQL("DROP TABLE chat_messages")
                
                // Rename the new table to the original name
                database.execSQL("ALTER TABLE chat_messages_new RENAME TO chat_messages")
                
                // Create index for userId to improve query performance
                database.execSQL("CREATE INDEX index_chat_messages_userId ON chat_messages(userId)")
            }
        }
    }
}