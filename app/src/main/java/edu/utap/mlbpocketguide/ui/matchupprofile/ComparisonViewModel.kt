package edu.utap.mlbpocketguide.ui.matchupprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import edu.utap.mlbpocketguide.api.PlayerInfo
import edu.utap.mlbpocketguide.api.PlayerRepository

class ComparisonViewModel: ViewModel() {
    private lateinit var pitcherToCompare: PlayerInfo
    private lateinit var hitterToCompare: PlayerInfo
    private lateinit var playerProfile: PlayerInfo
    private val playerRepository = PlayerRepository()

    // Helper to get a PlayerInfo object given a name
    fun getPlayer(name: String): PlayerInfo {
        val newPlayerInfo = playerRepository.fetchData().filter {
            val combinedName = it.firstName + " " + it.lastName
            combinedName == name
        }
        return newPlayerInfo[0]
    }

    // Hmm.. I was about to set the observer but i actually think this might be the wrong way to go about this... maybe not..
    // We will have to again set the profile and matchup sections to be fragments within the activities in order to replace with the same search functionality
    // and use a data lookup whenever the fragment restarts to see what player we should show when the fragment opens?

    // Handle setting and getting the pitcher for a comparison
    fun checkPitcherToCompare(): Boolean {
        return this::pitcherToCompare.isInitialized
    }

    fun setPitcherToCompare(name: String) {
        pitcherToCompare = getPlayer(name)
    }

    fun getPitcherToCompare(): PlayerInfo {
        return pitcherToCompare
    }

    // Handle setting and getting the hitter for a comparison
    fun checkHitterToCompare(): Boolean {
        return this::hitterToCompare.isInitialized
    }

    fun setHitterToCompare(name: String) {
        hitterToCompare = getPlayer(name)
    }

    fun getHitterToCompare(): PlayerInfo {
        return hitterToCompare
    }

    // Handle setting and getting the player for a profile view
    fun checkPlayerProfile(): Boolean {
        return this::playerProfile.isInitialized
    }

    fun setPlayerForProfile(name: String) {
        playerProfile = getPlayer(name)
    }

    fun getPlayerForProfile(): PlayerInfo {
        return playerProfile
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

}

