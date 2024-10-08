package org.secuso.pfacore.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.ui.error.ErrorReportAdapter
import org.secuso.ui.view.databinding.ActivityErrorReportBinding

class ErrorReportActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = PFApplication.instance
        val binding = ActivityErrorReportBinding.inflate(layoutInflater)
        val adapter = ErrorReportAdapter(layoutInflater, application.getErrorReports())

        val ith = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.getErrorReportAt(viewHolder.adapterPosition).delete()
            }
        })
        ith.attachToRecyclerView(binding.errorReports)
        binding.errorReports.adapter = adapter
    }
}