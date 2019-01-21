package edu.rosehulman.condrak.roseschedule

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity(), EditActivityFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(classPeriod: ClassPeriod, day: Int) {
        val intent = Intent(this, EditPeriodActivity::class.java).apply {
            putExtra(SCHEDULE, schedule)
            putExtra(DAY, day)
            putExtra(PERIOD, classPeriod.periodNumber)
            putExtra(SCHEDULE_SETTINGS, scheduleSettings)
        }
        startActivity(intent)
    }

    private lateinit var scheduleSettings: ScheduleSettings
    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming
    private var day: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)

        if (savedInstanceState != null) {
            scheduleSettings = savedInstanceState.getParcelable(SCHEDULE_SETTINGS)!!
            schedule = savedInstanceState.getParcelable(SCHEDULE)!!
            scheduleTiming = savedInstanceState.getParcelable(SCHEDULE_TIMING)!!
            day = savedInstanceState.getInt(DAY)
        } else {
            scheduleSettings = intent.getParcelableExtra(SCHEDULE_SETTINGS)
            schedule = intent.getParcelableExtra(SCHEDULE)
            day = intent.getIntExtra(DAY, 0)
            scheduleTiming = ScheduleTiming(scheduleSettings)
        }


        supportFragmentManager.beginTransaction().replace(R.id.content, EditActivityFragment.newInstance(schedule, day, scheduleTiming)).commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        const val SCHEDULE = "schedule"
        const val DAY = "day"
        const val PERIOD = "period"
        const val SCHEDULE_SETTINGS = "scheduleSettings"
        const val SCHEDULE_TIMING = "scheduleTiming"
    }

}
