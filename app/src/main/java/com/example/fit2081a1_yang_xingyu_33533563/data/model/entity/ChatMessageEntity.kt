package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a single message in a chat conversation between the user and AI.
 */
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * The content of the message
     */
    val message: String,
    
    /**
     * Indicates if the message is from the user (true) or AI (false)
     */
    val isUserMessage: Boolean,
    
    /**
     * Optional reference to the user ID
     */
    val userId: Long? = null,
    
    /**
     * Timestamp when the message was created
     */
    val timestamp: Date = Date(),
    
    /**
     * Optional session ID to group related messages
     */
    val sessionId: String? = null
) 