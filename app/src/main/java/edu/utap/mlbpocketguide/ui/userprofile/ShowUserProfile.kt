package edu.utap.mlbpocketguide.ui.userprofile

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.utap.mlbpocketguide.AuthInit
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.FragUserProfileBinding
import edu.utap.mlbpocketguide.ui.favorites.FavoritesViewModel

class ShowUserProfile: Fragment() {

    private var _binding: FragUserProfileBinding? = null
    private val binding get() = _binding!!
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private var authUser = Firebase.auth.currentUser

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // we might build the ability to change this in the profile page for fun
            val user = FirebaseAuth.getInstance().currentUser
            if (user?.displayName.isNullOrEmpty()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName("Anonymous user")
                    .build()
                user!!.updateProfile(profileUpdates)
            }
            // refresh the current fragment with the new initialization
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, newInstance(), "userProfile")
                .commitNow()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d("MainActivity", "sign in failed $result")
        }
    }

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
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "My Account"
        _binding = FragUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clearFavorites.setOnClickListener {
            val favoritesFetched = favoritesViewModel.fetchFavorites()
            if (favoritesFetched.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Removing all favorites!",
                    Toast.LENGTH_LONG
                ).show()
                favoritesFetched.forEach {
                    favoritesViewModel.removeFavorite(it)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "There aren't any favorites to remove!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (authUser != null) {
            // show the user's email address
            val userEmail = Firebase.auth.currentUser?.email ?: "There was an error fetching your email."
            binding.emailActual.text = userEmail
            binding.passwordActual.text = "********"
            // set it to a sign out button
            binding.signOut.text = "Sign Out"
            binding.signOut.setOnClickListener {
                //Firebase.auth.signOut()
                Log.d("UserProfile", "I would be signing out right now...")
            }
        } else {
            binding.emailActual.text = "Sign in to display your email."
            binding.passwordActual.text = ""
            // set it to a sign in button
            binding.signOut.text = "Log In"
            binding.signOut.setOnClickListener {
                AuthInit(signInLauncher)
            }
        }

        // Listen for the sign out event and refresh the fragment when it happens

    }
}