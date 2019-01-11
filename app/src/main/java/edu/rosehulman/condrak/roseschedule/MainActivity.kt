package edu.rosehulman.condrak.roseschedule

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

// Fragment switching code from https://stackoverflow.com/a/46600951
class MainActivity : AppCompatActivity() {

    private var schedule = createEmptySchedule()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_right_now -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, RightNowFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_daily_view -> {
                supportFragmentManager.beginTransaction().replace(R.id.content, DailyScheduleFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_weekly_view -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun createEmptySchedule(): Schedule {
        val freePeriods: ArrayList<ClassPeriod> = ArrayList()
        for (i in 1..10)
            freePeriods.add(ClassPeriod(i,"Free", ""))
        val days: ArrayList<Day> = ArrayList()
        days.add(Day("Monday", freePeriods))
        return Schedule(ScheduleSettings(50,5,805f), days)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.content, RightNowFragment())
        ft.commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
