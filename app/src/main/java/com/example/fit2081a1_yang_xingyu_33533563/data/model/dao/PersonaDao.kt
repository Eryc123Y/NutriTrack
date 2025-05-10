package com.example.fit2081a1_yang_xingyu_33533563.data.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity

@Dao
interface PersonaDao {
    @Insert
    fun insert(persona: PersonaEntity)

    @Update
    fun update(persona: PersonaEntity)

    @Delete
    fun delete(persona: PersonaEntity)

    @Query("SELECT * FROM personas")
    fun getAllPersonas(): LiveData<List<PersonaEntity>>

    @Query("SELECT * FROM personas WHERE personaId = :personaId")
    fun getPersonaById(personaId: String): LiveData<PersonaEntity>
} 