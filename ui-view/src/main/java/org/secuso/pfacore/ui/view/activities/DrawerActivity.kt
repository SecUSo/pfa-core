package org.secuso.pfacore.ui.view.activities

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import org.secuso.pfacore.R
import org.secuso.pfacore.model.Drawer
import org.secuso.pfacore.model.DrawerMenu
import org.secuso.ui.view.databinding.ActivityDrawerBinding
import org.secuso.ui.view.databinding.DrawerNavHeaderBinding


abstract class DrawerActivity: AppCompatActivity(), Drawer {
    private lateinit var drawerBinding: ActivityDrawerBinding

    override fun defaultDrawerSection(builder: DrawerMenu.Builder) {
        builder.apply {
            section {
                activity {
                    name = getString(R.string.nav_tutorial)
                    icon = R.drawable.ic_tutorial
                    clazz = TutorialActivity::class.java
                }
                activity {
                    name = getString(R.string.nav_help)
                    icon = R.drawable.ic_help
                    clazz = HelpActivity::class.java
                }
                activity {
                    name = getString(R.string.nav_settings)
                    icon = R.drawable.ic_settings
                    clazz = SettingsActivity::class.java
                }
                activity {
                    name = getString(R.string.nav_about)
                    icon = R.drawable.ic_info
                    clazz = AboutActivity::class.java
                }
            }
        }
    }

    private fun initContent() {
        drawerBinding = ActivityDrawerBinding.inflate(layoutInflater)
        super.setContentView(drawerBinding.root)

        setSupportActionBar(findViewById(org.secuso.ui.view.R.id.toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val drawer = drawer()
        val header = DrawerNavHeaderBinding.inflate(layoutInflater, null, false)
        header.name = drawer.name
        header.imageView.setImageResource(drawer.icon)
        drawerBinding.navView.addHeaderView(header.root)

        drawer.sections.forEachIndexed { index, section ->
            section.items.forEach {
                drawerBinding.navView.menu.add(index, Menu.NONE, Menu.NONE, it.name).apply {
                    if (it.icon != null) {
                        setIcon(it.icon!!)
                    }
                    if (isActiveDrawerElement(it)) {
                        isChecked = true
                    }
                    setOnMenuItemClickListener {  _ -> it.onClick(this@DrawerActivity); true }
                }
            }
        }
    }

    fun setContent(view: View) {
        initContent()
        drawerBinding.content.addView(view)
    }

    fun setContent(@LayoutRes layoutResID: Int) {
        initContent()
        drawerBinding.content.addView(layoutInflater.inflate(layoutResID, drawerBinding.content, false))
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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