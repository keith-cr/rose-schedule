package edu.rosehulman.condrak.roseschedule

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.rosehulman.condrak.roseschedule.EditDayActivityFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_edit_day.view.*

class EditDayActivityRecyclerViewAdapter(
    private val schedule: Schedule,
    scheduleTiming: ScheduleTiming,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<EditDayActivityRecyclerViewAdapter.ViewHolder>() {
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Int
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_edit_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = schedule.days[position]

        with(holder.mView) {
            tag = position
            setOnClickListener(mOnClickListener)
            dayTextView.text = day.name
        }
    }

    override fun getItemCount(): Int = schedule.days.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView)
}
