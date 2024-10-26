package org.secuso.pfacore.model

data class ErrorReport(val unixTime: Long, val trace: String) {
    override fun hashCode() = unixTime.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other is ErrorReport) {
            return other.unixTime == this.unixTime
        }
        return false
    }
}

class ErrorReportHandler(val report: ErrorReport, private val sendReport: (ErrorReport) -> Unit, private val deleteReport: (ErrorReport) -> Unit) {
    fun send() {
        sendReport(report)
    }
    fun delete() {
        deleteReport(report)
    }

    override fun equals(other: Any?): Boolean {
        if (other is ErrorReportHandler) {
            return report == other.report
        }
        return false
    }
    override fun hashCode() = report.hashCode()
}
