package emad.space.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import emad.space.domain.repo.UserPrefsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPrefsRepoImpl(
    private val context: Context
) : UserPrefsRepo {

    private val KEY_USER_NAME = stringPreferencesKey("user_name")

    override suspend fun saveUserName(name: String) {
        context.dataStore.edit { it[KEY_USER_NAME] = name }
    }

    override suspend fun getUserName(): String? {
        return context.dataStore.data.map { it[KEY_USER_NAME] }.firstOrNull()
    }

    override fun observeUserName(): Flow<String?> {
        return context.dataStore.data.map { it[KEY_USER_NAME] }
    }
}