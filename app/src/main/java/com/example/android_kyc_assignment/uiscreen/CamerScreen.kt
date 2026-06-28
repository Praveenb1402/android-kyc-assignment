package com.example.android_kyc_assignment.uiscreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    customerId: Int,
    onPhotoTaken: () -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: CustomerDetailViewModel = hiltViewModel<CustomerDetailViewModel>()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isCapturing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Selfie") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── Camera Preview ───────────────────────────────────
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()

                        imageCapture = capture

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_FRONT_CAMERA,  // selfie = front camera
                                preview,
                                capture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // ── Capture Button ───────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isCapturing) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Button(
                        onClick = {
                            isCapturing = true
                            takePhoto(
                                context = context,
                                imageCapture = imageCapture,
                                customerId = customerId,
                                onSuccess = { filePath ->
                                    viewModel.markVerified(customerId, filePath)
                                    onPhotoTaken()
                                },
                                onError = {
                                    isCapturing = false
                                }
                            )
                        },
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(0.dp)
                    ) {}
                }
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    customerId: Int,
    onSuccess: (String) -> Unit,
    onError: () -> Unit
) {
    val capture = imageCapture ?: run { onError(); return }

    val photoFile = File(context.filesDir, "selfie_$customerId.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    capture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),  // CHANGE: use main executor instead of background thread
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onSuccess(photoFile.absolutePath)  // now runs on main thread ✅
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onError()
            }
        }
    )
}