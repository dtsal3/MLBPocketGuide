package edu.utap.mlbpocketguide.ui.matchupprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.FragMatchupComparisonBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel
import edu.utap.mlbpocketguide.ui.search.SearchPlayers
import edu.utap.mlbpocketguide.ui.userprofile.ShowUserProfile

class ShowPlayerComparison: Fragment(){

    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val comparisonViewModel: ComparisonViewModel by activityViewModels()
    private var _binding: FragMatchupComparisonBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ShowPlayerComparison {
            return ShowPlayerComparison()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragMatchupComparisonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Up Choosing Players
        if (comparisonViewModel.checkHitterToCompare()) {
            val fullName = comparisonViewModel.getHitterToCompare().firstName + " " + comparisonViewModel.getHitterToCompare().lastName
            binding.comparisonHitterName.text = fullName
        }
        binding.comparisonHitterName.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SearchPlayers.newInstance("searchHitters"), "search")
                .commitNow()
        }

        if (comparisonViewModel.checkPitcherToCompare()) {
            val fullName = comparisonViewModel.getPitcherToCompare().firstName + " " + comparisonViewModel.getPitcherToCompare().lastName
            binding.comparisonPitcherName.text = fullName
        }

        binding.comparisonPitcherName.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SearchPlayers.newInstance("searchPitchers"), "search")
                .commitNow()
        }

        // Set up Fetching Data
        binding.fetchStatsButton.setOnClickListener {
            if (comparisonViewModel.checkHitterToCompare() && comparisonViewModel.checkPitcherToCompare()) {
                comparisonViewModel.fetchComparisonStats()
            }
        }

        // Listen for and display the data
        // Pitcher
        comparisonViewModel.observeLivingPitcherStats().observe(viewLifecycleOwner, Observer {
            binding.pitcherBA.text = it.comparisonStats.get("AVG").toString()
            binding.pitcherKp.text = it.comparisonStats.get("KP").toString()
            binding.pitcherBBp.text = it.comparisonStats.get("BBP").toString()
            binding.pitcherGBp.text = it.comparisonStats.get("GBP").toString()
            binding.pitcherPp.text = it.comparisonStats.get("PullP").toString()
            binding.pitcherHp.text = it.comparisonStats.get("HardP").toString()
            binding.pitcherFBv.text = it.comparisonStats.get("valFB").toString()
            binding.pitcherOv.text = it.comparisonStats.get("valOTHER").toString()
            binding.pitcherCp.text = it.comparisonStats.get("ContactP").toString()
        })
        // Hitter
        comparisonViewModel.observeLivingHitterStats().observe(viewLifecycleOwner, Observer {
            binding.hitterBA.text = it.comparisonStats.get("AVG").toString()
            binding.hitterKp.text = it.comparisonStats.get("KP").toString()
            binding.hitterBBp.text = it.comparisonStats.get("BBP").toString()
            binding.hitterGBp.text = it.comparisonStats.get("GBP").toString()
            binding.hitterPp.text = it.comparisonStats.get("PullP").toString()
            binding.hitterHp.text = it.comparisonStats.get("HardP").toString()
            binding.hitterFBv.text = it.comparisonStats.get("valFB").toString()
            binding.hitterOv.text = it.comparisonStats.get("valOTHER").toString()
            binding.hitterCp.text = it.comparisonStats.get("ContactP").toString()
        })

    }

}