package edu.rosehulman.condrak.roseschedule

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import edu.rosehulman.condrak.roseschedule.DailyScheduleFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_daily__schedule.view.*

class DailyScheduleRecyclerViewAdapter(
    private val schedule: Schedule,
    private val scheduleTiming: ScheduleTiming,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<DailyScheduleRecyclerViewAdapter.ViewHolder>() {

    private val day: Int = 0
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Schedule
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_daily__schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = schedule.days[day].periods[position]

        with(holder.mView) {
            tag = schedule
            setOnClickListener(mOnClickListener)
            periodTextView.text = item.getPeriodText()
            if (item.hasLocation())
                classTextView.text = resources.getString(R.string.class_text, item.className, item.classLocation)
            else
                classTextView.text = item.className
            startTimeTextView.text = scheduleTiming.getStartTime(item).toString("hh:mm aa")
            endTimeTextView.text = scheduleTiming.getEndTime(item).toString("hh:mm aa")
        }
    }

    override fun getItemCount(): Int = schedule.days[day].periods.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView)
}
