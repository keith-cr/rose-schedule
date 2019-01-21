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

        scheduleSettings = intent.getParcelableExtra(SCHEDULE_SETTINGS)
        schedule = intent.getParcelableExtra(SCHEDULE)
        scheduleTiming = ScheduleTiming(scheduleSettings)


        supportFragmentManager.beginTransaction().replace(R.id.content, EditDayActivityFragment.newInstance(schedule, scheduleTiming)).commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        const val SCHEDULE = "schedule"
        const val SCHEDULE_SETTINGS = "scheduleSettings"
        const val SCHEDULE_TIMING = "scheduleTiming"
        const val DAY = "day"
    }

}
