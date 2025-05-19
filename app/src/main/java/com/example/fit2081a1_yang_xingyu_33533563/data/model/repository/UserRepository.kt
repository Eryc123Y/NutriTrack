package com.example.fit2081a1_yang_xingyu_33533563.data.model.repository

import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.UserDao
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    fun getUserById(userId: String): Flow<UserEntity?> {
        return userDao.getUserById(userId)
    }

    suspend fun insertUser(user: UserEntity) {
        userDao.insert(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
    }

    suspend fun deleteUser(user: UserEntity) {
        userDao.delete(user)
    }

    suspend fun getUserPersonaId(userId: String): String {
        return userDao.getUserPersonaId(userId)
    }

    suspend fun getHashedCredentialByUserId(userId: String): String? {
        return userDao.getHashedCredentialByUserId(userId)
    }

    suspend fun getUserIsRegistered(userId: String): Boolean? {
        return userDao.getUserIsRegistered(userId)
    }

    suspend fun getUserGender(userId: String): String? {
        return userDao.getUserGender(userId)
    }

    suspend fun getUserPhoneNumber(userId: String): String {
        return userDao.getUserPhoneNumber(userId)
    }

    suspend fun updateUserIsRegistered(userId: String) {
        userDao.updateUserIsRegistered(userId)
    }

    suspend fun updateUserHashedCredential(userId: String, hashedCredential: String) {
        userDao.updateUserHashedCredential(userId, hashedCredential)
    }


} 