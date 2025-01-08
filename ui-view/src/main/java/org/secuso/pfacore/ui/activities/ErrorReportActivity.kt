package org.secuso.pfacore.ui.activities

import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.ui.PFApplication
import org.secuso.pfacore.ui.dialog.show
import org.secuso.pfacore.ui.error.ErrorReportAdapter
import org.secuso.ui.view.R
import org.secuso.pfacore.R as CoreR
import org.secuso.ui.view.databinding.ActivityErrorReportBinding

class ErrorReportActivity: BaseActivity() {

    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var binding: ActivityErrorReportBinding
    private lateinit var adapter: ErrorReportAdapter
    private val provider by lazy {
        ErrorReportAdapter.ErrorReportKeyProvider(adapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = PFApplication.instance
        adapter = ErrorReportAdapter(this, layoutInflater) {
            AbortElseDialog.build(this) {
                title = { ContextCompat.getString(this@ErrorReportActivity, CoreR.string.dialog_report_sensitive_information_title) }
                content = { ContextCompat.getString(this@ErrorReportActivity, CoreR.string.dialog_report_sensitive_information_content) }
                acceptLabel = ContextCompat.getString(context, org.secuso.pfacore.R.string.dialog_button_understood)
                onElse = {
                    it.send()
                }
            }.show()
        }
        binding = ActivityErrorReportBinding.inflate(layoutInflater)
        tracker = SelectionTracker.Builder<Long>(
            "error-report",
            binding.errorReports,
            provider,
            ErrorReportAdapter.ErrorReportDetailsLookup(binding.errorReports),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()
        adapter.tracker = tracker

        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                invalidateOptionsMenu()
            }
        })

        CoroutineScope(Dispatchers.Default).launch {
            val reports = application.getErrorReports()
            withContext(Dispatchers.Main) {
                adapter.setErrorReports(reports)
                if (reports.isEmpty()) {
                    binding.emptyText.visibility = View.VISIBLE
                    binding.errorReports.visibility = View.GONE
                } else {
                    binding.emptyText.visibility = View.GONE
                    binding.errorReports.visibility = View.VISIBLE
                }
            }
        }
        val ith = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.getErrorReportAt(viewHolder.adapterPosition).delete()
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        })
        ith.attachToRecyclerView(binding.errorReports)
        binding.errorReports.adapter = adapter

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report_error, menu)
        menu.findItem(R.id.email)?.let {
            it.isVisible = tracker.hasSelection()
            it.setOnMenuItemClickListener {
                AbortElseDialog.build(this) {
                    title = { ContextCompat.getString(this@ErrorReportActivity, CoreR.string.dialog_report_sensitive_information_title) }
                    content = { ContextCompat.getString(this@ErrorReportActivity, CoreR.string.dialog_report_sensitive_information_content) }
                    acceptLabel = ContextCompat.getString(context, org.secuso.pfacore.R.string.dialog_button_understood)
                    onElse = {
                        PFApplication.instance.sendEmailErrorReport(tracker.selection.map { id -> adapter.getErrorReportAt(provider.getPosition(id)).report })
                    }
                }.show()
                true
            }
        }

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val rect = Rect().apply {
            binding.errorReports.getGlobalVisibleRect(this)
        }

        if (!rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            tracker.clearSelection()
            return true
        }
        return super.onTouchEvent(event)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (tracker.hasSelection()) {
            tracker.clearSelection()
        } else {
            super.onBackPressed()
        }
    }
}