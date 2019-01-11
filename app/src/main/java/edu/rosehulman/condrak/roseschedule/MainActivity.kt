package edu.rosehulman.condrak.roseschedule

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_right_now -> {
                message.setText(R.string.title_right_now)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_daily_view -> {
                message.setText(R.string.title_daily_view)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_weekly_view -> {
                message.setText(R.string.title_weekly_view)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
