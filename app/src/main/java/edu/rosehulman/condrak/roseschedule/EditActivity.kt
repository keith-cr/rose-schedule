package edu.rosehulman.condrak.roseschedule

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity(), EditActivityFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(classPeriod: ClassPeriod, day: Int) {

    }

    private lateinit var scheduleSettings: ScheduleSettings
    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)

        if (savedInstanceState != null) {
            scheduleSettings = savedInstanceState.getParcelable(MainActivity.SCHEDULE_SETTINGS)!!
            schedule = savedInstanceState.getParcelable(MainActivity.SCHEDULE)!!
            scheduleTiming = savedInstanceState.getParcelable(MainActivity.SCHEDULE_TIMING)!!
        } else {
            scheduleSettings = intent.getParcelableExtra(SCHEDULE_SETTINGS)
            schedule = intent.getParcelableExtra(SCHEDULE)
            scheduleTiming = ScheduleTiming(scheduleSettings)
        }


        supportFragmentManager.beginTransaction().replace(R.id.content, EditActivityFragment.newInstance(schedule, scheduleTiming)).commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        const val SCHEDULE = "schedule"
        const val SCHEDULE_SETTINGS = "scheduleSettings"
        const val SCHEDULE_TIMING = "scheduleTiming"
    }

}
