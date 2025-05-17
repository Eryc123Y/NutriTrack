package com.example.fit2081a1_yang_xingyu_33533563.data.model.repository

import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.ChatMessageEntity
import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.ChatMessageDao
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

/**
 * Repository for managing chat messages between user and AI.
 * Provides an abstraction layer over the data sources.
 */
class ChatRepository(private val chatMessageDao: ChatMessageDao) {

    /**
     * Get all chat messages ordered by timestamp (newest first)
     */
    fun getAllMessages(): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getAllMessages()
    }

    /**
     * Get messages for a specific conversation session
     */
    fun getSessionMessages(sessionId: String): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getMessagesBySession(sessionId)
    }

    /**
     * Get messages for a specific user
     */
    fun getMessagesByUser(userId: Long): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getMessagesByUser(userId)
    }

    /**
     * Get the full conversation as a chronological flow of messages
     */
    fun getConversationFlow(): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getConversationFlow()
    }
    
    /**
     * Get conversation for a specific user
     */
    fun getUserConversationFlow(userId: Long): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getUserConversationFlow(userId)
    }

    /**
     * Save a user message to the database
     */
    suspend fun saveUserMessage(
        message: String,
        userId: Long? = null,
        sessionId: String = UUID.randomUUID().toString()
    ): Long {
        val chatMessage = ChatMessageEntity(
            message = message,
            isUserMessage = true,
            userId = userId,
            timestamp = Date(),
            sessionId = sessionId
        )
        return chatMessageDao.insertMessage(chatMessage)
    }

    /**
     * Save an AI response to the database
     */
    suspend fun saveAiResponse(
        response: String,
        userId: Long? = null,
        sessionId: String? = null
    ): Long {
        val chatMessage = ChatMessageEntity(
            message = response,
            isUserMessage = false,
            userId = userId,
            timestamp = Date(),
            sessionId = sessionId
        )
        return chatMessageDao.insertMessage(chatMessage)
    }

    /**
     * Save a complete conversation turn (user message + AI response)
     */
    suspend fun saveConversationTurn(
        userMessage: String,
        aiResponse: String,
        userId: Long? = null,
        sessionId: String = UUID.randomUUID().toString()
    ): List<Long> {
        val messages = listOf(
            ChatMessageEntity(
                message = userMessage,
                isUserMessage = true,
                userId = userId,
                timestamp = Date(),
                sessionId = sessionId
            ),
            ChatMessageEntity(
                message = aiResponse,
                isUserMessage = false,
                userId = userId,
                timestamp = Date(System.currentTimeMillis() + 1), // Ensure correct order
                sessionId = sessionId
            )
        )
        return chatMessageDao.insertMessages(messages)
    }

    /**
     * Delete a specific message
     */
    suspend fun deleteMessage(message: ChatMessageEntity) {
        chatMessageDao.deleteMessage(message)
    }

    /**
     * Delete all messages in the database
     */
    suspend fun clearAllMessages() {
        chatMessageDao.deleteAllMessages()
    }

    /**
     * Delete all messages from a specific session
     */
    suspend fun clearSessionMessages(sessionId: String) {
        chatMessageDao.deleteSessionMessages(sessionId)
    }
    
    /**
     * Delete all messages for a specific user
     */
    suspend fun clearUserMessages(userId: Long) {
        chatMessageDao.deleteUserMessages(userId)
    }
}