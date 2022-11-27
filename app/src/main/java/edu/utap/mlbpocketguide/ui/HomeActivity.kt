package edu.utap.mlbpocketguide.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.ActivityHomeBinding
import edu.utap.mlbpocketguide.ui.favorites.ShowFavorites
import edu.utap.mlbpocketguide.ui.matchupprofile.ShowPlayerComparison
import edu.utap.mlbpocketguide.ui.matchupprofile.ShowPlayerProfile
import edu.utap.mlbpocketguide.ui.userprofile.ShowUserProfile

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // actionBar?.setDisplayHomeAsUpEnabled(false) didn't work
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)

        // Initialize the Favorites section RV/VH
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragContainer, ShowFavorites.newInstance(), "favorites")
            .commitNow()

        // Initialize moving around with the Nav Bar
        binding.bottomNavigation.selectedItemId = R.id.favoriteMenu
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favoriteMenu -> {
                    val favFrag = supportFragmentManager.findFragmentByTag("favorites")
                    if (favFrag != null && favFrag.isVisible) {
                        // do nothing, we are already here
                    } else {
                        // launch this fragment
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragContainer, ShowFavorites.newInstance(), "favorites")
                            .commitNow()
                    }
                    true
                }
                R.id.playerMenu -> {
                    val playerFrag = supportFragmentManager.findFragmentByTag("playerProfile")
                    if (playerFrag != null && playerFrag.isVisible) {
                        // do nothing, we are already here
                    } else {
                        // launch this fragment
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragContainer, ShowPlayerProfile.newInstance(), "playerProfile")
                            .commitNow()
                    }
                    true
                }
                R.id.matchupMenu -> {
                    val comparisonFrag = supportFragmentManager.findFragmentByTag("playerComparison")
                    if (comparisonFrag != null && comparisonFrag.isVisible) {
                        // do nothing, we are already here
                    } else {
                        // launch this fragment
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragContainer, ShowPlayerComparison.newInstance(), "playerComparison")
                            .commitNow()
                    }
                    true
                }
                R.id.profileMenu -> {
                    val userProfileFrag = supportFragmentManager.findFragmentByTag("userProfile")
                    if (userProfileFrag != null && userProfileFrag.isVisible) {
                        // do nothing, we are already here
                    } else {
                        // launch this fragment
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragContainer, ShowUserProfile.newInstance(), "userProfile")
                            .commitNow()
                    }
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
