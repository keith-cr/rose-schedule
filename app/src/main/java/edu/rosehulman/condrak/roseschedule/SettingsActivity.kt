package edu.rosehulman.condrak.roseschedule

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), SettingsActivityFragment.OnFragmentInteractionListener,
    TimePickerFragment.TimeSetListener {

    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming

    private var uid = ""
    private lateinit var scheduleRef: DocumentReference
    private lateinit var listenerRegistration: ListenerRegistration

    private var fragment: SettingsActivityFragment? = null

    private fun addSnapshotListener() {
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

    override fun onSave(schedule: Schedule) {
        scheduleRef.set(schedule).addOnSuccessListener {
            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTimePickerButtonClicked(hour: Int, minute: Int) {
        TimePickerFragment.newInstance(hour, minute).show(supportFragmentManager, "timePicker")
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        fragment?.onTimeSet(view, hourOfDay, minute)
    }

    private fun processSnapshotChanges(documentSnapshot: DocumentSnapshot) {
        schedule = documentSnapshot.toObject(Schedule::class.java)!!
        scheduleTiming = ScheduleTiming(schedule.scheduleSettings)
        scheduleTiming.init(this)

        fragment = SettingsActivityFragment.newInstance(schedule)
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment as Fragment).commitAllowingStateLoss()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        uid = intent.getStringExtra(UID)
        scheduleRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.USERS_COLLECTION)
            .document(uid)
        addSnapshotListener()
    }

    companion object {
        private const val UID = "uid"
    }

}
