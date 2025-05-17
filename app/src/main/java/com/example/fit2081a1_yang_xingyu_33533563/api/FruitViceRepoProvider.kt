package com.example.fit2081a1_yang_xingyu_33533563.api

/**
 * A simple object to provide an instance of FruitViceRepo.
 * This helps in centralizing the creation of the repository.
 */
object FruitViceRepoProvider {

    private var instance: FruitViceRepo? = null

    /**
     * Provides a singleton instance of FruitViceRepo.
     */
    fun getRepository(): FruitViceRepo {
        if (instance == null) {
            instance = FruitViceRepo()
        }
        return instance!!
    }
}