package edu.utap.mlbpocketguide.ui.matchupprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.FragMatchupComparisonBinding
import edu.utap.mlbpocketguide.ui.search.SearchPlayers

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

        // describe what is found in the summary section if tapping the info icon
        binding.summarySectionInfo.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Got It", {
                        dialog, _ -> dialog.cancel()
                })
                .setTitle("Summary Explanation")
                .setMessage("""
                |This section outlines high-level summaries for batting outcomes. 
                |
                |On the hitter side, it is how often that batter gets a hit, strikes out, or walks. On the pitcher side, it is how often the pitcher gives up a hit, strikes a batter out, or gives up a walk.
                | 
                |A pitcher is hoping for lower batting averages and walk rates, and high strike-out rates, while the batter is hoping for the inverse.
                """.trimMargin())
            val alert = dialogBuilder.create()
            alert.show()
        }

        // describe what is found in the hit profile section if tapping the info icon
        binding.hitProfileIcon.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Got It", {
                        dialog, _ -> dialog.cancel()
                })
                .setTitle("Hit Profile Explanation")
                .setMessage("""
                |This section describes the type of hits a batter produces, or a pitcher gives up.
                | 
                |A pitcher is hoping for a high ground ball rate, and low hard hit rates, while a batter is hoping for the opposite.
                | 
                |While a high pull rate is not necessarily good or bad, if a pitcher and batter both have a similar pull rate, it is indicative that the batter will not have to adjust their swing much to compete. 
                """.trimMargin())
            val alert = dialogBuilder.create()
            alert.show()
        }

        // describe what is found in the swing profile section if tapping the info icon
        binding.swingProfileInfo.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Got It", {
                        dialog, _ -> dialog.cancel()
                })
                .setTitle("Swing Profile Explanation")
                .setMessage("""
                |This section describes the outcome typically generated when a batter swings at, or a pitcher throws, a certain type of pitch.
                | 
                |wFB/c is the "value" generated per fastball seen or thrown, and wOther/c is the "value" for all other pitches seen or thrown.
                | 
                |Contact Rate describes the percentage of time a swing actually results in hitting the ball.
                | 
                |A pitcher is looking to minimize these numbers, while a hitter is trying to maximize.
                """.trimMargin())
            val alert = dialogBuilder.create()
            alert.show()
        }

        // Listen for and display the data
        // Pitcher
        comparisonViewModel.observeLivingPitcherStats().observe(viewLifecycleOwner, {
            binding.pitcherBA.text = String.format("%.3f", it.comparisonStats["AVG"])
            binding.pitcherKp.text = String.format("%.1f", 100 * it.comparisonStats["KP"]!!) + "%"
            binding.pitcherBBp.text = String.format("%.1f", 100 * it.comparisonStats["BBP"]!!) + "%"
            binding.pitcherGBp.text = String.format("%.1f", 100 * it.comparisonStats["GBP"]!!) + "%"
            binding.pitcherPp.text = String.format("%.1f", 100 * it.comparisonStats["PullP"]!!) + "%"
            binding.pitcherHp.text = String.format("%.1f", 100 * it.comparisonStats["HardP"]!!) + "%"
            binding.pitcherFBv.text = String.format("%.3f", it.comparisonStats["valFB"])
            binding.pitcherOv.text = String.format("%.3f", it.comparisonStats["valOTHER"])
            binding.pitcherCp.text = String.format("%.1f", 100 * it.comparisonStats["ContactP"]!!) + "%"
        })
        // Hitter
        comparisonViewModel.observeLivingHitterStats().observe(viewLifecycleOwner, {
            binding.hitterBA.text = String.format("%.3f", it.comparisonStats["AVG"])
            binding.hitterKp.text = String.format("%.1f", 100* it.comparisonStats["KP"]!!) + "%"
            binding.hitterBBp.text = String.format("%.1f", 100* it.comparisonStats["BBP"]!!) + "%"
            binding.hitterGBp.text = String.format("%.1f", 100* it.comparisonStats["GBP"]!!) + "%"
            binding.hitterPp.text = String.format("%.1f", 100* it.comparisonStats["PullP"]!!) + "%"
            binding.hitterHp.text = String.format("%.1f", 100* it.comparisonStats["HardP"]!!) + "%"
            binding.hitterFBv.text = String.format("%.3f", it.comparisonStats["valFB"])
            binding.hitterOv.text = String.format("%.3f", it.comparisonStats["valOTHER"])
            binding.hitterCp.text = String.format("%.1f", 100 * it.comparisonStats["ContactP"]!!) + "%"
        })

    }

}