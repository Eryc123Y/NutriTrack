package com.example.fit2081a1_yang_xingyu_33533563.di

import android.content.Context
import com.example.fit2081a1_yang_xingyu_33533563.data.model.AppDatabase
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ChatRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel.GenAIViewModel

/**
 * Dependency provider for the application.
 * Centralized location for creating and providing dependencies.
 */
object AppModule {
    
    /**
     * Provides the ChatRepository instance
     */
    fun provideChatRepository(context: Context): ChatRepository {
        val database = AppDatabase.getDatabase(context)
        return ChatRepository(database.chatMessageDao())
    }
    
    /**
     * Provides the GenAIViewModel instance
     */
    fun provideGenAIViewModel(context: Context): GenAIViewModel {
        return GenAIViewModel(provideChatRepository(context))
    }
} 