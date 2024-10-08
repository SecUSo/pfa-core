package org.secuso.pfacore.model

data class ErrorReport(val unixTime: Long, val trace: String)

class ErrorReportHandler(val report: ErrorReport, private val sendReport: (ErrorReport) -> Unit, private val deleteReport: (ErrorReport) -> Unit) {
    fun send() {
        sendReport(report)
    }
    fun delete() {
        deleteReport(report)
    }
}