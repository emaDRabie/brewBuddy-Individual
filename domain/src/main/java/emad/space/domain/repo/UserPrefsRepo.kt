package emad.space.domain.repo

import kotlinx.coroutines.flow.Flow

interface UserPrefsRepo {
    suspend fun saveUserName(name: String)
    suspend fun getUserName(): String?
    fun observeUserName(): Flow<String?>
}