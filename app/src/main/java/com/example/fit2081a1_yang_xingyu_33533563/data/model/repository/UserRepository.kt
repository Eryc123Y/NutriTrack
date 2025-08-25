package com.example.fit2081a1_yang_xingyu_33533563.data.model.repository

import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.UserDao
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository class for managing user data operations.
 * 
 * This class provides a clean API for accessing user data from the database
 * through the UserDao. It abstracts the data source and provides methods
 * for all user-related operations including CRUD operations and specific queries.
 * 
 * @param userDao Data Access Object for user table operations
 */
class UserRepository(private val userDao: UserDao) {

    /**
     * Retrieves all users from the database as a Flow.
     * The Flow will emit new values whenever the user data changes.
     * 
     * @return Flow of List<UserEntity> containing all users
     */
    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    /**
     * Retrieves a specific user by their ID.
     * Returns null if the user is not found.
     * 
     * @param userId The unique identifier of the user to retrieve
     * @return Flow of UserEntity? (nullable) containing the user or null
     */
    fun getUserById(userId: String): Flow<UserEntity?> {
        return userDao.getUserById(userId)
    }

    /**
     * Inserts a new user into the database.
     * This is a suspend function and should be called from a coroutine.
     * 
     * @param user The UserEntity to insert
     */
    suspend fun insertUser(user: UserEntity) {
        userDao.insert(user)
    }

    /**
     * Updates an existing user in the database.
     * This is a suspend function and should be called from a coroutine.
     * 
     * @param user The UserEntity with updated information
     */
    suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
    }

    /**
     * Deletes a user from the database.
     * This is a suspend function and should be called from a coroutine.
     * 
     * @param user The UserEntity to delete
     */
    suspend fun deleteUser(user: UserEntity) {
        userDao.delete(user)
    }

    /**
     * Retrieves the persona ID associated with a specific user.
     * Used to determine the user's character type for personalization.
     * 
     * @param userId The unique identifier of the user
     * @return String representing the persona ID
     */
    suspend fun getUserPersonaId(userId: String): String {
        return userDao.getUserPersonaId(userId)
    }

    /**
     * Retrieves the hashed password/credential for a user.
     * Used for authentication purposes during login.
     * 
     * @param userId The unique identifier of the user
     * @return String? containing the hashed credential or null if not found
     */
    suspend fun getHashedCredentialByUserId(userId: String): String? {
        return userDao.getHashedCredentialByUserId(userId)
    }

    /**
     * Checks if a user has completed registration.
     * Used to determine if a user needs to complete the registration process.
     * 
     * @param userId The unique identifier of the user
     * @return Boolean? indicating registration status (null if user not found)
     */
    suspend fun getUserIsRegistered(userId: String): Boolean? {
        return userDao.getUserIsRegistered(userId)
    }

    /**
     * Retrieves the gender of a specific user.
     * Used for personalization and demographic analysis.
     * 
     * @param userId The unique identifier of the user
     * @return String? containing the gender or null if not set
     */
    suspend fun getUserGender(userId: String): String? {
        return userDao.getUserGender(userId)
    }

    /**
     * Retrieves the phone number of a specific user.
     * Used for contact and verification purposes.
     * 
     * @param userId The unique identifier of the user
     * @return String containing the phone number
     */
    suspend fun getUserPhoneNumber(userId: String): String {
        return userDao.getUserPhoneNumber(userId)
    }

    /**
     * Marks a user as registered in the system.
     * Called after successful registration completion.
     * 
     * @param userId The unique identifier of the user to mark as registered
     */
    suspend fun updateUserIsRegistered(userId: String) {
        userDao.updateUserIsRegistered(userId)
    }

    /**
     * Updates the hashed credential (password) for a user.
     * Used during password changes and initial registration.
     * 
     * @param userId The unique identifier of the user
     * @param hashedCredential The new hashed password to store
     */
    suspend fun updateUserHashedCredential(userId: String, hashedCredential: String) {
        userDao.updateUserHashedCredential(userId, hashedCredential)
    }
} 