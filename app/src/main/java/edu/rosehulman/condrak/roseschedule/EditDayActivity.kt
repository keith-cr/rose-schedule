package edu.rosehulman.condrak.roseschedule

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_edit_day.*

class EditDayActivity : AppCompatActivity(), EditDayActivityFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(day: Int) {
        val intent = Intent(this, EditActivity::class.java).apply {
            putExtra(UID, uid)
            putExtra(DAY, day)
        }
        startActivity(intent)
    }

    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming

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
        supportFragmentManager.beginTransaction().replace(R.id.content, EditDayActivityFragment.newInstance(schedule,
            scheduleTiming)).commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_day)
        setSupportActionBar(toolbar)

        uid = intent.getStringExtra(UID)
        scheduleRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.USERS_COLLECTION)
            .document(uid)
        addSnapshotListener()
    }

    companion object {
        const val DAY = "day"
        const val UID = "uid"
    }

}
