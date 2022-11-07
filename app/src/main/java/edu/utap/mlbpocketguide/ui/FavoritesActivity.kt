package edu.utap.mlbpocketguide.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.ActivityHomeBinding
import edu.utap.mlbpocketguide.ui.favorites.ShowFavorites
import edu.utap.mlbpocketguide.ui.search.SearchPlayers

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    fun onClickListener() {
        when (binding.finishButton.text) {
            "Tap to Add Favorites" -> {
                // swap to searching
                binding.finishButton.text = "Finish Adding Favorites"
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragContainer, SearchPlayers.newInstance(), "search")
                    .commitNow()
            }
            "Finish Adding Favorites" -> {
                // swap to showing
                binding.finishButton.text = "Tap to Add Favorites"
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragContainer, ShowFavorites.newInstance(), "favorites")
                    .commitNow()
            }
            else -> {}
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Search Functionality for adding new favorites
        binding.finishButton.setOnClickListener {
            onClickListener()
        }

        // Initialize the Favorites section RV/VH and handle adding new favorites
        //XXX Write me. Setup adapter.
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragContainer, ShowFavorites.newInstance(), "favorites")
            .commitNow()

        // Initialize moving around with the Nav Bar
        binding.bottomNavigation.selectedItemId = R.id.favoriteMenu
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favoriteMenu -> {
                    // do Nothing, we are already here
                    true
                }
                R.id.playerMenu -> {
                    // launch the player landing page activity
                    //val newIntent = Intent(this, PlayerProfileActivity::class.java)
                    //startActivity(intent)
                    true
                }
                R.id.matchupMenu -> {
                    // launch the player matchup page activity
                    //val newIntent = Intent(this, PlayerProfileActivity::class.java)
                    //startActivity(intent)
                    true
                }
                R.id.profileMenu -> {
                    // launch the profile page of the signed in account
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
    }


}
