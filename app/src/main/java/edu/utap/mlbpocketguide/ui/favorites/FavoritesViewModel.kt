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
    private var playerRepository = PlayerRepository()
    // Generally speaking, having to maintain these three data objects is a bad idea, lots of opportunity to be out of sync
    // Ideally, I would refactor to have a handler that we call to keep all three tog
    private var playerNames = MutableLiveData<List<String>>()
    private var players = MutableLiveData<List<PlayerInfo>>()
    private var playerMetaList = MutableLiveData<List<FavoriteMeta>>()
    private var authUser = Firebase.auth.currentUser
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "favoritePlayers"
    var addStatusFailure = MutableLiveData<Boolean?>()
    var removeStatusFailure = MutableLiveData<Boolean?>()

    init {
        authUser = Firebase.auth.currentUser
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
                        result.documents.forEach{ playerMeta ->
                            val currentPlayerMeta = playerMeta.toObject(FavoriteMeta::class.java)
                            playerMetasToPost.add(currentPlayerMeta!!)
                            playerNamesToPost.add(currentPlayerMeta.playerName)
                            val playerToAddToPlayers = playerRepository.fetchData().filter { player ->
                                val fullName = player.firstName + " " + player.lastName
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
                // The guests starts with no favorites, do nothing
            }
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
        // Start with adding the name to our list of names,
        val namesList: List<String>
        // if it hasn't been initialized we need to init the names list
        if (playerNames.value == null) {
            namesList = listOf(name)
        } else {
            namesList = playerNames.value!!.toMutableList()
            namesList.add(name)
        }

        Log.d("AddFavorite","The new player names are %s".format(namesList.toString()))
        // Now build the player info from the name and add to list of player infos
        val newPlayerInfo = playerRepository.fetchData().filter {
            val combinedName = it.firstName + " " + it.lastName
            combinedName == name
        }
        Log.d("AddFavorite","The new player is %s".format(newPlayerInfo.toString()))
        //val newPlayers = players.value?.plus(newPlayerInfo) ?: setPlayersList(newPlayerInfo)
        val newPlayers: List<PlayerInfo> = if (players.value != null) {
            players.value!! + newPlayerInfo
        } else {
            newPlayerInfo
        }

        // Add to firestore too and only post new values on success
        // We check that the player selected is not already a favorite before adding so we should be protected from dupes
        authUser = Firebase.auth.currentUser
        if (authUser?.uid != null) {
            val favoriteMeta = FavoriteMeta(
                ownerUid = authUser!!.uid,
                playerName = name
            )
            val newMetas: List<FavoriteMeta> = if (playerMetaList.value != null) {
                playerMetaList.value!!.plus(favoriteMeta)
            } else {
                listOf(favoriteMeta)
            }
            db.collection(rootCollection)
                .add(favoriteMeta)
                .addOnSuccessListener {
                    Log.d("AddFavorite", "We are posting this to our players object: %s".format(newPlayers.toString()))
                    players.postValue(newPlayers)
                    playerNames.postValue(namesList)
                    playerMetaList.postValue(newMetas)
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
        // Remove them from the list of names
        Log.d("FavoritesViewModelRemove", "We are trying to remove: %s".format(name))
        val namesList = playerNames.value!!.toMutableList()
        namesList.remove(name)
        val selectedPlayerInfo = playerRepository.fetchData().filter {
            val combinedName = it.firstName + " " + it.lastName
            combinedName == name
        }
        // and remove them from the players info object
        val newPlayers = players.value!!.toMutableList()
        newPlayers.remove(selectedPlayerInfo[0])

        // If signed in, we need to remove from firestore too
        authUser = Firebase.auth.currentUser
        if (authUser?.uid != null) {
            // we should be guaranteed to be in the meta list
            val favoriteMetaToRemove = playerMetaList.value!!.filter {
                it.playerName == name
            }
            val newMetas = playerMetaList.value!!.toMutableList()
            newMetas.remove(favoriteMetaToRemove[0])
            db.collection(rootCollection)
                .whereEqualTo("ownerUid", authUser!!.uid)
                .whereEqualTo("playerName", name)
                .get()
                .addOnSuccessListener { allDocs ->
                    allDocs.forEach { individualDoc ->
                        db.collection(rootCollection)
                            .document(individualDoc.id)
                            .delete()
                            .addOnFailureListener {
                                removeStatusFailure.value = true
                            }
                    }
                    players.postValue(newPlayers)
                    playerNames.postValue(namesList)
                    playerMetaList.postValue(newMetas)
                }
                .addOnFailureListener {
                    removeStatusFailure.value = true
                }
        } else {
            // otherwise, just skip and remove
            players.postValue(newPlayers)
            playerNames.postValue(namesList)
        }
    }

    fun clearAllFavorites() {
        val newPlayers = listOf<PlayerInfo>()
        val namesList = listOf<String>()
        val newMetas = listOf<FavoriteMeta>()

        authUser = Firebase.auth.currentUser
        if (authUser?.uid != null) {
            db.collection(rootCollection)
                .whereEqualTo("ownerUid", authUser!!.uid)
                .get()
                .addOnSuccessListener { allDocs ->
                    allDocs.forEach { individualDoc ->
                        db.collection(rootCollection)
                            .document(individualDoc.id)
                            .delete()
                            .addOnFailureListener {
                                removeStatusFailure.value = true
                            }
                    }
                    players.postValue(newPlayers)
                    playerNames.postValue(namesList)
                    playerMetaList.postValue(newMetas)
                }
                .addOnFailureListener {
                    removeStatusFailure.value = true
                }
        } else {
            players.postValue(newPlayers)
            playerNames.postValue(namesList)
        }
    }

    fun fetchFavorites(): List<String> {
        Log.d("TracingFavorites", "We are fetching our favorites, which is: %s".format(playerNames.value.toString()))
        val favoritesNum = playerNames.value?.size ?: 0
        if (favoritesNum > 0) {
            return playerNames.value!!
        }
        return listOf()
    }

    fun downloadAndSetFavorites() {
        authUser = Firebase.auth.currentUser
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
                    result.documents.forEach { playerMeta ->
                        val currentPlayerMeta = playerMeta.toObject(FavoriteMeta::class.java)
                        playerMetasToPost.add(currentPlayerMeta!!)
                        playerNamesToPost.add(currentPlayerMeta.playerName)
                        val playerToAddToPlayers = playerRepository.fetchData().filter { player ->
                            val fullName = player.firstName + " " + player.lastName
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
        }
    }

}

