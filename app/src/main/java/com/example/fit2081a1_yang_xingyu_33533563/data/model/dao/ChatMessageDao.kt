package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the ChatMessageEntity.
 * Provides methods to interact with the chat_messages table.
 */
@Dao
interface ChatMessageDao {
    /**
     * Insert a new chat message
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    /**
     * Insert multiple chat messages at once
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>): List<Long>

    /**
     * Update an existing message
     */
    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    /**
     * Delete a message
     */
    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)

    /**
     * Get all chat messages, ordered by timestamp (newest first)
     */
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    /**
     * Get all messages for a specific session
     */
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesBySession(sessionId: String): Flow<List<ChatMessageEntity>>

    /**
     * Get all messages for a specific user
     */
    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMessagesByUser(userId: String): Flow<List<ChatMessageEntity>>

    /**
     * Get a conversation as pairs of messages (user question and AI response)
     */
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getConversationFlow(): Flow<List<ChatMessageEntity>>

    /**
     * Get conversation for a specific user
     */
    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp ASC")
    fun getUserConversationFlow(userId: String): Flow<List<ChatMessageEntity>>

    /**
     * Delete all messages
     */
    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()

    /**
     * Delete messages from a specific session
     */
    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteSessionMessages(sessionId: String)
    
    /**
     * Delete all messages for a specific user
     */
    @Query("DELETE FROM chat_messages WHERE userId = :userId")
    suspend fun deleteUserMessages(userId: String)
}