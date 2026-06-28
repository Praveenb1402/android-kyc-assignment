package com.example.android_kyc_assignment.uiscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_kyc_assignment.LocalDatabase.toModel
import com.example.android_kyc_assignment.data.CustomerDatamodel
import com.example.android_kyc_assignment.data.local.CustomerDatastore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.android_kyc_assignment.Apicall.resolveIfsc
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val customer: CustomerDatamodel) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    private val customerDao: CustomerDatastore
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _bankBranch = MutableStateFlow("Resolving...")
    val bankBranch: StateFlow<String> = _bankBranch.asStateFlow()



    private suspend fun fetchBankDetails(ifsc: String) {
        try {
            val result = resolveIfsc(ifsc)
            _bankBranch.value = "${result.bank}, ${result.branch}"
        } catch (e: Exception) {
            _bankBranch.value = "Unable to resolve"
        }
    }
    fun loadCustomer(customerId: Int) {
        viewModelScope.launch {
            try {
                val entity = customerDao.getCustomerById(customerId)
                if (entity != null) {
                    val customer = entity.toModel()
                    _uiState.value = DetailUiState.Success(customer)
                    fetchBankDetails(customer.ifsc)
                } else {
                    _uiState.value = DetailUiState.Error("Customer not found")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }



    fun markVerified(customerId: Int, selfiePath: String) {
        viewModelScope.launch {
            customerDao.markVerified(customerId, selfiePath)
            loadCustomer(customerId)
        }
    }
}