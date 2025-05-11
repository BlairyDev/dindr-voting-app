package com.grouptoo.dindr.ui

import android.R.attr.fontStyle
import android.R.attr.text
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.grouptoo.dindr.JoinOrCreate
import com.grouptoo.dindr.R
import com.grouptoo.dindr.ui.theme.DarkRed
import com.grouptoo.dindr.ui.theme.DeeperRed
import com.grouptoo.dindr.ui.theme.White
import com.grouptoo.dindr.viewmodel.AuthState
import com.grouptoo.dindr.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    backToLogin: () -> Unit,
) {
    val authState = authViewModel.authState.collectAsState()
    var username by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> backToLogin()
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Image(
            painter = painterResource(id = R.drawable.dindr_title),
            contentDescription = "Title of Dindr",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .height(180.dp)
        )


        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Welcome!",
                fontSize = 50.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            HorizontalDivider(thickness = 3.dp, modifier = Modifier.width(250.dp), color = DarkRed)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = {
                    Text("Enter Username")
                },
                modifier = modifier,

            )

            Spacer(modifier = Modifier.height(16.dp))

            Row() {
                Button(
                    onClick = {
                        if(username == "") {
                            Toast.makeText(context, "Enter a Username", Toast.LENGTH_SHORT).show()
                        } else {
                            navController.navigate(JoinOrCreate(username, "join"))
                        }
                    },

                ) {
                    Text(
                        text = "Join Session",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if(username == "") {
                            Toast.makeText(context, "Enter a Username", Toast.LENGTH_SHORT).show()
                        } else {
                            navController.navigate(JoinOrCreate(username, "create"))
                        }
                    },
                ) {
                    Text(
                        text = "Create Session",
                        color = Color.White
                    )
                }
            }

        }


        TextButton(
            onClick = {
                authViewModel.signOut()
            }
        ) {
            Text(
                text = "Sign out",
                color = White
            )
        }

    }
}

