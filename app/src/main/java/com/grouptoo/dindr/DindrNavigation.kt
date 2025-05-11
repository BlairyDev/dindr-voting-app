package com.grouptoo.dindr

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.grouptoo.dindr.ui.HomePage
import com.grouptoo.dindr.ui.HostSessionScreen
import com.grouptoo.dindr.ui.LoginPage
import com.grouptoo.dindr.ui.ResultScreen
import com.grouptoo.dindr.ui.ScanPage
import com.grouptoo.dindr.ui.SessionScreen
import com.grouptoo.dindr.ui.SessionSwipeScreen
import com.grouptoo.dindr.ui.SignupPage
import kotlinx.serialization.Serializable


enum class DindrScreen() {
    Login,
    SignUp,
    HomeScreen,
}

@Serializable
data class JoinSession(val sessionId: String)

@Serializable
data class SessionSwipe(val sessionId: String?)

@Serializable
data class JoinOrCreate(val username: String, val action: String)

@Serializable
data class ResultRestaurant(val sessionId: String?)




@Composable
fun DindrNavigation(
    modifier: Modifier = Modifier,
) {

    val navController = rememberNavController()

    Scaffold() { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DindrScreen.Login.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = DindrScreen.Login.name) {
                BackHandler(true) {
                    Log.i("LOG_TAG", "Clicked back")
                }
                LoginPage(
                    onNextButtonClicked = {
                        navController.navigate(DindrScreen.HomeScreen.name)
                    },
                    onSignButtonClicked = {
                        navController.navigate(DindrScreen.SignUp.name)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(innerPadding)
                )
            }
            composable(route = DindrScreen.HomeScreen.name) {
                BackHandler(true) {
                    Log.i("LOG_TAG", "Clicked back")
                }
                HomePage(
                    navController = navController
                    ,
                    backToLogin = {
                        navController.navigate(DindrScreen.Login.name)
                    },

                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable(route = DindrScreen.SignUp.name) {
                SignupPage(
                    onNextButtonClicked = {
                        navController.navigate(DindrScreen.HomeScreen.name)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }

            composable<JoinOrCreate>{
                val args = it.toRoute<JoinOrCreate>()

                if(args.action == "join"){
                    ScanPage(
                        navController = navController,
                        username = args.username
                    )

                } else if(args.action == "create") {
                    BackHandler(true) {
                        Log.i("LOG_TAG", "Clicked back")
                    }
                    HostSessionScreen(
                        navController = navController,
                        username = args.username
                    )
                }
            }

            composable<JoinSession>{
                BackHandler(true) {
                    Log.i("LOG_TAG", "Clicked back")
                }
                val args = it.toRoute<JoinSession>()
                SessionScreen(
                    sessionId = args.sessionId,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    navController = navController,
                    onCancelledButton = {
                        navController.navigate(DindrScreen.HomeScreen.name)
                    }
                )
            }

            composable<SessionSwipe>{
                BackHandler(true) {
                    Log.i("LOG_TAG", "Clicked back")
                }
                val args = it.toRoute<SessionSwipe>()
                SessionSwipeScreen(
                    sessionId = args.sessionId,
                    navController = navController

                )

            }

            composable<ResultRestaurant> {
                BackHandler(true) {
                    Log.i("LOG_TAG", "Clicked back")
                }
                val args = it.toRoute<ResultRestaurant>()
                ResultScreen(
                    sessionId = args.sessionId,
                    onHomeButtonClicked = {
                        navController.navigate(DindrScreen.HomeScreen.name)
                    }
                )
            }
        }
    }

}