package edu.utap.mlbpocketguide

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import edu.utap.mlbpocketguide.databinding.ActivityMainBinding
import edu.utap.mlbpocketguide.ui.FavoritesActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
            // launch the next activity
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d("MainActivity", "sign in failed $result")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener {
            AuthInit(signInLauncher)
        }

        binding.continueAsGuestButton.setOnClickListener {
            // also launch the next activity
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

    }


}