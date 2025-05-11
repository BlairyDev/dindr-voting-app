package com.grouptoo.dindr.data.repository

import android.R.attr.data
import android.system.Os.remove
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.grouptoo.dindr.model.RestaurantPlaces
import com.grouptoo.dindr.model.Sessions
import com.grouptoo.dindr.model.Users
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class DataRepositoryReal @Inject constructor(
    private val firebaseDb: DatabaseReference
) : DataRepository {

    private val _randomRestaurant = MutableStateFlow<RestaurantPlaces?>(RestaurantPlaces())
    override val randomRestaurant: StateFlow<RestaurantPlaces?> = _randomRestaurant

    override suspend fun getRandomRestaurant(sessionId: String?) {
        val data = firebaseDb.child("sessions/${sessionId}/random").get().await()
        _randomRestaurant.value = data.getValue(RestaurantPlaces::class.java)
    }



    private val _restaurantList = MutableStateFlow<List<RestaurantPlaces>>(emptyList())
    override val restaurantList: StateFlow<List<RestaurantPlaces>> = _restaurantList

    override fun getPlaces(sessionId: String?) {
        firebaseDb.child("sessions/${sessionId}/restaurant").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<RestaurantPlaces>()
                for (child in snapshot.children) {
                    val item = child.getValue(RestaurantPlaces::class.java)
                    item?.let { list.add(it) }
                }
                _restaurantList.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())
            }
        })

    }

    override fun voteRestaurant(sessionId: String?, position: Int) {
        firebaseDb.child("sessions/${sessionId}/restaurant/${position}").runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(RestaurantPlaces::class.java) ?: return Transaction.success(mutableData)

                p.votes += 1
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (databaseError != null) {
                    Log.e("TAG", "Transaction failed", databaseError.toException())
                } else {
                    Log.d("TAG", "Transaction completed successfully")

                }
            }
        })

    }

    override fun userVoted(sessionId: String?, userId: String?) {
        firebaseDb.child("sessions/${sessionId}/roles/$userId/voted").setValue(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Firebase", "Value updated successfully")
                } else {
                    Log.e("Firebase", "Error updating value", task.exception)
                }
            }
    }


    private val _votingFinished = MutableStateFlow<Boolean?>(false)
    override val votingFinished: StateFlow<Boolean?> = _votingFinished

    override fun checkVoteFinished(sessionId: String?) {
        firebaseDb.child("sessions/${sessionId}/roles").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(userSnapshot in snapshot.children) {
                    val voted = userSnapshot.child("voted").getValue(Boolean::class.java) ?: false
                    if(!voted) {
                        _votingFinished.value = false
                        break
                    }
                    _votingFinished.value = true
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "checkVotedFinished", error.toException())
            }
        })
    }

    override fun resetVoteFinished() {
        _votingFinished.value = false
    }

    override fun createSession(
        userId: String,
        username: String,
        restaurants: List<RestaurantPlaces>
    ): String? {

        val key = firebaseDb.child("sessions").push().key;

        val userData = hashMapOf(
            "name" to username,
            "type" to "host",
            "voted" to false
        )

        val userRoles: HashMap<String, Any> = hashMapOf(userId to userData);


        val session = Sessions(userRoles, restaurants);
        val sessionValues = session.toMap();

        val childUpdates = hashMapOf<String, Any>(
            "/sessions/$key" to sessionValues
        );

        firebaseDb.updateChildren(childUpdates);
        firebaseDb.child("sessions/$key").onDisconnect().removeValue()

        return key
    }

    override fun endSession(sessionId: String) {
        firebaseDb.child("sessions/${sessionId}").removeValue()
    }

    override suspend fun checkSession(sessionId: String): Boolean{
        val id = firebaseDb.child("sessions/${sessionId}").get().await().exists()
        return id
    }

    override fun addUserToSession(sessionId: String, userId: String, username: String) {

        val userData = hashMapOf(
            "name" to username,
            "type" to "user",
            "voted" to false
        )

        val userRole: HashMap<String, Any> = hashMapOf(userId to userData)

        val rolesUpdate = hashMapOf<String, Any>(
            "sessions/$sessionId/roles" to userRole
        );

        firebaseDb.child("sessions/$sessionId/roles").updateChildren(userRole)
        firebaseDb.child("sessions/$sessionId/roles/$userId").onDisconnect().removeValue()

    }

    override fun userLeaveSession(sessionId: String, userId: String) {
        firebaseDb.child("sessions/$sessionId/roles/$userId").removeValue()
    }


    private val _usersList = MutableStateFlow<MutableMap<String, Users>>(mutableMapOf())
    override val userList: StateFlow<MutableMap<String, Users>> = _usersList

    override fun usersEventListener(sessionId: String?) {
        firebaseDb.child("sessions/${sessionId}/roles").addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val user = snapshot.getValue(Users::class.java)

                if(user != null){
                    val newUserList = _usersList.value?.toMutableMap() ?: mutableMapOf()
                    newUserList[snapshot.key!!] = user
                    _usersList.value = newUserList
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("TAG", snapshot.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val newUserList = _usersList.value?.toMutableMap() ?: mutableMapOf()
                newUserList.remove(snapshot.key)
                _usersList.value = newUserList
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }


    private val _startSwipeSession = MutableStateFlow<Boolean?>(false)
    override val startSwipeSession: StateFlow<Boolean?> = _startSwipeSession

    override fun startEventListener(sessionId: String) {
        firebaseDb.child("sessions/${sessionId}/start").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val start = snapshot.getValue<Boolean?>()
                _startSwipeSession.value = start
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())
            }
        })

    }

    override fun startSession(sessionId: String?) {
        firebaseDb.child("sessions/${sessionId}/start").setValue(true)
    }
}