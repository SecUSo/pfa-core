package org.secuso.ui.view.help

import org.secuso.pfacore.model.help.IHelpData
import org.secuso.ui.view.Inflatable

data class HelpData(val title: Inflatable, val summary: Inflatable): IHelpData<HelpData>