package org.secuso.pfacore.ui.activities

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.model.ErrorReport
import org.secuso.pfacore.model.ErrorReportHandler
import org.secuso.pfacore.ui.theme.PrivacyFriendlyCoreTheme
import org.secuso.ui.compose.R
import java.text.DateFormat
import java.util.Date

class ErrorReportActivity: BaseActivity() {

    private val selectedReports: SnapshotStateList<ErrorReport> = mutableStateListOf()

    @Composable
    override fun Actions() {
        if (selectedReports.isNotEmpty()) {
            IconButton(onClick = { PFApplication.instance.sendEmailErrorReport(selectedReports.toList()) }) {
                Icon(imageVector = Icons.Filled.Email, contentDescription = "E-Mail")
            }
        }
    }

    @Composable
    override fun Content(application: PFApplication) {
        val errors = application.getErrorReports()
        Box(Modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    if (selectedReports.isNotEmpty()) {
                        selectedReports.clear()
                    }
                }
            )
        }) {
            ErrorReportList(errors, selectedReports)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Log.d("debug", selectedReports.toString())
        if (selectedReports.isNotEmpty()) {
            selectedReports.clear()
        } else {
            super.onBackPressed()
        }
    }
}

@Composable
fun ErrorReportElement(errorReport: ErrorReportHandler, selected: Boolean) {
    val expanded = remember {
        mutableStateOf(false)
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = if (selected) { MaterialTheme.colorScheme.primaryContainer } else { MaterialTheme.colorScheme.surface }),
    ) {
        Column(modifier = Modifier.padding(all = 8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date(errorReport.report.unixTime)),
                    style = MaterialTheme.typography.titleSmall
                )
                Row {
                    if (!selected) {
                        IconButton(onClick = { errorReport.send() }) {
                            Icon(imageVector = Icons.Filled.Email, contentDescription = "E-Mail")
                        }
                    }
                    IconToggleButton(checked = expanded.value, onCheckedChange = {
                        expanded.value = !expanded.value
                    }) {
                        if (!expanded.value) {
                            Icon(painter = painterResource(id = R.drawable.baseline_expand_more_24), contentDescription = "Expand")
                        } else {
                            Icon(painter = painterResource(id = R.drawable.baseline_expand_less_24), contentDescription = "Collapse")
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth()) {
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = if (expanded.value) {
                        errorReport.report.trace
                    } else {
                        errorReport.report.trace.lines().take(3).joinToString(System.lineSeparator())
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewErrorReportElement() {
    val trace = "java.lang.IllegalStateException: This application was crashed on purpose!\n" +
            "\tat org.secuso.privacyfriendlyexample.ui.MainActivity.Content\$lambda\$4\$lambda\$3(MainActivity.kt:67)\n" +
            "\tat org.secuso.privacyfriendlyexample.ui.MainActivity.\$r8\$lambda\$9-ZpqTpTUqkAl743ouc1v33AN5Y(Unknown Source:0)\n" +
            "\tat org.secuso.privacyfriendlyexample.ui.MainActivity\$\$ExternalSyntheticLambda6.onClick(D8\$\$SyntheticClass:0)\n" +
            "\tat android.view.View.performClick(View.java:7542)\n" +
            "\tat android.view.View.performClickInternal(View.java:7519)\n" +
            "\tat android.view.View.-\$\$Nest\$mperformClickInternal(Unknown Source:0)\n" +
            "\tat android.view.View\$PerformClick.run(View.java:29476)\n" +
            "\tat android.os.Handler.handleCallback(Handler.java:942)\n" +
            "\tat android.os.Handler.dispatchMessage(Handler.java:99)\n" +
            "\tat android.os.Looper.loopOnce(Looper.java:201)\n" +
            "\tat android.os.Looper.loop(Looper.java:288)\n" +
            "\tat android.app.ActivityThread.main(ActivityThread.java:7924)\n" +
            "\tat java.lang.reflect.Method.invoke(Native Method)\n" +
            "\tat com.android.internal.os.RuntimeInit\$MethodAndArgsCaller.run(RuntimeInit.java:548)\n" +
            "\tat com.android.internal.os.ZygoteInit.main(ZygoteInit.java:936)"
    val selected = false
    ErrorReportElement(ErrorReportHandler(ErrorReport(System.currentTimeMillis(), trace), { Log.d("Preview", "Send report ${it.unixTime}")}, {Log.d("Preview", "Delete report ${it.unixTime}")}), selected)
}

@Composable
fun ErrorReportList(errorReports: List<ErrorReportHandler>, selectedReports: SnapshotStateList<ErrorReport>) {
    LazyColumn(Modifier.fillMaxWidth()) {
        items(count = errorReports.size) {
            val errorReport = errorReports[it]
            Box(Modifier.padding(horizontal = 8.dp, vertical = 4.dp).pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if (!selectedReports.contains(errorReport.report)) {
                            selectedReports.add(errorReport.report)
                        } else {
                            selectedReports.remove(errorReport.report)
                        }
                    },
                    onTap = {
                        if (selectedReports.isNotEmpty()) {
                            if (!selectedReports.contains(errorReport.report)) {
                                selectedReports.add(errorReport.report)
                            } else {
                                selectedReports.remove(errorReport.report)
                            }
                        }
                    }
                )}
            ) {
                ErrorReportElement(errorReport, selectedReports.contains(errorReport.report))
            }
        }
    }
}

@Preview
@Composable
fun PreviewErrorReportList() {
    val errors = mutableListOf(
        ErrorReportHandler(ErrorReport(System.currentTimeMillis(), "a"), { Log.d("Preview", "Send report ${it.unixTime}")}, {Log.d("Preview", "Delete report ${it.unixTime}")}),
        ErrorReportHandler(ErrorReport(System.currentTimeMillis() + 1, "b"), { Log.d("Preview", "Send report ${it.unixTime}")}, {Log.d("Preview", "Delete report ${it.unixTime}")}),
        ErrorReportHandler(ErrorReport(System.currentTimeMillis() + 2, "c"), { Log.d("Preview", "Send report ${it.unixTime}")}, {Log.d("Preview", "Delete report ${it.unixTime}")})
    )
    val selectedReports = remember {
        mutableStateListOf<ErrorReport>()
    }
    PrivacyFriendlyCoreTheme {
        ErrorReportList(errors, selectedReports)
    }
}