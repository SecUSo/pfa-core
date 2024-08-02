package org.secuso.pfacore.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.application.PFApplication
import org.secuso.ui.view.databinding.ActivityHelpBinding
import org.secuso.pfacore.ui.help.Help

class HelpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = PFApplication.instance.data.help
        if (data !is Help) {
            throw IllegalStateException("The application help data is of type ${data::class.java} but expected ${Help::class.java}")
        }
        val binding = ActivityHelpBinding.inflate(layoutInflater)
        binding.recyclerView.adapter = data.build(layoutInflater, this)
        setContentView(binding.root)
    }
}