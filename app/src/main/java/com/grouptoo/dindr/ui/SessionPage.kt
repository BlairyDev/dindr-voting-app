package com.grouptoo.dindr.ui

import android.util.Log
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
import androidx.compose.ui.Modifier
import com.grouptoo.dindr.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.grouptoo.dindr.DindrScreen
import com.grouptoo.dindr.R
import com.grouptoo.dindr.SessionSwipe
import com.grouptoo.dindr.model.Users
import com.grouptoo.dindr.ui.theme.DarkRed
import com.grouptoo.dindr.ui.theme.DeeperRed
import com.grouptoo.dindr.ui.theme.White
import com.grouptoo.dindr.viewmodel.SessionPageViewModel

@Composable
fun SessionScreen(
    sessionViewModel: SessionPageViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController,
    sessionId: String,
    modifier: Modifier = Modifier,
    onCancelledButton: () -> Unit
) {
    sessionViewModel.usersEventListener(sessionId)
    sessionViewModel.startEventListener(sessionId)


    val userList by sessionViewModel.userList.collectAsState()
    val startSwipeSession by sessionViewModel.startSwipeSession.collectAsState()

    SessionPage(
        modifier = Modifier.padding(16.dp),
        sessionId = sessionId,
        onUserListChange = userList,
        startSwipeSession = startSwipeSession,
        navController = navController,
        sessionViewModel = sessionViewModel,
        authViewModel = authViewModel,
        onCancelledButton = onCancelledButton,

    )

}

@Composable
fun SessionPage(
    modifier: Modifier = Modifier,
    sessionId: String,
    onUserListChange: Map<String, Users>,
    startSwipeSession: Boolean?,
    navController: NavController,
    sessionViewModel: SessionPageViewModel,
    authViewModel: AuthViewModel,
    onCancelledButton: () -> Unit
) {

    var hostName by remember { mutableStateOf("") }

    LaunchedEffect(onUserListChange) {
        if(sessionViewModel.checkSession(sessionId) == true) {
            val host = onUserListChange.values.find { it.type != "user" }
            hostName = host?.name.toString()
        } else {
            navController.navigate(DindrScreen.HomeScreen.name)
        }


    }

    LaunchedEffect(startSwipeSession) {
        if(startSwipeSession == true) {
            navController.navigate(SessionSwipe(sessionId))
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Image(
            modifier = Modifier
                .clip(CircleShape)
                .size(200.dp),
            painter = rememberDrawablePainter(
                drawable = getDrawable(
                    LocalContext.current,
                    R.drawable.plate_loading
                )
            ),
            contentDescription = "Loading animation",
            contentScale = ContentScale.FillWidth

        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Waiting for host...",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        Box() {
            Button(
                onClick = {
                    sessionViewModel.userLeaveSession(sessionId, authViewModel.getUserId())
                    onCancelledButton()
                }
            ) {
                Text("Leave Session")
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
                        text = "Host: ${hostName}",
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