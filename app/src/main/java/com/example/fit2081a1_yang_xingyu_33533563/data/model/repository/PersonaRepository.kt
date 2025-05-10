package com.example.fit2081a1_yang_xingyu_33533563.data.model.repository

import com.example.fit2081a1_yang_xingyu_33533563.data.model.dao.PersonaDao
import com.example.fit2081a1_yang_xingyu_33533563.data.model.entity.PersonaEntity
import kotlinx.coroutines.flow.Flow

class PersonaRepository(private val personaDao: PersonaDao) {

    suspend fun insert(persona: PersonaEntity) {
        personaDao.insert(persona)
    }

    suspend fun update(persona: PersonaEntity) {
        personaDao.update(persona)
    }

    suspend fun delete(persona: PersonaEntity) {
        personaDao.delete(persona)
    }

    fun getAllPersonas(): Flow<List<PersonaEntity>> {
        return personaDao.getAllPersonas()
    }

    fun getPersonaById(personaId: String): Flow<PersonaEntity> {
        return personaDao.getPersonaById(personaId)
    }
} 