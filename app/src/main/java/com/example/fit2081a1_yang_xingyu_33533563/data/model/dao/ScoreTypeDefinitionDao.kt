package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.ScoreTypeDefinitionEntity

@Dao
interface ScoreTypeDefinitionDao {
    @Insert
    fun insert(scoreType: ScoreTypeDefinitionEntity)

    @Update
    fun update(scoreType: ScoreTypeDefinitionEntity)

    @Delete
    fun delete(scoreType: ScoreTypeDefinitionEntity)

    @Query("SELECT * FROM score_type_definitions ORDER BY displayOrder ASC")
    fun getAllScoreTypes(): LiveData<List<ScoreTypeDefinitionEntity>>

    @Query("SELECT * FROM score_type_definitions WHERE scoreTypeKey = :scoreTypeKey")
    fun getScoreTypeByKey(scoreTypeKey: String): LiveData<ScoreTypeDefinitionEntity>
} 