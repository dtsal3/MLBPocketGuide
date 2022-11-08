package edu.utap.mlbpocketguide.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.utap.mlbpocketguide.api.PlayerRepository
import edu.utap.mlbpocketguide.databinding.FragSearchBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel
import edu.utap.mlbpocketguide.ui.matchupprofile.ComparisonViewModel

class SearchPlayers : Fragment(){
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val comparisonViewModel: ComparisonViewModel by activityViewModels()
    lateinit var listAdapter: ArrayAdapter<String>
    private var _binding: FragSearchBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    companion object {
        private val search = "search"
        fun newInstance(searchMode: String): SearchPlayers {
            val frag = SearchPlayers()
            val bundle = Bundle()
            bundle.putString(search, searchMode)
            frag.arguments = bundle
            return frag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        val playersLV = binding.playerLV
        val searchView = binding.playerSearch
        val players = PlayerRepository().fetchData()
        val listOfPlayers = mutableListOf<String>()
        players.forEach {
            val fullName = it.firstName + " " + it.lastName
            listOfPlayers.add(fullName)
        }
        listAdapter = ArrayAdapter<String> (
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOfPlayers
        )
        playersLV.adapter = listAdapter

        // What mode are we searching for?
        val searchMode = arguments?.getString(search)
        // Determine what to do when selecting a player, based on what mode we are searching for
        playersLV.setOnItemClickListener { _, _, position, _ ->
            val playerSelected = listAdapter.getItem(position)
            when (searchMode) {
                "searchFavorites" -> {
                    // when we select a player, try to add them to our RV of favorite players, or ignore if they are already there
                    Log.d("HomeActivity", "The i clicked on is: %s".format(position.toString()))
                    Log.d(
                        "HomeActivity",
                        "The adapter item clicked is: %s".format(listAdapter.getItem(position))
                    )
                    if (favoritesViewModel.isFavorite(playerSelected!!)) {
                        Toast.makeText(
                            requireContext(),
                            "They are already a favorite!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        favoritesViewModel.addFavorite(playerSelected)
                    }
                }
                "searchProfiles" -> {
                    // do nothing yet
                    comparisonViewModel.setPlayerForProfile(playerSelected!!)
                }
                "searchPitchers" -> {
                    // do nothing yet
                    comparisonViewModel.setPitcherToCompare(playerSelected!!)
                }
                "searchHitters" -> {
                    comparisonViewModel.setHitterToCompare(playerSelected!!)
                }
                else -> {}//do nothing
            }
        }



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (listOfPlayers.contains(query)) {
                    listAdapter.filter.filter(query)
                } else {
                    Toast.makeText(requireContext(), "No players found..", Toast.LENGTH_LONG).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val q = newText?.length ?: 0
                if (q > 0) {
                    playersLV.visibility = View.VISIBLE
                } else {
                    playersLV.visibility = View.INVISIBLE
                }
                listAdapter.filter.filter(newText)
                return false
            }
        })

    }
}