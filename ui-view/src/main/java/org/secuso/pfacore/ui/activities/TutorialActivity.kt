package org.secuso.pfacore.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import org.secuso.pfacore.R
import org.secuso.pfacore.activities.SplashActivity
import org.secuso.pfacore.ui.PFApplication
import org.secuso.pfacore.ui.tutorial.Tutorial
import org.secuso.ui.view.databinding.ActivityTutorialBinding

class TutorialActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.navigationBars())
        }
        val tutorial = PFApplication.instance.data.tutorial
        tutorial.onFinish = {
            val activity: Class<out Activity>? = tutorial.launchActivity ?: run {
                if (intent.extras?.getBoolean(SplashActivity.EXTRA_LAUNCH_MAIN_ACTIVITY_AFTER_TUTORIAL) == true) {
                    PFApplication.instance.mainActivity
                } else {
                    null
                }
            }
            if (activity != null) {
                startActivity(tutorial.extras(Intent(this@TutorialActivity, activity)))
            }
            finish()
        }

        val adapter = StagePagerAdapter(tutorial, layoutInflater, this)

        val binding = ActivityTutorialBinding.inflate(layoutInflater)
        val colorInactive = R.color.secusoDotListInactive
        val colorActive = R.color.secusoDotListActive
        val dots = tutorial.stages.indices.map {
            TextView(this).apply {
                // Draws a colored circle
                text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
                textSize = 35f
                setTextColor(resources.getColor(if (it == 0) colorActive else colorInactive))
                binding.layoutDots.addView(this)
            }
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                if (position == tutorial.stages.size - 1) {
                    binding.btnNext.text = getString(R.string.tutorial_finish)
                    binding.btnSkip.visibility = View.GONE
                } else {
                    binding.btnNext.text = getString(R.string.tutorial_next)
                    binding.btnSkip.visibility = View.VISIBLE
                }
                dots.forEachIndexed { index, dot ->
                    dot.setTextColor(resources.getColor(if (index == position) colorActive else colorInactive))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
        })
        binding.btnSkip.setOnClickListener { tutorial.onFinish() }
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < tutorial.stages.size) {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1)
            } else {
                tutorial.onFinish()
            }
        }
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
    }

    class StagePagerAdapter(private val tutorial: Tutorial, private val inflater: LayoutInflater, private val owner: LifecycleOwner) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): View {
            return tutorial.stages[position].inflate(inflater, container, owner).apply {
                container.addView(this)
            }
        }

        override fun getCount() = tutorial.stages.size

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}