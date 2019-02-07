package edu.rosehulman.condrak.roseschedule

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity(), EditActivityFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(classPeriod: ClassPeriod, day: Int) {
        val intent = Intent(this, EditPeriodActivity::class.java).apply {
            putExtra(UID, uid)
            putExtra(DAY, day)
            putExtra(PERIOD, classPeriod.periodNumber-1)
        }
        startActivity(intent)
    }

    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming
    private var day: Int = 0

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

        supportFragmentManager.beginTransaction().replace(R.id.content, EditActivityFragment.newInstance(schedule, day,
            scheduleTiming)).commitAllowingStateLoss()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)

        day = savedInstanceState?.getInt(DAY) ?: intent.getIntExtra(DAY, 0)

        uid = intent.getStringExtra(UID)
        scheduleRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.USERS_COLLECTION)
            .document(uid)
        addSnapshotListener()
    }

    override fun onResume() {
        super.onResume()
        addSnapshotListener()
    }

    override fun onPause() {
        super.onPause()
        listenerRegistration.remove()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // Save the user's current game state
        outState?.run {
            putInt(DAY, day)
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val DAY = "day"
        const val PERIOD = "period"
        const val UID = "uid"
    }
}
