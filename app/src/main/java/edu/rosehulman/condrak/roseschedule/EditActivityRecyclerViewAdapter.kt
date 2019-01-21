package edu.rosehulman.condrak.roseschedule

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.rosehulman.condrak.roseschedule.EditActivityFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_edit.view.*

class EditActivityRecyclerViewAdapter(
    private val schedule: Schedule,
    private val scheduleTiming: ScheduleTiming,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<EditActivityRecyclerViewAdapter.ViewHolder>() {

    private var day: Int = scheduleTiming.getCurrentClassDay()
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            // TODO: Fix
            val item = v.tag as ClassPeriod
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item, 0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_edit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val period = schedule.days[day].periods[position]

        with(holder.mView) {
            tag = period
            setOnClickListener(mOnClickListener)
            periodTextView.text = period.getPeriodText()
            if (period.hasLocation())
                classTextView.text = resources.getString(R.string.class_text, period.className, period.classLocation)
            else
                classTextView.text = period.className
        }
    }

    override fun getItemCount(): Int = schedule.days[day].periods.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView)
}
