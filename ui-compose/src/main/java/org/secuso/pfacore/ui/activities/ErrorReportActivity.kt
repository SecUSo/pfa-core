package org.secuso.pfacore.ui.activities

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.model.ErrorReport
import org.secuso.pfacore.model.ErrorReportHandler
import org.secuso.ui.compose.R
import java.text.DateFormat
import java.util.Date

class ErrorReportActivity: BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) {
        val errors = application.getErrorReports()
        if (errors.isEmpty()) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "No Error Reports available :D")
            }
        } else {
            LazyColumn(Modifier.fillMaxWidth()) {
                items(count = errors.size) {
                    ErrorReportElement(errors[it])
                }
            }
        }
    }
}

@Composable
fun ErrorReportElement(errorReport: ErrorReportHandler) {
    val expanded = remember {
        mutableStateOf(false)
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(all = 8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date(errorReport.report.unixTime)),
                    style = MaterialTheme.typography.titleSmall
                )
                Row {
                    IconButton(onClick = { errorReport.send() }) {
                        Icon(imageVector = Icons.Filled.Email, contentDescription = "E-Mail")
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
                        errorReport.report.trace.lines().slice(0..2).joinToString(System.lineSeparator())
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
    ErrorReportElement(ErrorReportHandler(ErrorReport(System.currentTimeMillis(), trace), { Log.d("Preview", "Send report ${it.unixTime}")}, {Log.d("Preview", "Delete report ${it.unixTime}")}))
}