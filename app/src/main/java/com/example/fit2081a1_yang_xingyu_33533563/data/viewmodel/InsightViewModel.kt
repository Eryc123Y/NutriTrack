package com.example.fit2081a1_yang_xingyu_33533563.data.viewmodel

    import androidx.lifecycle.*
    import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.ScoreTypeDefinitionRepository
    import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserRepository
    import com.example.fit2081a1_yang_xingyu_33533563.data.model.repository.UserScoreRepository
    import kotlinx.coroutines.flow.firstOrNull
    import kotlinx.coroutines.launch

    class InsightsViewModel(
        private val userScoreRepository: UserScoreRepository,
        private val scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository,
        private val userRepository: UserRepository
    ) : ViewModel() {

        // A helper data class for displaying scores
        data class DisplayableScore(
            // Key for matching with UserScoreEntity
            val scoreTypeKey: String,
            val displayName: String,
            val scoreValue: Float,
            val maxScore: Float,
            val description: String?
        )

        private val _userId = MutableLiveData<String?>()
        val userId: LiveData<String?> = _userId

        private val _displayableScores = MutableLiveData<List<DisplayableScore>>(emptyList())
        val displayableScores: LiveData<List<DisplayableScore>> = _displayableScores

        private val _isLoading = MutableLiveData<Boolean>(false)
        val isLoading: LiveData<Boolean> = _isLoading

        fun setUserId(userId: String?) {
            _userId.value = userId
            userId?.let { loadScoresForUser(it) }
        }

        private fun loadScoresForUser(userId: String) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val userScores = userScoreRepository
                        .getScoresByUserId(userId).firstOrNull() ?: emptyList()
                    val scoreDefinitions = scoreTypeDefinitionRepository
                        .getAllScoreTypes().firstOrNull() ?: emptyList()

                    val displayScores = userScores.mapNotNull { userScore ->
                        scoreDefinitions.find { it.scoreTypeKey == userScore.scoreTypeKey }
                            ?.let { definition ->
                                DisplayableScore(
                                    scoreTypeKey = userScore.scoreTypeKey,
                                    displayName = definition.displayName,
                                    scoreValue = userScore.scoreValue,
                                    maxScore = definition.maxScore,
                                    description = definition.description
                                )
                            }
                    }
                    _displayableScores.postValue(displayScores)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _displayableScores.postValue(emptyList())
                } finally {
                    _isLoading.value = false
                }
            }
        }

        // Function to manually trigger score refresh
        fun refreshScores(userId: String) {
            loadScoresForUser(userId)
        }

        class InsightsViewModelFactory(
            private val userScoreRepository: UserScoreRepository,
            private val scoreTypeDefinitionRepository: ScoreTypeDefinitionRepository,
            private val userRepository: UserRepository
        )
            : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(InsightsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return InsightsViewModel(userScoreRepository, scoreTypeDefinitionRepository,
                        userRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }