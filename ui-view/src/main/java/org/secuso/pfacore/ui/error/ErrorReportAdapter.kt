package org.secuso.pfacore.ui.error

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.secuso.pfacore.R
import org.secuso.pfacore.model.ErrorReportHandler
import org.secuso.ui.view.databinding.ErrorReportItemBinding
import java.text.DateFormat
import java.util.Date

class ErrorReportAdapter(private val inflater: LayoutInflater, private var errorReports: List<ErrorReportHandler> = listOf()): RecyclerView.Adapter<ErrorReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ErrorReportItemBinding.inflate(inflater, parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setErrorReports(errorReports: List<ErrorReportHandler>) {
        this.errorReports = errorReports
        this.notifyDataSetChanged()
    }

    override fun getItemCount() = errorReports.size

    fun getErrorReportAt(index: Int) = errorReports[index]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val errorReport = errorReports[position]
        var expanded = false
        holder.binding.title = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date(errorReport.report.unixTime))
        holder.binding.text = errorReport.report.trace.lines().slice(0..2).joinToString("\n")
        holder.binding.toggle.setImageResource(R.drawable.baseline_expand_more_24)
        holder.binding.toggle.setOnClickListener {
            if (expanded) {
                holder.binding.text = errorReport.report.trace.lines().slice(0..2).joinToString("\n")
                holder.binding.toggle.setImageResource(R.drawable.baseline_expand_more_24)
            } else {
                holder.binding.text = errorReport.report.trace
                holder.binding.toggle.setImageResource(R.drawable.baseline_expand_less_24)
            }
            expanded = !expanded
        }
        holder.binding.email.setOnClickListener { errorReport.send() }
    }

    class ViewHolder(val binding: ErrorReportItemBinding): RecyclerView.ViewHolder(binding.root) {

    }
}