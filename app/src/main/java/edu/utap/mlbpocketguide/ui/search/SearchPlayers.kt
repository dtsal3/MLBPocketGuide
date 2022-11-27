package edu.utap.mlbpocketguide.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.api.PlayerRepository
import edu.utap.mlbpocketguide.databinding.FragSearchBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel
import edu.utap.mlbpocketguide.ui.favorites.ShowFavorites
import edu.utap.mlbpocketguide.ui.matchupprofile.ComparisonViewModel
import edu.utap.mlbpocketguide.ui.matchupprofile.ShowPlayerComparison
import edu.utap.mlbpocketguide.ui.matchupprofile.ShowPlayerProfile

class SearchPlayers : Fragment(){
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val comparisonViewModel: ComparisonViewModel by activityViewModels()
    lateinit var listAdapter: ArrayAdapter<String>
    private var _binding: FragSearchBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    companion object {
        private const val search = "search"
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

    private fun setAdapter(listOfPlayerNames: List<String>, LV: ListView) {
        listAdapter = ArrayAdapter<String> (
            requireContext(),
            android.R.layout.simple_list_item_1,
            listOfPlayerNames
        )
        LV.adapter = listAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        val playersLV = binding.playerLV
        val searchView = binding.playerSearch
        // Initialize the players data, and build our filtering lists, and set up our full names list
        val playersRepo = PlayerRepository()
        val listOfPlayers = playersRepo.getAllNames()
        setAdapter(listOfPlayers, playersLV)

        // What mode are we searching for?
        val searchMode = arguments?.getString(search)
        // We determine what list to serve based on the search mode
        when (searchMode) {
            "searchFavorites" -> {
                // choose any player not already in favorites and keep finish button
                binding.finishSearchingButton.visibility = View.VISIBLE
                binding.finishSearchingButton.isClickable = true
                binding.playerSearch.queryHint = "Add Another Favorite Player"
                val favs = favoritesViewModel.fetchFavorites()
                Log.d("TracingSearch","The favorites to filter out are: %s".format(favs.toString()))
                val notFavorites = listOfPlayers.filterNot {
                    favs.contains(it)
                }
                setAdapter(notFavorites, playersLV)
            }
            "searchAnyProfiles" -> {
                // choose anything, but hide the finish button
                (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Search All Players"
                binding.finishSearchingButton.visibility = View.GONE
                binding.finishSearchingButton.isClickable = false
                binding.playerSearch.queryHint = "Choose a Player to Observe"
                setAdapter(listOfPlayers, playersLV)
            }
            "searchFavoriteProfiles" -> {
                // choose any favorites, and hide the finish button
                (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Search Favorites"
                binding.finishSearchingButton.visibility = View.GONE
                binding.finishSearchingButton.isClickable = false
                binding.playerSearch.queryHint = "Choose a Favorite to Observe"
                setAdapter(favoritesViewModel.fetchFavorites(), playersLV)
            }
            "searchPitchers" -> {
                // only allow us to choose pitchers
                (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Search All Pitchers"
                binding.finishSearchingButton.visibility = View.GONE
                binding.finishSearchingButton.isClickable = false
                binding.playerSearch.queryHint = "Choose a Pitcher to Compare"
                setAdapter(playersRepo.getPitcherNames(), playersLV)
            }
            "searchHitters" -> {
                // only allow us to choose hitters
                (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Search All Hitters"
                binding.finishSearchingButton.visibility = View.GONE
                binding.finishSearchingButton.isClickable = false
                binding.playerSearch.queryHint = "Choose a Hitter to Compare"
                setAdapter(playersRepo.getHitterNames(), playersLV)
            }

        }

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
                        listAdapter.remove(playerSelected)
                        listAdapter.notifyDataSetChanged()
                    }
                }
                "searchAnyProfiles" -> {
                    comparisonViewModel.setPlayerForProfile(playerSelected!!)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragContainer, ShowPlayerProfile.newInstance(), "playerProfile")
                        .commitNow()
                }
                "searchFavoriteProfiles" -> {
                    comparisonViewModel.setPlayerForProfile(playerSelected!!)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragContainer, ShowPlayerProfile.newInstance(), "playerProfile")
                        .commitNow()
                }
                "searchPitchers" -> {
                    comparisonViewModel.setPitcherToCompare(playerSelected!!)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragContainer, ShowPlayerComparison.newInstance(), "playerComparison")
                        .commitNow()
                }
                "searchHitters" -> {
                    comparisonViewModel.setHitterToCompare(playerSelected!!)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragContainer, ShowPlayerComparison.newInstance(), "playerComparison")
                        .commitNow()
                }
                else -> {}//do nothing
            }
        }

        binding.finishSearchingButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, ShowFavorites.newInstance(), "favorites")
                .commitNow()
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

        // We want to launch a toast based on whether or not we successfully added a favorite, but have no access to the context
        favoritesViewModel.addStatusFailure.observe(viewLifecycleOwner, { status ->
            status?.let {
                //Reset status value at first to prevent multitriggering
                //and to be available to trigger action again
                favoritesViewModel.addStatusFailure.value = null
                Toast.makeText(
                    requireContext(),
                    "Sorry, there was an error adding your favorite. Try again!",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        favoritesViewModel.removeStatusFailure.observe(viewLifecycleOwner, { status ->
            status?.let {
                //Reset status value at first to prevent multitriggering
                //and to be available to trigger action again
                favoritesViewModel.removeStatusFailure.value = null
                Toast.makeText(
                    requireContext(),
                    "Sorry, there was an error removing your favorite. Try again!",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

    }
}