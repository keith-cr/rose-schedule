package edu.rosehulman.condrak.roseschedule


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_weekly_view.view.*
import org.joda.time.DateTime

/**
 * A simple [Fragment] subclass.
 * Use the [WeeklyViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class WeeklyViewFragment : Fragment() {
    private var schedule: Schedule? = null
    private var scheduleTiming: ScheduleTiming? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            schedule = it.getParcelable(ARG_SCHEDULE)
            scheduleTiming = it.getParcelable(ARG_SCHEDULE_TIMING)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weekly_view, container, false)
        activity?.title = getString(R.string.weekly_schedule)
        for (day in 0 until 5) {
            if (day == DateTime().dayOfWeek-1) {
                val dayText: TextView = when (day) {
                    0 -> view.monday
                    1 -> view.tuesday
                    2 -> view.wednesday
                    3 -> view.thursday
                    else -> view.friday
                }
                dayText.setTextColor(resources.getColor(R.color.colorPrimary, null))
            }
            for (period in 0 until 10) {
                if (day == 0) {
                    val classPeriod = schedule!!.days[day].periods[period]
                    val id = resources.getIdentifier("_${period + 1}_period_title", "id", activity?.packageName)
                    val item = view.findViewById<TextView>(id)
                    item.text = resources.getString(R.string.period_title,
                        classPeriod.shortPeriodText(), scheduleTiming!!.getStartTime(classPeriod).toString("h:mm"))
                    if (scheduleTiming!!.isNow(classPeriod) && DateTime().dayOfWeek-1 in 0 until 5)
                        item.setTextColor(resources.getColor(R.color.colorPrimary, null))
                }
                val id = resources.getIdentifier("day${day}period$period", "id", activity?.packageName)
                val classPeriod = schedule!!.days[day].periods[period]
                val item = view.findViewById<TextView>(id)
                if (classPeriod.hasLocation())
                    item.text = resources.getString(R.string.class_text,
                        classPeriod.className, classPeriod.classLocation)
                else
                    item.text = classPeriod.className
                if (day == DateTime().dayOfWeek-1 && scheduleTiming!!.isNow(classPeriod))
                    item.setTextColor(resources.getColor(R.color.colorPrimary, null))
            }
        }
        return view
    }


    companion object {

        private const val ARG_SCHEDULE = "schedule"
        private const val ARG_SCHEDULE_TIMING = "scheduleTiming"

        @JvmStatic
        fun newInstance(schedule: Schedule, scheduleTiming: ScheduleTiming) =
            WeeklyViewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SCHEDULE, schedule)
                    putParcelable(ARG_SCHEDULE_TIMING, scheduleTiming)
                }
            }
    }
}
