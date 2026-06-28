package com.example.android_kyc_assignment.uiscreen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerAccount(
    customerId: Int,
    onBackClick: () -> Unit = {},
    onOpenCamera: (Int) -> Unit = {},
    viewModel: CustomerDetailViewModel = hiltViewModel<CustomerDetailViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionDeniedPermanently by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onOpenCamera(customerId)
        } else {

            val activity = context as? android.app.Activity
            val showRationale = activity?.shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ) ?: false

            if (!showRationale) {
                permissionDeniedPermanently = true
            } else {
                showPermissionDialog = true
            }
        }
    }
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Camera Permission Needed") },
            text = { Text("Camera access is required to capture the KYC selfie. Please allow it.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Try Again") }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    if (permissionDeniedPermanently) {
        AlertDialog(
            onDismissRequest = { permissionDeniedPermanently = false },
            title = { Text("Permission Permanently Denied") },
            text = { Text("Camera permission was denied permanently. Please enable it from App Settings to complete KYC.") },
            confirmButton = {
                TextButton(onClick = {
                    permissionDeniedPermanently = false

                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        android.net.Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { permissionDeniedPermanently = false }) {
                    Text("Cancel")
                }
            }
        )
    }





    LaunchedEffect(customerId) {
        viewModel.loadCustomer(customerId)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        when (val state = uiState) {

            is DetailUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is DetailUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Color.Red)
                }
            }

            is DetailUiState.Success -> {
                val customer = state.customer

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = customer.image,
                            contentDescription = customer.name,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEAEAEA)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = customer.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "A/C  •••• ${customer.iban.takeLast(4)}",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(4.dp))


                            Surface(
                                color = if (customer.verified) Color(0xFFDFF6E4) else Color(
                                    0xFFFFE7C2
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = if (customer.verified) "KYC VERIFIED" else "KYC PENDING",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    color = if (customer.verified) Color(0xFF1E8C45) else Color(
                                        0xFFE58E00
                                    ),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }


                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${customer.balance.toLong()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "${customer.cardType} A/C",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Divider()
                    Spacer(Modifier.height(16.dp))
                    val bankBranch by viewModel.bankBranch.collectAsState()

                    DetailRow("Date of Birth", customer.birth)
                    DetailRow("Nationality", customer.nationality ?: "N/A")
                    DetailRow("Bank / Branch", bankBranch)
                    DetailRow("IFSC", customer.ifsc)

                    Spacer(Modifier.height(24.dp))

                    Text("KYC Selfie", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (customer.selfiePath != null) {
                            AsyncImage(
                                model = customer.selfiePath,
                                contentDescription = "Selfie",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color(0xFFDDE3EA), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(12.dp))


                    }

                    Spacer(Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = {
                            val hasPerm = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPerm) {
                                onOpenCamera(customerId)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (customer.verified) "Re-take Selfie" else "Do KYC",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
    Divider(color = Color(0xFFF0F0F0))
}