package org.secuso.ui.view.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.application.PFApplication
import org.secuso.ui.view.databinding.ActivityHelpBinding
import org.secuso.ui.view.help.Help
import org.secuso.ui.view.help.HelpData

class HelpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val data = PFApplication.instance.data.help
        if (data !is Help) {
            throw IllegalStateException("The application help data is of type ${data::class.java} but expected ${Help::class.java}")
        }
        val binding = ActivityHelpBinding.inflate(layoutInflater)
        binding.recyclerView.adapter = data.build(layoutInflater)
    }
}