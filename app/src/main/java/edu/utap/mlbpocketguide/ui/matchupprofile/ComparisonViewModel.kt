package edu.utap.mlbpocketguide.ui.matchupprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.mlbpocketguide.api.FangraphsAPI
import edu.utap.mlbpocketguide.api.FangraphsStats
import edu.utap.mlbpocketguide.api.PlayerInfo
import edu.utap.mlbpocketguide.api.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ComparisonViewModel: ViewModel() {
    private lateinit var pitcherToCompare: PlayerInfo
    private lateinit var hitterToCompare: PlayerInfo
    private lateinit var playerProfile: PlayerInfo
    private val fangraphsApi = FangraphsAPI()
    private val playerRepository = PlayerRepository()
    private var pitcherStats = MutableLiveData<FangraphsStats>()
    private var hitterStats = MutableLiveData<FangraphsStats>()
    private var playerProfileStats = MutableLiveData<FangraphsStats>()
    private val playersFetched = mutableMapOf<String, FangraphsStats>()

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

    fun postValue(playersStats: FangraphsStats, location: String) {
        when (location) {
            "playerProfile" -> {
                playerProfileStats.postValue(playersStats)
            }
            "pitcherComparison" -> {
                pitcherStats.postValue(playersStats)
            }
            "hitterComparison" -> {
                hitterStats.postValue(playersStats)
            }
            else -> {}
        }
    }

    fun getStatistics(playerId: String, position: String, location: String) {
        Log.d("TracingComparisonViewModel","Our playerid is %s and our fetched players already are %s".format(playerId, playersFetched.keys))
        if (playerId in playersFetched.keys) {
            Log.d("TracingComparisonViewModel","We already have this player")
            postValue(playersFetched.get(playerId)!!, location)
        } else {
            viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
                Log.d("TracingComparisonViewModel", "Fetching statistics for the player")
                val playerStatsToShare = fangraphsApi.getStats(playerId, position)
                playersFetched[playerId] = playerStatsToShare
                Log.d("TracingComparisonViewModel","After adding playerid %s our fetched players are %s".format(playerId, playersFetched.keys))
                postValue(playerStatsToShare, location)
            }
        }
    }

    fun observeLivingPlayerStats(): LiveData<FangraphsStats> {
        return playerProfileStats
    }

}

