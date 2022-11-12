package edu.utap.mlbpocketguide.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import edu.utap.mlbpocketguide.databinding.ActivityHomeBinding
import edu.utap.mlbpocketguide.databinding.ActivityPlayerProfileBinding
import edu.utap.mlbpocketguide.databinding.ActivityProfileBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel
import edu.utap.mlbpocketguide.ui.matchupprofile.ComparisonViewModel

class PlayerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerProfileBinding
    private val comparisonViewModel: ComparisonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // call comparisonViewModel.netStats(19556, OF) ?
        comparisonViewModel.getStatistics("19556","OF","playerProfile")

        comparisonViewModel.observeLivingPlayerStats().observe(this, Observer {
            Log.d("Tracing", it.playerInfo.toString())
        })

    }
}
