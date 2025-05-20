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
    version = 4, // The DB migration is done by AI
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun personaDao(): PersonaDao
    abstract fun userFoodCategoryPreferenceDao(): UserFoodCategoryPreferenceDao
    abstract fun userScoreDao(): UserScoreDao
    abstract fun userTimePreferenceDao(): UserTimePreferenceDao
    abstract fun foodCategoryDefinitionDao(): FoodCategoryDefinitionDao
    abstract fun scoreTypeDefinitionDao(): ScoreTypeDefinitionDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (false) {
                throw IllegalArgumentException("Context cannot be null when initializing database")
            }
            
            return INSTANCE ?: synchronized(this) {
                val applicationContext = context.applicationContext 
                    ?: throw IllegalArgumentException("Application context is null")
                
                val instance = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "nutritrack_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN userFruitServingsize REAL")
            }
        }
        
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
                
                // Create index for userId
                database.execSQL("CREATE INDEX index_chat_messages_userId ON chat_messages(userId)")
            }
        }
    }
}