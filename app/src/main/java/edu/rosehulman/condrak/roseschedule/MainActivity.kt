package edu.rosehulman.condrak.roseschedule

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.LocalTime

// Fragment switching code from https://stackoverflow.com/a/46600951
class MainActivity : AppCompatActivity(), DailyScheduleFragment.OnListFragmentInteractionListener {
    private var scheduleSettings = ScheduleSettings(50,5, LocalTime("08:05"))
    private var schedule = Schedule.createEmptySchedule(scheduleSettings)
    private var scheduleTiming = ScheduleTiming(scheduleSettings)

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_right_now -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, RightNowFragment.newInstance(schedule)).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_daily_view -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, DailyScheduleFragment.newInstance(schedule, scheduleTiming)).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_weekly_view -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, WeeklyViewFragment.newInstance(schedule)).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.content, RightNowFragment())
        ft.commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onListFragmentInteraction(item: Schedule?) {

    }
}
