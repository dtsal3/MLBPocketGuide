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
import edu.utap.mlbpocketguide.databinding.SearchFragBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel

class SearchPlayers : Fragment(){
    private val viewModel: FavoritesViewModel by activityViewModels()
    lateinit var listAdapter: ArrayAdapter<String>
    private var _binding: SearchFragBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    companion object {
        fun newInstance(): SearchPlayers {
            return SearchPlayers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = SearchFragBinding.inflate(inflater, container, false)
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
        // when we select a player, try to add them to our RV of favorite players, or ignore if they are already there
        playersLV.setOnItemClickListener { adapterView, view, i, l ->
            Log.d("HomeActivity", "The i clicked on is: %s".format(i.toString()) )
            Log.d("HomeActivity", "The adapter item clicked is: %s".format(listAdapter.getItem(i)))

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