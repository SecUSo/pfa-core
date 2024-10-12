package org.secuso.pfacore.ui.error

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import org.secuso.pfacore.R
import org.secuso.pfacore.model.ErrorReportHandler
import org.secuso.ui.view.databinding.ErrorReportItemBinding
import java.text.DateFormat
import java.util.Date

class ErrorReportAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private var errorReports: List<ErrorReportHandler> = listOf(),
): RecyclerView.Adapter<ErrorReportAdapter.ViewHolder>() {

    internal var tracker: SelectionTracker<Long>? = null
    private val unselectedColor = TypedValue().apply {
        context.theme.resolveAttribute(R.attr.colorSurface, this, true)
    }.data
    private val selectedColor = TypedValue().apply {
        context.theme.resolveAttribute(R.attr.colorPrimaryContainer, this, true)
    }.data

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ErrorReportItemBinding.inflate(inflater, parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setErrorReports(errorReports: List<ErrorReportHandler>) {
        this.errorReports = errorReports
        this.notifyDataSetChanged()
    }
    fun getErrorReportAt(index: Int) = errorReports[index]

    override fun getItemCount() = errorReports.size

    override fun getItemId(position: Int): Long = errorReports[position].report.unixTime

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val errorReport = errorReports[position]
        var expanded = false
        holder.binding.title = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date(errorReport.report.unixTime))
        holder.binding.text = errorReport.report.trace.lines().take(3).joinToString("\n")
        holder.binding.toggle.setImageResource(R.drawable.baseline_expand_more_24)
        holder.binding.toggle.setOnClickListener {
            if (expanded) {
                holder.binding.text = errorReport.report.trace.lines().take(3).joinToString("\n")
                holder.binding.toggle.setImageResource(R.drawable.baseline_expand_more_24)
            } else {
                holder.binding.text = errorReport.report.trace
                holder.binding.toggle.setImageResource(R.drawable.baseline_expand_less_24)
            }
            expanded = !expanded
        }
        holder.binding.email.setOnClickListener { errorReport.send() }

        if (tracker?.isSelected(errorReport.report.unixTime) == true) {
            holder.binding.card.setCardBackgroundColor(selectedColor)
            holder.binding.selected = false
        } else {
            holder.binding.card.setCardBackgroundColor(unselectedColor)
            holder.binding.selected = true
        }
    }

    inner class ViewHolder(val binding: ErrorReportItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition() = adapterPosition
            override fun getSelectionKey() = errorReports[adapterPosition].report.unixTime
        }
    }
    class ErrorReportKeyProvider(private val adapter: ErrorReportAdapter): ItemKeyProvider<Long>(SCOPE_CACHED) {
        override fun getKey(position: Int) = adapter.errorReports[position].report.unixTime
        override fun getPosition(key: Long) = adapter.errorReports.indexOfFirst { it.report.unixTime == key }
    }

    class ErrorReportDetailsLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            return if (view != null) {
                (recyclerView.getChildViewHolder(view) as ViewHolder).getItemDetails()
            } else {
                null
            }
        }
    }
}