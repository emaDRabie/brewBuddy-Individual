package emad.space.domain.usecases

import emad.space.domain.repo.UserPrefsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserNameUseCase @Inject constructor(
    private val repo: UserPrefsRepo
) {
    operator fun invoke(): Flow<String?> = repo.observeUserName()
}