package edu.rosehulman.condrak.roseschedule

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var authListener: FirebaseAuth.AuthStateListener

    // Request code for launching the sign in Intent.
    private val RC_SIGN_IN = 1

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initializeListeners()
        loginButton.setOnClickListener {
            launchLoginUI()
        }
    }

    private fun launchLoginUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_logo)
            .build()

        // Create and launch sign-in intent
        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    private fun initializeListeners() {
        authListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user = auth.currentUser
            if (user != null) {
                doInitSetup(user.uid)
            } else {
                loginButton.visibility = View.VISIBLE
                loader.visibility = View.GONE
            }
        }
    }

    private fun doInitSetup(uid: String) {
        FirebaseFirestore
            .getInstance()
            .collection(Constants.USERS_COLLECTION)
            .document(uid).get().addOnCompleteListener {
                if (it.result?.exists() != true) {
                    FirebaseFirestore
                        .getInstance()
                        .collection(Constants.USERS_COLLECTION)
                        .document(uid).set(Schedule.createEmptySchedule(ScheduleSettings(50,
                            5, "08:05")))
                }
                switchToMainActivity(uid)
            }
    }

    private fun switchToMainActivity(uid: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(UID, uid)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        startActivity(intent)
        finish()
    }

    companion object {
        const val UID = "uid"
    }
}
