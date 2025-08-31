package emad.space.brewbuddy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import emad.space.domain.handleState.ApiResult
import emad.space.domain.models.CoffeeCategory
import emad.space.domain.models.PricedCoffeeItem
import emad.space.domain.repo.HomeCollectionsRepo
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
    private val getWeeklyRecommendations: GetWeeklyRecommendationsUseCase,
    private val homeRepo: HomeCollectionsRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigateToDetail = MutableSharedFlow<PricedCoffeeItem>(replay = 0, extraBufferCapacity = 1)
    val navigateToDetail: SharedFlow<PricedCoffeeItem> = _navigateToDetail.asSharedFlow()

    // Last known good lists (do NOT clear on later failures)
    private var lastHot: List<PricedCoffeeItem> = emptyList()
    private var lastIced: List<PricedCoffeeItem> = emptyList()

    // Exposed for downstream mapping
    private val _allPriced = MutableStateFlow<List<PricedCoffeeItem>>(emptyList())

    // guards to avoid re-seeding
    private var seededBest = false
    private var seededRecs = false

    // Have we received at least one emission from each stream?
    private var hotSeen = false
    private var icedSeen = false

    init {
        observeUserName().onEach { name ->
            _uiState.update { it.copy(userName = name) }
        }.launchIn(viewModelScope)

        // Collect HOT and ICED independently; keep last success, ignore failures for data
        viewModelScope.launch {
            getPricedCoffeeList(CoffeeCategory.HOT).collect { result ->
                hotSeen = true
                when (result) {
                    is ApiResult.Success -> {
                        lastHot = result.data
                        updateAllPriced()
                        _uiState.update { it.copy(loading = !(hotSeen && icedSeen), error = null) }
                    }
                    is ApiResult.Failure -> {
                        // Keep lastHot; just surface error if you want
                        _uiState.update { it.copy(loading = !(hotSeen && icedSeen), error = result.error.message) }
                    }
                }
            }
        }
        viewModelScope.launch {
            getPricedCoffeeList(CoffeeCategory.ICED).collect { result ->
                icedSeen = true
                when (result) {
                    is ApiResult.Success -> {
                        lastIced = result.data
                        updateAllPriced()
                        _uiState.update { it.copy(loading = !(hotSeen && icedSeen), error = null) }
                    }
                    is ApiResult.Failure -> {
                        _uiState.update { it.copy(loading = !(hotSeen && icedSeen), error = result.error.message) }
                    }
                }
            }
        }

        // BEST: seed only if no persisted row, otherwise map from persisted
        combine(_allPriced, homeRepo.observeBestSeller()) { all, bestItem ->
            if (bestItem == null) {
                if (all.isNotEmpty() && !seededBest) {
                    val computed = getBestSeller(all.map { it.item })
                    val matched = computed?.id?.let { id -> all.firstOrNull { it.item.id == id } }
                    if (matched != null) {
                        seededBest = true
                        viewModelScope.launch { homeRepo.setBestSeller(matched.item.id!!, matched.category) }
                        _uiState.update { it.copy(bestSeller = matched) }
                    } else {
                        _uiState.update { it.copy(bestSeller = null) }
                    }
                }
            } else {
                val matched = all.firstOrNull { it.item.id == bestItem.id }
                _uiState.update { it.copy(bestSeller = matched) }
            }
        }.launchIn(viewModelScope)

        // RECOMMENDATIONS: seed only if empty, otherwise map from persisted
        combine(_allPriced, homeRepo.observeRecommendations()) { all, recItems ->
            if (recItems.isEmpty()) {
                if (all.isNotEmpty() && !seededRecs) {
                    val computed = getWeeklyRecommendations(all.map { it.item }, count = 10)
                    val pairs = computed.mapNotNull { ci ->
                        val m = all.firstOrNull { it.item.id == ci.id }
                        if (ci.id != null && m != null) ci.id!! to m.category else null
                    }
                    if (pairs.isNotEmpty()) {
                        seededRecs = true
                        viewModelScope.launch { homeRepo.setRecommendations(pairs) }
                        val matched = pairs.mapNotNull { (id, _) -> all.firstOrNull { it.item.id == id } }
                        _uiState.update { it.copy(recommendations = matched) }
                    } else {
                        _uiState.update { it.copy(recommendations = emptyList()) }
                    }
                }
            } else {
                val matched = recItems.mapNotNull { rec ->
                    all.firstOrNull { it.item.id == rec.id }
                }
                _uiState.update { it.copy(recommendations = matched) }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateAllPriced() {
        _allPriced.value = lastHot + lastIced
    }

    fun onBestSellerClicked() {
        _uiState.value.bestSeller?.let { _navigateToDetail.tryEmit(it) }
    }

    fun onRecommendationClicked(item: PricedCoffeeItem) {
        _navigateToDetail.tryEmit(item)
    }
}