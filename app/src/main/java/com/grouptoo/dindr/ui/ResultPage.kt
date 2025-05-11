package com.grouptoo.dindr.ui


import android.R.attr.onClick
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.grouptoo.dindr.model.RestaurantPlaces
import com.grouptoo.dindr.ui.common.composables.StarRatingBar
import com.grouptoo.dindr.ui.theme.DarkRed
import com.grouptoo.dindr.viewmodel.ResultPageViewModel
import kotlin.collections.filter

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    resultPageViewModel: ResultPageViewModel = hiltViewModel(),
    onHomeButtonClicked: () -> Unit,
    sessionId: String?
) {
    resultPageViewModel.getPlaces(sessionId)

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResultContent(
            resultPageViewModel = resultPageViewModel,
            sessionId = sessionId
        )

        Box() {
            Button(
                modifier = Modifier.padding(bottom = 16.dp),
                onClick = onHomeButtonClicked
            ) {
                Text("Back to Home")
            }

        }

    }

}

@Composable
fun ResultContent(
    modifier: Modifier = Modifier,
    resultPageViewModel: ResultPageViewModel,
    sessionId: String?
) {

    val places by resultPageViewModel.restaurantList.collectAsState()
    val random by resultPageViewModel.randomRestaurant.collectAsState()

    val maxVote = places.maxOfOrNull { it.votes } ?: 0
    val selectedRestaurants = places.filter { it.votes == maxVote}

    LaunchedEffect(Unit) {
        resultPageViewModel.getRandomRestaurant(sessionId.toString())
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        if(selectedRestaurants.count() > 1){

            val randomRestaurant = places.find { it.name == random?.name}
            val restaurants = places.sortedByDescending { it.votes }

            RestaurantWinner(
                restaurant = randomRestaurant,
                resultPageViewModel = resultPageViewModel
            )


            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                RunnerUpText()
                restaurants.forEach { restaurant ->

                    if(restaurant.name.toString() != randomRestaurant?.name) {
                        ListRestaurant(
                            restaurant = restaurant,
                            resultPageViewModel = resultPageViewModel
                        )
                    }

                }

            }


        } else if(selectedRestaurants.count() == 1) {
            val winnerRestaurant = selectedRestaurants[0]
            val restaurants = places.sortedByDescending { it.votes }

            RestaurantWinner(
                restaurant = winnerRestaurant,
                resultPageViewModel = resultPageViewModel
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                RunnerUpText()
                restaurants.forEach { restaurant ->

                    if(restaurant.name.toString() != winnerRestaurant.name) {
                        ListRestaurant(
                            restaurant = restaurant,
                            resultPageViewModel = resultPageViewModel
                        )
                    }

                }

            }


        }


    }


    Spacer(modifier = Modifier.height(20.dp))

}

@Composable
fun RestaurantWinner(
    modifier: Modifier = Modifier,
    restaurant: RestaurantPlaces?,
    resultPageViewModel: ResultPageViewModel
) {


    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(16.dp),

    ) {

        Box() {
            AsyncImage(
                modifier = Modifier
                    .height(500.dp),
                model = restaurant?.img,
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
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = restaurant?.name.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,
                        fontSize = 50.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = restaurant?.address.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraLight
                    )


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(
                            modifier = Modifier.padding(8.dp)
                        ) {

                            StarRatingBar(
                                rating = restaurant?.ratings,
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Text(
                                text = "(${restaurant?.ratings})",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )

                        }

                        Button(
                            modifier = Modifier.padding(end = 6.dp),
                            onClick = {
                                resultPageViewModel.viewRestaurantMap(
                                    restaurant?.name,
                                    restaurant?.address,
                                    restaurant?.latitude.toString(),
                                    restaurant?.longitude.toString(),
                                    context
                                )
                            }
                        ) {
                            Text("View Map")
                        }

                    }

                    Text(
                        text = "Votes: ${restaurant?.votes.toString()}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraLight
                    )



                }

            }


        }

    }
}

@Composable
fun ListRestaurant(
    modifier: Modifier = Modifier,
    restaurant: RestaurantPlaces,
    resultPageViewModel: ResultPageViewModel
) {

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = DarkRed,
        ),
        onClick = {
            resultPageViewModel.viewRestaurantMap(
                restaurant.name,
                restaurant.address,
                restaurant.latitude.toString(),
                restaurant.longitude.toString(),
                context
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = restaurant.name.toString(),
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            )

            Text(
                text = restaurant.votes.toString(),
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )

        }

    }

}

@Composable
fun RunnerUpText(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        ) {
        Text(
            text = "Runner up",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.ExtraLight
        )

        Text(
            text = "Votes",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.ExtraLight
        )

    }
}

