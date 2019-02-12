package edu.rosehulman.condrak.roseschedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
import io.sentry.event.UserBuilder
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
        val ctx = this.applicationContext

        // Use the Sentry DSN (client key) from the Project Settings page on Sentry
        val sentryDsn = "https://a8d13b1126b24e2388ba5502bbe067cc@sentry.io/1390907"
        Sentry.init(sentryDsn, AndroidSentryClientFactory(ctx))

        createNotificationChannel(Constants.CLASS_NOTIFICATIONS_CHANNEL_ID)
        createNotificationChannel(Constants.CLASS_ALARMS_CHANNEL_ID)

        initializeListeners()
        loginButton.setOnClickListener {
            launchLoginUI()
        }
    }

    private fun createNotificationChannel(channelID: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = if (channelID == Constants.CLASS_ALARMS_CHANNEL_ID) "Class Alarms" else "Class Notifications"
            val descriptionText = if (channelID == Constants.CLASS_ALARMS_CHANNEL_ID) "Alarms for classes" else "Notifications for classes"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            if (channelID == Constants.CLASS_ALARMS_CHANNEL_ID) {
                channel.setSound(null, null)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
                Sentry.getContext().user = UserBuilder().setId(user.uid).build()
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
                            5, "8:05 AM")))
                }
                val prefs = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString(Constants.KEY_UID, uid).apply()
                switchToMainActivity(uid)
            }
    }

    private fun switchToMainActivity(uid: String) {
        loginButton.visibility = View.GONE
        loader.visibility = View.VISIBLE
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
