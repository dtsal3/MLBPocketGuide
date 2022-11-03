package edu.utap.mlbpocketguide

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.utap.mlbpocketguide.api.PlayerRepository
import edu.utap.mlbpocketguide.databinding.ActivityMainBinding
import edu.utap.mlbpocketguide.databinding.HomeActivityBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeActivityBinding
    lateinit var playersLV: ListView
    lateinit var listAdapter: ArrayAdapter<String>
    lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playersLV = binding.playerLV
        searchView = binding.playerSearch

        val players = PlayerRepository().fetchData()
        val listOfPlayers = mutableListOf<String>()

        players.forEach {
            val fullName = it.firstName + " " + it.lastName
            listOfPlayers.add(fullName)
        }

        // replace with one that allows us to set click listener and logo
        // this doesn't actually work
        listAdapter = ArrayAdapter<String> (
            this,
            R.layout.player_row,
            listOfPlayers
        )
        playersLV.adapter = listAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (listOfPlayers.contains(query)) {
                    listAdapter.filter.filter(query)
                } else {
                    // if query is not present we are displaying
                    // a toast message as no  data found..
                    Toast.makeText(this@HomeActivity, "No players found..", Toast.LENGTH_LONG)
                        .show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we
                // are filtering our adapter with
                // new text on below line.
                listAdapter.filter.filter(newText)
                return false
            }
        })
    }

}
