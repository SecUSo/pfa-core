package org.secuso.pfacore.ui.help

import org.secuso.pfacore.model.help.IHelpData
import org.secuso.pfacore.ui.Inflatable

data class HelpData(val title: Inflatable, val summary: Inflatable): IHelpData<HelpData>