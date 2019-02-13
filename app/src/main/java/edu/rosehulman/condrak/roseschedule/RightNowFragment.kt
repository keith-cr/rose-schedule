package edu.rosehulman.condrak.roseschedule


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_right_now.view.*
import org.joda.time.LocalTime


/**
 * A simple [Fragment] subclass.
 * Use the [RightNowFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RightNowFragment : Fragment() {

    private var schedule: Schedule? = null
    private var scheduleTiming: ScheduleTiming? = null
    private val refreshHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            schedule = it.getParcelable(ARG_SCHEDULE)
            scheduleTiming = it.getParcelable(ARG_SCHEDULE_TIMING)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        refreshHandler.removeCallbacksAndMessages(null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.schedule)
        val view = inflater.inflate(R.layout.fragment_right_now, container, false)
        drawView(view)
        val runnable = object : Runnable {
            override fun run() {
                drawView(view)
                refreshHandler.postDelayed(this, 60 * 1000)
            }
        }
        refreshHandler.postDelayed(runnable, ((60 * 1000) - (LocalTime().secondOfMinute * 1000)).toLong())
        return view
    }

    private fun drawView(view: View) {
        if (scheduleTiming!!.isEmptySchedule(schedule!!)) {
            view.currentClassLayout.visibility = View.GONE
            view.nextClassLayout.visibility = View.GONE
            view.welcomeLayout.visibility = View.VISIBLE
        } else {
            val currClass = scheduleTiming?.getCurrentClass(schedule!!)
            val nextClass = scheduleTiming?.getNextClass(schedule!!)
            if (currClass != null) {
                view.currentClassPeriodNumber.text = currClass.periodText()
                view.currentClassInfo.text = if (currClass.hasLocation())
                    resources.getString(R.string.class_text, currClass.className, currClass.classLocation)
                else
                    currClass.className
                view.currentClassEndTime.text = resources.getString(
                    R.string.class_end_time,
                    scheduleTiming!!.getEndTime(currClass).toString("h:mm a")
                )
                view.currentClassEndTimeRelative.text = resources.getString(
                    R.string.class_time_relative,
                    scheduleTiming!!.getRelativeEndTime(currClass)
                )
            } else {
                view.currentClassLayout.visibility = View.GONE
            }
            if (nextClass != null) {
                view.nextClassPeriodNumber.text = nextClass.periodText()
                view.nextClassInfo.text = if (nextClass.hasLocation())
                    resources.getString(R.string.class_text, nextClass.className, nextClass.classLocation)
                else
                    nextClass.className
                view.nextClassStartTime.text = resources.getString(
                    R.string.class_start_time,
                    scheduleTiming!!.getStartTime(nextClass).toString("h:mm a")
                )
                view.nextClassStartTimeRelative.text = resources.getString(
                    R.string.class_time_relative,
                    scheduleTiming!!.getRelativeStartTime(schedule!!, nextClass)
                )
            } else {
                view.nextClassLayout.visibility = View.GONE
            }
        }
    }

    companion object {

        private const val ARG_SCHEDULE = "schedule"
        private const val ARG_SCHEDULE_TIMING = "scheduleTiming"

        @JvmStatic
        fun newInstance(schedule: Schedule, scheduleTiming: ScheduleTiming) =
            RightNowFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SCHEDULE, schedule)
                    putParcelable(ARG_SCHEDULE_TIMING, scheduleTiming)
                }
            }
    }
}
