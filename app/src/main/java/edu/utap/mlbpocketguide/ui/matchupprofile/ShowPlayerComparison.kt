package edu.utap.mlbpocketguide.ui.matchupprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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


    }

}