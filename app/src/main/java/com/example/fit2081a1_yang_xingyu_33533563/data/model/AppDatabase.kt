package com.example.fit2081a1_yang_xingyu_33533563.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
    version = 1,
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
                    .fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}