package org.secuso.pfacore.ui.error

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.secuso.pfacore.R
import org.secuso.pfacore.model.ErrorReportHandler
import org.secuso.ui.view.databinding.ErrorReportItemBinding
import java.text.DateFormat
import java.util.Date

class ErrorReportAdapter(private val inflater: LayoutInflater, private val errorReports: List<ErrorReportHandler>): RecyclerView.Adapter<ErrorReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ErrorReportItemBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = errorReports.size

    fun getErrorReportAt(index: Int) = errorReports[index]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val errorReport = errorReports[position]
        var expanded = false
        holder.binding.title = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date(errorReport.report.unixTime))
        holder.binding.text = errorReport.report.trace
        holder.binding.toggle.setOnClickListener {
            if (expanded) {
                holder.binding.lines = 3
                holder.binding.toggle.setImageResource(R.drawable.baseline_expand_more_24)
            } else {
                holder.binding.lines = -1
                holder.binding.toggle.setImageResource(R.drawable.baseline_expand_less_24)
            }
            expanded = !expanded
        }
        holder.binding.email.setOnClickListener { errorReport.send() }
    }

    class ViewHolder(val binding: ErrorReportItemBinding): RecyclerView.ViewHolder(binding.root) {

    }
}