package edu.utap.mlbpocketguide.ui.favorites

import android.util.Log
import androidx.lifecycle.*
import edu.utap.mlbpocketguide.api.PlayerInfo
import edu.utap.mlbpocketguide.api.PlayerRepository
import kotlinx.coroutines.launch

class FavoritesViewModel: ViewModel() {
    private var playerNames = MutableLiveData<List<String>>()
    private var players = MutableLiveData<List<PlayerInfo>>()
    private var playerRepository = PlayerRepository()


    // but we want to launch in a state that is initialized to the AWW subreddit
    init {
        Log.d("Tracing","We are in the init of the viewModel")
        viewModelScope.launch {
            playerNames = MutableLiveData(mutableListOf("Mookie Betts"))
            val mookieBetts = playerRepository.fetchData().filter {
                it.firstName == "Mookie"
                        && it.lastName == "Betts"
            }
            players = MutableLiveData(mookieBetts)
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
        Log.d("AddFavorite", "We are posting this to our players object: %s".format(newPlayers.toString()))
        players.postValue(newPlayers)
        playerNames.postValue(namesList)
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
        players.postValue(newPlayers)
        playerNames.postValue(namesList)
    }

    fun fetchFavorites(): List<String> {
        val favoritesNum = playerNames.value?.size ?: 0
        if (favoritesNum > 0) {
            return playerNames.value!!
        }
        return listOf<String>()
    }

}

