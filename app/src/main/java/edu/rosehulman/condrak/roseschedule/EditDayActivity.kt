package edu.rosehulman.condrak.roseschedule

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_day.*

class EditDayActivity : AppCompatActivity(), EditDayActivityFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(day: Int) {
        val intent = Intent(this, EditActivity::class.java).apply {
            putExtra(SCHEDULE, schedule)
            putExtra(DAY, day)
            putExtra(SCHEDULE_SETTINGS, scheduleSettings)
        }
        startActivity(intent)
    }

    private lateinit var scheduleSettings: ScheduleSettings
    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_day)
        setSupportActionBar(toolbar)

        if (savedInstanceState != null) {
            scheduleSettings = savedInstanceState.getParcelable(SCHEDULE_SETTINGS)!!
            schedule = savedInstanceState.getParcelable(SCHEDULE)!!
            scheduleTiming = savedInstanceState.getParcelable(SCHEDULE_TIMING)!!
        } else {
            scheduleSettings = intent.getParcelableExtra(SCHEDULE_SETTINGS)
            schedule = intent.getParcelableExtra(SCHEDULE)
            scheduleTiming = ScheduleTiming(scheduleSettings)
        }

        supportFragmentManager.beginTransaction().replace(R.id.content, EditDayActivityFragment.newInstance(schedule, scheduleTiming)).commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // Save the user's current game state
        outState?.run {
            putParcelable(SCHEDULE, schedule)
            putParcelable(SCHEDULE_SETTINGS, scheduleSettings)
            putParcelable(SCHEDULE_TIMING, scheduleTiming)
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val SCHEDULE = "schedule"
        const val SCHEDULE_SETTINGS = "scheduleSettings"
        const val SCHEDULE_TIMING = "scheduleTiming"
        const val DAY = "day"
    }

}
