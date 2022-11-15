package edu.utap.mlbpocketguide.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.utap.mlbpocketguide.databinding.FragUserProfileBinding

class ShowUserProfile: Fragment() {

    private var _binding: FragUserProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ShowUserProfile {
            return ShowUserProfile()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
}