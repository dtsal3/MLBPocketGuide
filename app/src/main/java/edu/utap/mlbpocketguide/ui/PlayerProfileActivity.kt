package edu.utap.mlbpocketguide.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.ActivityHomeBinding
import edu.utap.mlbpocketguide.databinding.ActivityPlayerProfileBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel
import edu.utap.mlbpocketguide.ui.matchupprofile.ComparisonViewModel

class PlayerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerProfileBinding
    private val comparisonViewModel: ComparisonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize moving around with the Nav Bar
        binding.bottomNavigation.selectedItemId = R.id.playerMenu
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favoriteMenu -> {
                    // launch the favorites page activity
                    val newIntent = Intent(this, FavoritesActivity::class.java)
                    startActivity(newIntent)
                    true
                }
                R.id.playerMenu -> {
                    // do Nothing, we are already here
                    true
                }
                R.id.matchupMenu -> {
                    // launch the player matchup page activity
                    val newIntent = Intent(this, MatchupToolActivity::class.java)
                    startActivity(newIntent)
                    true
                }
                R.id.profileMenu -> {
                    // launch the account profile page activity
                    val newIntent = Intent(this, AccountProfileActivity::class.java)
                    startActivity(newIntent)
                    true
                }
                else -> {
                    // won't happen in actuality, but do nothing anyways
                    true
                }
            }
        }

        // call comparisonViewModel.netStats(19556, OF) ?
        comparisonViewModel.getStatistics("19556","OF","playerProfile")

        comparisonViewModel.observeLivingPlayerStats().observe(this, Observer {
            binding.textView1.text = it.profileCharacteristics.toString()
            binding.textView2.text = it.profileStatsAvg.toString()
            binding.textView3.text = it.profileStatsCounting.toString()
            binding.textView4.text = it.comparisonStats.toString()
            Log.d("Tracing", it.profileCharacteristics.toString())
        })

    }
}
