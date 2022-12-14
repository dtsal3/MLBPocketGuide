package edu.utap.mlbpocketguide

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.utap.mlbpocketguide.databinding.ActivityMainBinding
import edu.utap.mlbpocketguide.ui.HomeActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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
            // launch the next activity
            val intent = Intent(this, HomeActivity::class.java)
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

        // Force sign-out on Main Activity
        if (authUser != null) {
            FirebaseAuth.getInstance().signOut()
        }

        binding.signUpButton.setOnClickListener {
            AuthInit(signInLauncher)
        }

        binding.continueAsGuestButton.setOnClickListener {
            // also launch the next activity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

    }


}