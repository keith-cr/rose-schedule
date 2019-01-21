package edu.rosehulman.condrak.roseschedule

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_period.*

class EditPeriodActivity : AppCompatActivity() {

    private lateinit var scheduleSettings: ScheduleSettings
    private lateinit var schedule: Schedule
    private lateinit var scheduleTiming: ScheduleTiming
    private var day: Int = 0
    private var period: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_period)
        setSupportActionBar(toolbar)

        scheduleSettings = intent.getParcelableExtra(SCHEDULE_SETTINGS)
        schedule = intent.getParcelableExtra(SCHEDULE)
        day = intent.getIntExtra(DAY, 0)
        period = intent.getIntExtra(PERIOD, 0)
        scheduleTiming = ScheduleTiming(scheduleSettings)

        supportFragmentManager.beginTransaction().replace(R.id.content, EditPeriodActivityFragment.newInstance(schedule, day, period, scheduleTiming)).commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        const val SCHEDULE = "schedule"
        const val SCHEDULE_SETTINGS = "scheduleSettings"
        const val SCHEDULE_TIMING = "scheduleTiming"
        const val DAY = "day"
        const val PERIOD = "period"
    }

}
