package edu.utap.mlbpocketguide.ui.favorites

import android.util.Log
import androidx.lifecycle.*
import edu.utap.mlbpocketguide.api.PlayerInfo
import edu.utap.mlbpocketguide.api.PlayerRepository
import kotlinx.coroutines.launch

class FavoritesViewModel: ViewModel() {
    private var playerNames = MutableLiveData<String>()
    private var players = MutableLiveData<List<PlayerInfo>>()
    private var playerRepository = PlayerRepository()


    // but we want to launch in a state that is initialized to the AWW subreddit
    init {
        Log.d("Tracing","We are in the init of the viewModel")
        viewModelScope.launch {
            playerNames = MutableLiveData("Mookie Betts")
            val mookieBetts = playerRepository.fetchData().filter {
                it.firstName == "Mookie"
                        && it.lastName == "Betts"
            }
            players = MutableLiveData(mookieBetts)
            Log.d("Tracing","And after adding mookie, our players length is %s".format(players.value?.size))
        }
    }

    val liveFavorites = MediatorLiveData<List<PlayerInfo>>().apply {
        this.value = players.value
    }

    fun observeLivingFavorites(): LiveData<List<PlayerInfo>> {
        return liveFavorites
    }


}

