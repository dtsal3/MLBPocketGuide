package edu.utap.mlbpocketguide.ui.favorites

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import edu.utap.mlbpocketguide.api.PlayerInfo
import edu.utap.mlbpocketguide.api.PlayerRepository
import edu.utap.mlbpocketguide.model.FavoriteMeta
import kotlinx.coroutines.launch

class FavoritesViewModel: ViewModel() {
    private var playerNames = MutableLiveData<List<String>>()
    private var players = MutableLiveData<List<PlayerInfo>>()
    private var playerRepository = PlayerRepository()
    private var playerMetaList = MutableLiveData<List<FavoriteMeta>>()
    private var authUser = Firebase.auth.currentUser
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "favoritePlayers"
    var addStatusFailure = MutableLiveData<Boolean?>()
    var removeStatusFailure = MutableLiveData<Boolean?>()



    // but we want to launch in a state that is initialized to the AWW subreddit
    init {
        Log.d("Tracing","We are in the init of the viewModel")
        viewModelScope.launch {
            // If signed in, get the list from firestore
            if (authUser != null) {
                db.collection(rootCollection)
                    .whereEqualTo("ownerUid", authUser!!.uid)
                    .get()
                    .addOnSuccessListener { result ->
                        Log.d(javaClass.simpleName, "favoritePlayers fetch ${result!!.documents.size}")
                        // Could refactor favorites adapter to not need all 3 of these...
                        val playerMetasToPost = mutableListOf<FavoriteMeta>()
                        val playerNamesToPost = mutableListOf<String>()
                        val playersToPost = mutableListOf<PlayerInfo>()
                        result.documents.forEach{
                            val currentPlayerMeta = it.toObject(FavoriteMeta::class.java)
                            playerMetasToPost.add(currentPlayerMeta!!)
                            playerNamesToPost.add(currentPlayerMeta.playerName)
                            val playerToAddToPlayers = playerRepository.fetchData().filter {
                                val fullName = it.firstName + " " + it.lastName
                                fullName == currentPlayerMeta.playerName
                            }
                            playersToPost.add(playerToAddToPlayers[0])
                        }
                        playerNames.postValue(playerNamesToPost)
                        playerMetaList.postValue(playerMetasToPost)
                        players.postValue(playersToPost)
                    }
                    .addOnFailureListener {
                        Log.d(javaClass.simpleName, "favoritePlayers fetch FAILED ", it)
                    }
            } else {
                // The guests always start with Mookie Betts and Brayan Bello
                playerNames = MutableLiveData(mutableListOf("Mookie Betts", "Brayan Bello"))
                val mookieBetts = playerRepository.fetchData().filter {
                    (it.firstName == "Mookie" && it.lastName == "Betts")
                            ||
                    (it.firstName == "Brayan" && it.lastName == "Bello")
                }
                players = MutableLiveData(mookieBetts)
            }
            Log.d("Tracing","And after adding mookie, our players length is %s".format(players.value?.size))
        }
    }

    fun observeLivingFavorites(): LiveData<List<PlayerInfo>> {
        Log.d("FavoritesViewModel","In Observe Living Favorites!")
        Log.d("FavoritesViewModel", "We are finding this for favorites: %s".format(players.value.toString()))
        return players
    }

    fun isFavorite(name: String): Boolean {
        return playerNames.value?.contains(name) == true
    }

    // Ideally we'd add based on ID but the ListView serving users only has their name...
    fun addFavorite(name: String) {
        val namesList = playerNames.value!!.toMutableList()
        namesList.add(name)
        Log.d("AddFavorite","The new player names are %s".format(namesList.toString()))
        val newPlayerInfo = playerRepository.fetchData().filter {
            val combinedName = it.firstName + " " + it.lastName
            combinedName == name
        }
        Log.d("AddFavorite","The new player is %s".format(newPlayerInfo.toString()))
        val newPlayers = players.value!! + newPlayerInfo

        // Add to firestore too and only post new values on success
        // We check that the player selected is not already a favorite before adding so we should be protected from dupes
        if (authUser?.uid != null) {
            val favoriteMeta = FavoriteMeta(
                ownerUid = authUser!!.uid,
                playerName = name
            )
            db.collection(rootCollection)
                .add(favoriteMeta)
                .addOnSuccessListener {
                    Log.d("AddFavorite", "We are posting this to our players object: %s".format(newPlayers.toString()))
                    players.postValue(newPlayers)
                    playerNames.postValue(namesList)
                }
                .addOnFailureListener {
                    addStatusFailure.value = true
                }
        } else {
            // Unless we are using as a guest, and then skip the firestore part
            players.postValue(newPlayers)
            playerNames.postValue(namesList)
        }

    }

    fun removeFavorite(name: String) {
        val namesList = playerNames.value!!.toMutableList()
        namesList.remove(name)
        val selectedPlayerInfo = playerRepository.fetchData().filter {
            val combinedName = it.firstName + " " + it.lastName
            combinedName == name
        }
        val newPlayers = players.value!!.toMutableList()
        newPlayers.remove(selectedPlayerInfo[0])

        val favoriteMetaToRemove = playerMetaList.value!!.filter {
            it.playerName == name
        }

        // If signed in, we need to remove from firestore too
        if (authUser?.uid != null) {
            db.collection(rootCollection)
                .document(favoriteMetaToRemove[0].firestoreID)
                .delete()
                .addOnSuccessListener {
                    players.postValue(newPlayers)
                    playerNames.postValue(namesList)
                }
                .addOnFailureListener { e ->
                    removeStatusFailure.value = true
                }
        } else {
            // otherwise, just skip and remove
            players.postValue(newPlayers)
            playerNames.postValue(namesList)
        }
    }

    fun fetchFavorites(): List<String> {
        val favoritesNum = playerNames.value?.size ?: 0
        if (favoritesNum > 0) {
            return playerNames.value!!
        }
        return listOf<String>()
    }

}

