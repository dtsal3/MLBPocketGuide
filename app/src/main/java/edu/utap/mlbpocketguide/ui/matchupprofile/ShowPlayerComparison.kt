package edu.utap.mlbpocketguide.ui.matchupprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.FragMatchupComparisonBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel
import edu.utap.mlbpocketguide.ui.search.SearchPlayers
import edu.utap.mlbpocketguide.ui.userprofile.ShowUserProfile

class ShowPlayerComparison: Fragment(){

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
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Player Matchups"
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
            binding.pitcherBA.text = String.format("%.3f", it.comparisonStats.get("AVG"))
            binding.pitcherKp.text = String.format("%.1f", 100 * it.comparisonStats.get("KP")!!) + "%"
            binding.pitcherBBp.text = String.format("%.1f", 100 * it.comparisonStats.get("BBP")!!) + "%"
            binding.pitcherGBp.text = String.format("%.1f", 100 * it.comparisonStats.get("GBP")!!) + "%"
            binding.pitcherPp.text = String.format("%.1f", 100 * it.comparisonStats.get("PullP")!!) + "%"
            binding.pitcherHp.text = String.format("%.1f", 100 * it.comparisonStats.get("HardP")!!) + "%"
            binding.pitcherFBv.text = String.format("%.3f", it.comparisonStats.get("valFB"))
            binding.pitcherOv.text = String.format("%.3f", it.comparisonStats.get("valOTHER"))
            binding.pitcherCp.text = String.format("%.1f", 100 * it.comparisonStats.get("ContactP")!!) + "%"
        })
        // Hitter
        comparisonViewModel.observeLivingHitterStats().observe(viewLifecycleOwner, Observer {
            binding.hitterBA.text = String.format("%.3f", it.comparisonStats.get("AVG"))
            binding.hitterKp.text = String.format("%.1f", 100*it.comparisonStats.get("KP")!!) + "%"
            binding.hitterBBp.text = String.format("%.1f", 100*it.comparisonStats.get("BBP")!!) + "%"
            binding.hitterGBp.text = String.format("%.1f", 100*it.comparisonStats.get("GBP")!!) + "%"
            binding.hitterPp.text = String.format("%.1f", 100*it.comparisonStats.get("PullP")!!) + "%"
            binding.hitterHp.text = String.format("%.1f", 100*it.comparisonStats.get("HardP")!!) + "%"
            binding.hitterFBv.text = String.format("%.3f", it.comparisonStats.get("valFB"))
            binding.hitterOv.text = String.format("%.3f", it.comparisonStats.get("valOTHER"))
            binding.hitterCp.text = String.format("%.1f", 100 * it.comparisonStats.get("ContactP")!!) + "%"
        })

    }

}