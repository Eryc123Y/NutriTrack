package com.example.fit2081a1_yang_xingyu_33533563.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a single message in a chat conversation between the user and AI.
 */
@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Or SET_NULL, depending on desired behavior
        )
    ],
    indices = [Index(value = ["userId"])]
)
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
    val userId: String? = null,
    
    /**
     * Timestamp when the message was created
     */
    val timestamp: Date = Date(),
    
    /**
     * Optional session ID to group related messages
     */
    val sessionId: String? = null
) 