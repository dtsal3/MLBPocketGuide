package edu.utap.mlbpocketguide.ui.favorites


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.FragFavoritesBinding
import edu.utap.mlbpocketguide.ui.search.SearchPlayers


class ShowFavorites : Fragment(){
    lateinit var favoritesAdapter: FavoritesAdapter
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    // lateinit var listAdapter: ArrayAdapter<String>
    private var _binding: FragFavoritesBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    companion object {
        fun newInstance(): ShowFavorites {
            return ShowFavorites()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "My Favorites"
        _binding = FragFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        // Initialize the Favorites section RV/VH and handle adding new favorites
        //XXX Write me. Setup adapter.
        Log.d("Tracing", "Setting up the recycler for favorites")
        val rv = binding.favoritesRV
        favoritesAdapter = FavoritesAdapter(favoritesViewModel)
        rv.adapter = favoritesAdapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        val decor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(decor)
        val currentList = favoritesViewModel.observeLivingFavorites()
        favoritesAdapter.submitList(currentList.value)
        Log.d("Tracing", "Our current list submitted is %s".format(currentList.value.toString()))
        favoritesAdapter.notifyDataSetChanged()

        favoritesViewModel.observeLivingFavorites().observe(viewLifecycleOwner, Observer {
            favoritesAdapter.submitList(it)
            favoritesAdapter.notifyDataSetChanged()
        })

        binding.startSearchingButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SearchPlayers.newInstance("searchFavorites"), "search")
                .commitNow()
        }
    }

}