package edu.utap.mlbpocketguide.ui.matchupprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.utap.mlbpocketguide.databinding.FragPlayerProfileBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel

class ShowPlayerProfile: Fragment(){

    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val comparisonViewModel: ComparisonViewModel by activityViewModels()
    private var _binding: FragPlayerProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ShowPlayerProfile {
            return ShowPlayerProfile()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragPlayerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}