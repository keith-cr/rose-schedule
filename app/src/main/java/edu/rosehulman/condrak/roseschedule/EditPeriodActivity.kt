package edu.rosehulman.condrak.roseschedule

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_edit_period.*

class EditPeriodActivity : AppCompatActivity(), EditPeriodActivityFragment.OnSaveListener {
    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming
    private var day: Int = 0
    private var period: Int = 0

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

    override fun onResume() {
        super.onResume()
        addSnapshotListener()
    }

    override fun onPause() {
        super.onPause()
        listenerRegistration.remove()
    }

    override fun onSave(classPeriod: ClassPeriod) {
        schedule.days[day].periods[period] = classPeriod
        scheduleRef.set(schedule).addOnSuccessListener {
            Toast.makeText(this, "Class Period Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processSnapshotChanges(documentSnapshot: DocumentSnapshot) {
        schedule = documentSnapshot.toObject(Schedule::class.java)!!
        scheduleTiming = ScheduleTiming(schedule.scheduleSettings)
        scheduleTiming.init(this)

        supportFragmentManager.beginTransaction().replace(R.id.content, EditPeriodActivityFragment.newInstance(schedule,
            day, period, scheduleTiming)).commitAllowingStateLoss()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_period)
        setSupportActionBar(toolbar)

        day = savedInstanceState?.getInt(DAY) ?: intent.getIntExtra(DAY, 0)
        period = savedInstanceState?.getInt(PERIOD) ?: intent.getIntExtra(PERIOD, 0)
        uid = intent.getStringExtra(EditDayActivity.UID)
        scheduleRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.USERS_COLLECTION)
            .document(uid)
        addSnapshotListener()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // Save the user's current game state
        outState?.run {
            putInt(DAY, day)
            putInt(PERIOD, period)
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val DAY = "day"
        const val PERIOD = "period"
    }

}
