package edu.utap.mlbpocketguide.ui.matchupprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.FragPlayerProfileBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel
import edu.utap.mlbpocketguide.ui.search.SearchPlayers

class ShowPlayerProfile: Fragment(){
    
    private val comparisonViewModel: ComparisonViewModel by activityViewModels()
    private var _binding: FragPlayerProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ShowPlayerProfile {
            return ShowPlayerProfile()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragPlayerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Select a Favorite
        binding.searchFavorites.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SearchPlayers.newInstance("searchFavoriteProfiles"), "search")
                .commitNow()
        }
        // Select any Player
        binding.searchAll.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SearchPlayers.newInstance("searchAnyProfiles"), "search")
                .commitNow()
        }

        // Show the player name in the profile page
        if (comparisonViewModel.checkPlayerProfile()) {
            val fullName = comparisonViewModel.getPlayerForProfile().firstName + " " + comparisonViewModel.getPlayerForProfile().lastName
            binding.playerProfileName.text = fullName
        }

        // Fetch that players stats
        binding.fetchProfileData.setOnClickListener {
            if (comparisonViewModel.checkPlayerProfile()) {
                comparisonViewModel.fetchPlayerProfileStats()
            }
        }

        // Listen for and display the data
        comparisonViewModel.observeLivingPlayerStats().observe(viewLifecycleOwner, Observer {
            binding.profileAge.text = it.profileCharacteristics.get("playerAge")
            binding.profilePos.text = it.profileCharacteristics.get("playerPosition")
            binding.profileThrow.text = it.profileCharacteristics.get("playerThrow")
            binding.profileHit.text = it.profileCharacteristics.get("playerHit")

            binding.pieChartView.text = it.profileStatsCounting.toString()
            binding.lineGraphView.text = it.profileStatsAvg.toString()
        })

    }

}