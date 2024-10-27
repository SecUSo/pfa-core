package org.secuso.pfacore.ui.activities

import android.os.Bundle
import org.secuso.pfacore.ui.PFApplication
import org.secuso.ui.view.databinding.ActivityHelpBinding

class HelpActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = PFApplication.instance.data.help
        val binding = ActivityHelpBinding.inflate(layoutInflater)
        binding.recyclerView.adapter = data!!.build(layoutInflater, this)
        setContentView(binding.root)
    }
}