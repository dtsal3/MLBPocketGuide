package edu.utap.mlbpocketguide.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.ActivityComparisonBinding

class MatchupToolActivity: AppCompatActivity() {

    private lateinit var binding: ActivityComparisonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityComparisonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize moving around with the Nav Bar
        binding.bottomNavigation.selectedItemId = R.id.matchupMenu
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favoriteMenu -> {
                    // launch the favorites page activity
                    val newIntent = Intent(this, FavoritesActivity::class.java)
                    startActivity(newIntent)
                    true
                }
                R.id.playerMenu -> {
                    // launch the player landing page activity
                    //val newIntent = Intent(this, PlayerProfileActivity::class.java)
                    //startActivity(newIntent)
                    val newIntent = Intent(this, PlayerProfileActivity::class.java)
                    startActivity(newIntent)
                    true
                }
                R.id.matchupMenu -> {
                    // do Nothing, we are already here
                    true
                }
                R.id.profileMenu -> {
                    // do Nothing, we are already here
                    true
                }
                else -> {
                    // won't happen in actuality, but do nothing anyways
                    true
                }
            }
        }


    }

}