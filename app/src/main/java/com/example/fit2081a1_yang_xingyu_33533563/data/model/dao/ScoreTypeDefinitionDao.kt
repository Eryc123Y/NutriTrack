package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.ScoreTypeDefinitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreTypeDefinitionDao {
    @Insert
    suspend fun insert(scoreType: ScoreTypeDefinitionEntity)

    @Update
    suspend fun update(scoreType: ScoreTypeDefinitionEntity)

    @Delete
    suspend fun delete(scoreType: ScoreTypeDefinitionEntity)

    @Query("SELECT * FROM score_type_definitions")
    fun getAllScoreTypes(): Flow<List<ScoreTypeDefinitionEntity>>

    @Query("SELECT * FROM score_type_definitions WHERE scoreDefId = :scoreTypeKey")
    fun getScoreTypeByKey(scoreTypeKey: String): Flow<ScoreTypeDefinitionEntity>

    @Query("SELECT scoreDefId FROM score_type_definitions WHERE scoreTypeName = :scoreTypeName")
    suspend fun getScoreTypeKeyByName(scoreTypeName: String): Int
} 