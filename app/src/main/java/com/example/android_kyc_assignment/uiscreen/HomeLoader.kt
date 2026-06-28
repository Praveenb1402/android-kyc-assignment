package com.example.android_kyc_assignment.uiscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.android_kyc_assignment.LocalDatabase.toEntity
import com.example.android_kyc_assignment.LocalDatabase.toModel
import com.example.android_kyc_assignment.Apicall.fetchUsers
import com.example.android_kyc_assignment.data.CustomerDatamodel
import com.example.android_kyc_assignment.data.local.CustomerDatastore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

sealed class HomeUiState {
    object Loading : HomeUiState()

    data class Success(val customers: List<CustomerDatamodel>, val source: String) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val customerDao: CustomerDatastore
) : ViewModel() {

    companion object {

        private const val CACHE_EXPIRY_MS = 60 * 60 * 1000L
    }

    private val ifscPool = listOf(
        "HDFC0CAGSBK", "SBIN0000001", "ICIC0000001",
        "PUNB0244200", "UTIB0000001"
    )

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {

        if (_uiState.value is HomeUiState.Loading) {
            loadUsers()
        }
    }

    fun loadUsers(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                getCachedOrFresh(forceRefresh)
            } catch (e: Exception) {
                val stale = customerDao.getAllCustomers()
                if (stale.isNotEmpty()) {
                    _uiState.value = HomeUiState.Success(
                        customers = stale.map { it.toModel() },
                        source = "cache"
                    )
                } else {
                    _uiState.value = HomeUiState.Error(e.message ?: "Something went wrong")
                }
            }
        }
    }

    private suspend fun getCachedOrFresh(forceRefresh: Boolean) {
        val cached = customerDao.getAllCustomers()
        val oldestFetch = customerDao.getOldestFetchTime() ?: 0L
        val isCacheExpired = System.currentTimeMillis() - oldestFetch > CACHE_EXPIRY_MS


        if (cached.isNotEmpty() && !forceRefresh) {
            if (!isCacheExpired) {
                Log.d("Cache", "✅ Loading from ROOM")
                _uiState.value = HomeUiState.Success(
                    customers = cached.map { it.toModel() },
                    source = "cache"
                )
                return
            }
        }


        if (cached.isEmpty() || forceRefresh) {
            Log.d("Cache", "🌐 Loading from NETWORK")
            fetchFromNetwork()
            return
        }


        Log.d("Cache", "⏰ Cache expired, refreshing from NETWORK")
        _uiState.value = HomeUiState.Success(
            customers = cached.map { it.toModel() },
            source = "cache"
        )
        fetchFromNetwork()
    }

    private suspend fun fetchFromNetwork() {
        val response = fetchUsers(limit = 20)
        val rng = Random(seed = 42)


        val existingCustomers = customerDao.getAllCustomers()
        val verifiedMap = existingCustomers.associate { it.id to it }

        val customers = response.users.map { user ->
            val existing = verifiedMap[user.id]

            CustomerDatamodel(
                id = user.id,
                name = "${user.firstName} ${user.lastName}",
                image = user.image,
                iban = user.bank.iban,
                cardNumber = user.bank.cardNumber,
                cardType = user.bank.cardType,
                currency = user.bank.currency,
                accountNumber = user.bank.iban,
                balance = (rng.nextInt(138001) + 2000).toDouble(),
                ifsc = ifscPool[user.id % ifscPool.size],
                birth = user.birthDate,
                nationality = user.nationality ?: "N/A",
                verified = existing?.verified ?: false,
                selfiePath = existing?.selfiePath,
            )
        }

        customerDao.clearAll()
        customerDao.insertAll(customers.map { it.toEntity() })

        _uiState.value = HomeUiState.Success(
            customers = customers,
            source = "network"
        )
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    fun refreshFromRoom() {
        viewModelScope.launch {
            val cached = customerDao.getAllCustomers()
            if (cached.isNotEmpty()) {
                _uiState.value = HomeUiState.Success(
                    customers = cached.map { it.toModel() },
                    source = "cache"
                )
            }
        }
    }
}