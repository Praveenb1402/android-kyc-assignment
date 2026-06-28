package com.example.android_kyc_assignment.uiscreen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android_kyc_assignment.data.CustomerCard
import com.example.android_kyc_assignment.data.CustomerDatamodel

@Composable
fun HomeScreen(
    onCustomerClick: (Int) -> Unit = {},
    onKycClick: (Int) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    var selectedTab by remember { mutableIntStateOf(1) }
    val isLoading = uiState is HomeUiState.Loading
    val pullState = rememberPullToRefreshState()

    Scaffold(
        bottomBar = {
            TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
                Tab(
                    selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    text = { Text("VERIFIED") })
                Tab(
                    selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    text = { Text("PENDING") })
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.loadUsers(forceRefresh = true) },
            state = pullState,
            modifier = Modifier.fillMaxSize()
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(text = "Digital Bank", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (selectedTab == 0) "Verified KYC" else "Pending KYC",
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search name or account number") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(20.dp))

                when (val state = uiState) {

                    is HomeUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is HomeUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Error: ${state.message}", color = Color.Red)
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { viewModel.loadUsers() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }

                    is HomeUiState.Success -> {
                        val context = LocalContext.current
                        LaunchedEffect(state.source) {
                            Toast.makeText(
                                context,
                                if (state.source == "cache") "Loaded from local cache" else "Loaded from network",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val filtered = state.customers
                            .filter { it.verified == (selectedTab == 0) }
                            .filter { customer ->
                                query.isBlank() ||
                                        customer.name.contains(query, ignoreCase = true) ||
                                        customer.iban.contains(query)
                            }

                        if (filtered.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No customers found", color = Color.Gray)
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filtered, key = { it.id }) { customer ->
                                    CustomerCard(
                                        customer = customer,
                                        onClick = { onCustomerClick(customer.id) },
                                        onKycClick = { onKycClick(customer.id) }
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}