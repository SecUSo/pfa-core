package org.secuso.pfacore.ui.activities

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.secuso.pfacore.application.PFApplication
import java.util.Date

class ErrorReportActivity: BaseActivity() {

    @Composable
    override fun Content(application: PFApplication) {
        val errors = application.getErrorReports()
        LazyColumn(Modifier.fillMaxWidth()) {
            items(count = errors.size) {
                val error = errors[it]
                Card(colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )) {
                    Text(text = android.text.format.DateFormat.getDateFormat(application.applicationContext).format(Date(error.unixTime)))
                    Text(text = error.trace)
                }
            }
        }
    }
}