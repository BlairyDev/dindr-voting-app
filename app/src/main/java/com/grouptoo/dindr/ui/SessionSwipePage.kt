package com.grouptoo.dindr.ui

import android.R.attr.onClick
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.grouptoo.dindr.DindrScreen
import com.grouptoo.dindr.ResultRestaurant
import com.grouptoo.dindr.model.RestaurantPlaces
import com.grouptoo.dindr.ui.common.composables.CircleProgressBar
import com.grouptoo.dindr.ui.common.composables.StarRatingBar
import com.grouptoo.dindr.viewmodel.AuthViewModel
import com.grouptoo.dindr.viewmodel.SessionSwipePageViewModel
import com.spartapps.swipeablecards.state.rememberSwipeableCardsState
import com.spartapps.swipeablecards.ui.SwipeableCardDirection
import com.spartapps.swipeablecards.ui.lazy.LazySwipeableCards
import com.spartapps.swipeablecards.ui.lazy.items
import kotlinx.coroutines.delay


@Composable
fun SessionSwipeScreen(
    modifier: Modifier = Modifier,
    sessionSwipePageViewModel: SessionSwipePageViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    sessionId: String?,
    navController: NavController

) {

    sessionSwipePageViewModel.getPlaces(sessionId)
    val places by sessionSwipePageViewModel.restaurantList.collectAsState()

    if(places == emptyList<RestaurantPlaces>()) {
        return
    }



    SessionSwipePage(
        restaurants = places,
        sessionSwipePageViewModel = sessionSwipePageViewModel,
        authViewModel = authViewModel,
        navController = navController,
        sessionId = sessionId
    )
}

@Composable
fun SessionSwipePage(
    modifier: Modifier = Modifier,
    restaurants: List<RestaurantPlaces>,
    sessionSwipePageViewModel: SessionSwipePageViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    sessionId: String?
) {
    val state = rememberSwipeableCardsState(itemCount = {restaurants.size})

    val voteFinished by sessionSwipePageViewModel.votingFinished.collectAsState()

    var index by remember { mutableIntStateOf(0) }

    val onUserListChange by sessionSwipePageViewModel.userList.collectAsState()

    val context = LocalContext.current


    LaunchedEffect(voteFinished) {
        if(voteFinished == true) {
            sessionSwipePageViewModel.resetVoteFinished()
            navController.navigate(ResultRestaurant(sessionId))
        }
    }

    LaunchedEffect(onUserListChange) {
        if(sessionSwipePageViewModel.checkSession(sessionId.toString()) == false) {
            navController.navigate(DindrScreen.HomeScreen.name)
        }
    }

    var animateBackgroundColor by remember {
        mutableStateOf("transparent")
    }

    val animatedColor by animateColorAsState(
        if (animateBackgroundColor == "green") {
            Color(0x1A00FF00)
        } else if(animateBackgroundColor == "red") {
            Color(0x2DFF0000)
        } else {
            Color.Transparent
        },
        label = "color"
    )

    LaunchedEffect(index) {
        delay(500)
        animateBackgroundColor = "transparent"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(animatedColor)
            }
    ) {
        CircleProgressBar("Waiting for others...")
        LazySwipeableCards(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onSwipe = { restaurant, direction ->
                when (direction) {
                    SwipeableCardDirection.Right -> {
                        sessionSwipePageViewModel.voteRestaurant(sessionId, index)
                        animateBackgroundColor = "green"
                    }
                    SwipeableCardDirection.Left -> {
                        animateBackgroundColor = "red"
                    }
                }
                index++

                if(index == 5) {

                    sessionSwipePageViewModel.userVoted(sessionId, authViewModel.getUserId())
                    index = 0
                }

                sessionSwipePageViewModel.checkVoteFinished(sessionId)
            }
        ) {

            items(restaurants) { restaurant, index, offset ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(700.dp)
                        .padding(16.dp),
                ) {

                    Box() {
                        AsyncImage(
                            modifier = Modifier
                                .height(700.dp),
                            model = restaurant.img,
                            contentDescription = "image of a restaurant",
                            contentScale = ContentScale.Crop,
                        )


                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = restaurant.name.toString(),
                                    style = MaterialTheme.typography.headlineLarge,
                                    modifier = Modifier.padding(8.dp),
                                    color = Color.White,
                                    fontSize = 50.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = restaurant.address.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(8.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraLight
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {


                                    Row(
                                        modifier = Modifier
                                            .padding(8.dp)
                                    ) {
                                        StarRatingBar(
                                            rating = restaurant.ratings,
                                        )

                                        Spacer(modifier = Modifier.width(2.dp))

                                        Text(
                                            text = "(${restaurant.ratings})",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                        )

                                    }

                                    Button(
                                        modifier = Modifier.padding(6.dp),
                                        onClick = {
                                            sessionSwipePageViewModel.viewRestaurantMap(
                                                restaurant.name,
                                                restaurant.address,
                                                restaurant.latitude.toString(),
                                                restaurant.longitude.toString(),
                                                context
                                            )
                                        }
                                    ) {
                                        Text("View Map")
                                    }


                                }

                            }

                        }

                    }


                }
            }
        }

    }

}

