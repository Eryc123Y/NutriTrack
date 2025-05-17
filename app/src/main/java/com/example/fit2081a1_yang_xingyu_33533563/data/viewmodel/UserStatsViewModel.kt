package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.PersonaRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserTimePreferenceRepository

class UserStatsViewModel(
    private val userRepository: UserRepository,
    private val userScoreRepository: UserScoreRepository,
    private val userPersonaRepository: PersonaRepository,
    private val userTimePreferenceRepository: UserTimePreferenceRepository
) : ViewModel() {
}