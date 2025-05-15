package com.example.fit2081a1_yang_xingyu_33533563.data.model.repository

import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.ScoreTypeDefinitionDao
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.ScoreTypeDefinitionEntity
import kotlinx.coroutines.flow.Flow

class ScoreTypeDefinitionRepository(private val scoreTypeDefinitionDao: ScoreTypeDefinitionDao) {

    suspend fun insert(scoreType: ScoreTypeDefinitionEntity) {
        scoreTypeDefinitionDao.insert(scoreType)
    }

    suspend fun update(scoreType: ScoreTypeDefinitionEntity) {
        scoreTypeDefinitionDao.update(scoreType)
    }

    suspend fun delete(scoreType: ScoreTypeDefinitionEntity) {
        scoreTypeDefinitionDao.delete(scoreType)
    }

    suspend fun getScoreTypeKeyByName(scoreTypeName: String): String {
        return scoreTypeDefinitionDao.getScoreTypeKeyByName(scoreTypeName)
    }

    fun getAllScoreTypes(): Flow<List<ScoreTypeDefinitionEntity>> {
        return scoreTypeDefinitionDao.getAllScoreTypes()
    }

    fun getScoreTypeByKey(scoreTypeKey: String): Flow<ScoreTypeDefinitionEntity> {
        return scoreTypeDefinitionDao.getScoreTypeByKey(scoreTypeKey)
    }


} 