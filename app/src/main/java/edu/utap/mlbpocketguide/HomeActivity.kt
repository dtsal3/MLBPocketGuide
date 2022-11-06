package edu.utap.mlbpocketguide

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.mlbpocketguide.databinding.HomeActivityBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesAdapter
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel
import edu.utap.mlbpocketguide.ui.favorites.ShowFavorites
import edu.utap.mlbpocketguide.ui.search.SearchPlayers

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeActivityBinding

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

        binding = HomeActivityBinding.inflate(layoutInflater)
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
    }


}
