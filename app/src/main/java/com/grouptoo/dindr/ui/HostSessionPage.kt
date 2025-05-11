package com.grouptoo.dindr.ui

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.grouptoo.dindr.DindrScreen
import com.grouptoo.dindr.R
import com.grouptoo.dindr.viewmodel.AuthViewModel
import com.grouptoo.dindr.SessionSwipe
import com.grouptoo.dindr.model.RestaurantPlaces
import com.grouptoo.dindr.model.Users
import com.grouptoo.dindr.ui.common.composables.CircleProgressBar
import com.grouptoo.dindr.ui.theme.DarkRed
import com.grouptoo.dindr.ui.theme.DeeperRed
import com.grouptoo.dindr.ui.theme.White
import com.grouptoo.dindr.viewmodel.HostSessionPageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@SuppressLint("MissingPermission")
@Composable
fun HostSessionScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    hostSessionViewModel: HostSessionPageViewModel = hiltViewModel(),
    username: String,
    navController: NavController,
) {


    val userList by hostSessionViewModel.userList.collectAsState()

    HostSessionPage(
        authViewModel = authViewModel,
        hostSessionViewModel = hostSessionViewModel,
        username = username,
        onUserListChange = userList,
        navController = navController,
        modifier = modifier
    )


}

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HostSessionPage(
    authViewModel: AuthViewModel,
    hostSessionViewModel: HostSessionPageViewModel,
    username: String,
    onUserListChange: Map<String, Users>,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var sessionId by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val permissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    LaunchedEffect(Unit) {
        if (!permissions.allPermissionsGranted) {
            permissions.launchMultiplePermissionRequest()
        }
    }
    
    LaunchedEffect(permissions.allPermissionsGranted) {

        if (permissions.allPermissionsGranted) {
            scope.launch(Dispatchers.IO) {
                val result = fusedLocationClient.getCurrentLocation(Priority.
                    PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token)
                    .await()
                    result?.let { fetchLocation ->

                        hostSessionViewModel.nearbyPlace(fetchLocation.longitude, fetchLocation.latitude)

                        hostSessionViewModel.restaurants.collect { event ->
                            when (event) {
                                HostSessionPageViewModel.RestaurantsState.Failure -> {
                                    Log.i("TAG", "errorHostsession")
                                }
                                HostSessionPageViewModel.RestaurantsState.Loading -> {
                                    sessionId == null
                                }
                                is HostSessionPageViewModel.RestaurantsState.Success -> {
                                    val createdSessionKey = hostSessionViewModel.createUserSession(
                                        authViewModel.getUserId(),
                                        username,
                                        event.restaurants
                                    )

                                    sessionId = createdSessionKey
                                    if (createdSessionKey != null) {
                                        Log.i("HostSession", "Session created: $createdSessionKey")
                                        hostSessionViewModel.usersEventListener(createdSessionKey)

                                    }

                                }
                            }

                        }


                    }

            }

        }


    }


    if (sessionId == null) {
        CircleProgressBar("Creating Session...")
        return
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                bitmap = hostSessionViewModel.getQrCodeBitmap(sessionId),
                contentDescription = "SessionId",
                Modifier.clip(RoundedCornerShape(16.dp))
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    hostSessionViewModel.startUserSession(sessionId)
                    navController.navigate(SessionSwipe(sessionId))
                },
                modifier = Modifier.width(150.dp)
            ) {
                Text("Start")
            }

            Button(
                onClick = {
                    hostSessionViewModel.endSession(sessionId!!)
                    navController.navigate(DindrScreen.HomeScreen.name)
                },
                modifier = Modifier.width(150.dp)
            ) {
                Text("End Session")
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp),
            ) {

                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Host: ${username}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))


                Text(
                    text = "Foodies:",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(5.dp))

                onUserListChange.forEach { (userId, user) ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkRed,
                        )
                    ) {
                        if(user.type == "user") {
                            Text(
                                text = user.name,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                }


            }


        }

    }


}





