package com.grouptoo.dindr.ui

import android.Manifest
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.grouptoo.dindr.JoinSession
import com.grouptoo.dindr.viewmodel.AuthViewModel
import com.grouptoo.dindr.viewmodel.ScanPageViewModel
import kotlinx.coroutines.delay


@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanPage(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    scanPageViewModel: ScanPageViewModel = hiltViewModel(),
    username: String,
    modifier: Modifier = Modifier,
) {
    var barcode by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    var boundingRect by remember { mutableStateOf<Rect?>(null) }
    var qrCodeDetected by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            PreviewView(ctx).apply {

                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_CODABAR,
                        Barcode.FORMAT_CODE_93,
                        Barcode.FORMAT_CODE_39,
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_AZTEC
                    )
                    .build()

                val barcodeScanner = BarcodeScanning.getClient(options)


                cameraController.setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(ctx),
                    MlKitAnalyzer(
                        listOf(barcodeScanner),
                        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                        ContextCompat.getMainExecutor(ctx)
                    ) { result: MlKitAnalyzer.Result? ->
                        val barcodeResults = result?.getValue(barcodeScanner)
                        if (!barcodeResults.isNullOrEmpty()) {

                            barcode = barcodeResults.first().rawValue


                            qrCodeDetected = true


                            boundingRect = barcodeResults.first().boundingBox

                            Log.d("Looking for Barcode ", barcodeResults.first().boundingBox.toString())
                        }
                    }
                )


                cameraController.bindToLifecycle(lifecycleOwner)


                this.controller = cameraController
            }
        }
    )

    if (qrCodeDetected) {
        LaunchedEffect(Unit) {

            delay(100)

            if(scanPageViewModel.checkSession(barcode.toString()) != false) {
                scanPageViewModel.addUserToSession(
                    barcode.toString(),
                    authViewModel.getUserId(),
                    username
                )

                navController.navigate(JoinSession(barcode.toString()))

            }


            Log.i("TAG", "$barcode barcode")

        }

    }


}


