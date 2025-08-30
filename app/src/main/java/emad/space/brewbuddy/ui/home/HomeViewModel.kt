package emad.space.brewbuddy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import emad.space.domain.handleState.ApiResult
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.PricedCoffeeItem
import emad.space.domain.usecases.GetBestSellerUseCase
import emad.space.domain.usecases.GetWeeklyRecommendationsUseCase
import emad.space.domain.usecases.GetPricedCoffeeListUseCase
import emad.space.domain.usecases.ObserveUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String? = null,
    val bestSeller: PricedCoffeeItem? = null,
    val recommendations: List<PricedCoffeeItem> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeUserName: ObserveUserNameUseCase,
    private val getPricedCoffeeList: GetPricedCoffeeListUseCase,
    private val getBestSeller: GetBestSellerUseCase,
    private val getWeeklyRecommendations: GetWeeklyRecommendationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigateToDetail = MutableSharedFlow<PricedCoffeeItem>(replay = 0, extraBufferCapacity = 1)
    val navigateToDetail: SharedFlow<PricedCoffeeItem> = _navigateToDetail.asSharedFlow()

    init {
        observeUserName().onEach { name ->
            _uiState.update { it.copy(userName = name) }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            combine(
                getPricedCoffeeList(CoffeeCategory.HOT),
                getPricedCoffeeList(CoffeeCategory.ICED)
            ) { hotResult, icedResult ->
                val hot = (hotResult as? ApiResult.Success)?.data ?: emptyList()
                val iced = (icedResult as? ApiResult.Success)?.data ?: emptyList()
                val all = hot + iced
                val error = (hotResult as? ApiResult.Failure)?.error?.message
                    ?: (icedResult as? ApiResult.Failure)?.error?.message
                Triple(all, hotResult is ApiResult.Success && icedResult is ApiResult.Success, error)
            }.collect { (all, loaded, error) ->
                val bestSeller = getBestSeller(all.map { it.item })?.let { best ->
                    all.firstOrNull { it.item.id == best.id }
                }
                val recommendations = getWeeklyRecommendations(all.map { it.item }, count = 10).mapNotNull { rec ->
                    all.firstOrNull { it.item.id == rec.id }
                }
                _uiState.value = _uiState.value.copy(
                    bestSeller = bestSeller,
                    recommendations = recommendations,
                    loading = !loaded,
                    error = error
                )
            }
        }
    }

    fun onBestSellerClicked() {
        _uiState.value.bestSeller?.let { _navigateToDetail.tryEmit(it) }
    }

    fun onRecommendationClicked(item: PricedCoffeeItem) {
        _navigateToDetail.tryEmit(item)
    }
}