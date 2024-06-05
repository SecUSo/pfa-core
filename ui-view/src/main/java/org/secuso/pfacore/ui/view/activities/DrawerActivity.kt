package org.secuso.pfacore.ui.view.activities

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.model.DrawerMenu
import org.secuso.pfacore.R
import org.secuso.pfacore.model.DrawerElement
import org.secuso.ui.view.databinding.ActivityDrawerBinding
import org.secuso.ui.view.databinding.DrawerNavHeaderBinding
import org.secuso.pfacore.model.ActivityDrawerElement as ActivityDrawerElement
import org.secuso.pfacore.model.DrawerSection as MDrawerSection


abstract class DrawerActivity: AppCompatActivity() {
    protected var activeDrawerElement: DrawerElement? = null
    private val drawerBinding: ActivityDrawerBinding by lazy {
        ActivityDrawerBinding.inflate(layoutInflater)
    }

    fun setContent(@LayoutRes layoutResID: Int, @DrawableRes imgRes: Int, menu: DrawerMenu) {
        super.setContentView(drawerBinding.root)

        setSupportActionBar(drawerBinding.toolbarWrapper.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val header = DrawerNavHeaderBinding.inflate(layoutInflater, null, false)
        header.name = PFApplication.instance.name
        header.imageView.setImageResource(imgRes)
        drawerBinding.navView.addHeaderView(header.root)
        val standardMenu = MDrawerSection(listOf(
            ActivityDrawerElement(getString(R.string.nav_settings), R.drawable.ic_settings, SettingsActivity::class.java),
            ActivityDrawerElement(getString(R.string.nav_help), R.drawable.ic_help, HelpActivity::class.java),
            ActivityDrawerElement(getString(R.string.nav_about), R.drawable.ic_info, AboutActivity::class.java)
        ));
        val sections = menu.sections + standardMenu
        sections.forEachIndexed { index, section ->
            section.items.forEach {
                drawerBinding.navView.menu.add(index, Menu.NONE, Menu.NONE, it.name).apply {
                    if (it.icon != null) {
                        setIcon(it.icon!!)
                    }
                    if (it == activeDrawerElement) {
                        isChecked = true
                    }
                    setOnMenuItemClickListener {  _ -> it.onClick(this@DrawerActivity); true }
                }
            }
        }

        drawerBinding.content.addView(layoutInflater.inflate(layoutResID, drawerBinding.content, false))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerBinding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}