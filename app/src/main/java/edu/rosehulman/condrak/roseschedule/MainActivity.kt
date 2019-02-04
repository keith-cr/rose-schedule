package edu.rosehulman.condrak.roseschedule

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

// Fragment switching code from https://stackoverflow.com/a/46600951
class MainActivity : AppCompatActivity(), DailyScheduleFragment.OnListFragmentInteractionListener {

    enum class CurrentFragment {
        RIGHT_NOW, DAILY_VIEW, WEEKLY_VIEW
    }

    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming
    private lateinit var currentFragment: CurrentFragment

    private var uid = ""
    private lateinit var scheduleRef: DocumentReference
    private lateinit var listenerRegistration: ListenerRegistration

    fun addSnapshotListener() {
        listenerRegistration = scheduleRef
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(documentSnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(documentSnapshot: DocumentSnapshot) {
        schedule = documentSnapshot.toObject(Schedule::class.java)!!
        scheduleTiming = ScheduleTiming(schedule.scheduleSettings)
        scheduleTiming.init(this)
        val prefs = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(Constants.KEY_SCHEDULE, Gson().toJson(schedule))
        editor.apply()
        val ft = supportFragmentManager.beginTransaction()
        val fragment: Fragment = when (currentFragment) {
            CurrentFragment.RIGHT_NOW -> RightNowFragment.newInstance(schedule, scheduleTiming)
            CurrentFragment.DAILY_VIEW -> DailyScheduleFragment.newInstance(schedule, scheduleTiming)
            CurrentFragment.WEEKLY_VIEW -> WeeklyViewFragment.newInstance(schedule, scheduleTiming)
        }
        ft.replace(R.id.content, fragment)
        ft.commit()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_right_now -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, RightNowFragment.newInstance(schedule, scheduleTiming)).commit()
                currentFragment = CurrentFragment.RIGHT_NOW
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_daily_view -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, DailyScheduleFragment.newInstance(schedule, scheduleTiming)).commit()
                currentFragment = CurrentFragment.DAILY_VIEW
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_weekly_view -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, WeeklyViewFragment.newInstance(schedule, scheduleTiming)).commit()
                currentFragment = CurrentFragment.WEEKLY_VIEW
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            uid = savedInstanceState.getString(UID)!!
            currentFragment = CurrentFragment.valueOf(savedInstanceState.getString(CURRENT_FRAGMENT)!!)
        } else {
            currentFragment = CurrentFragment.RIGHT_NOW
            uid = intent.getStringExtra(UID)
        }
        setContentView(R.layout.activity_main)
        scheduleRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.USERS_COLLECTION)
            .document(uid)
        addSnapshotListener()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_edit_schedule -> {
                val intent = Intent(this, EditDayActivity::class.java).apply {
                    putExtra(UID, uid)
                }
                startActivity(intent)
            }
            R.id.action_notifications -> {

            }
            R.id.action_login -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onListFragmentInteraction(item: Schedule?) {

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // Save the user's current game state
        outState?.run {
            putString(CURRENT_FRAGMENT, currentFragment.name)
            putString(UID, uid)
        }

        listenerRegistration.remove()

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val CURRENT_FRAGMENT = "currentFragment"
        const val UID = "uid"
    }
}
