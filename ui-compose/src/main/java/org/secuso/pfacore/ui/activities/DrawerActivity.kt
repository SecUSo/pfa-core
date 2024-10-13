package org.secuso.pfacore.ui.activities


import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.secuso.pfacore.R
import org.secuso.pfacore.application.PFApplication
import org.secuso.pfacore.model.Drawer
import org.secuso.pfacore.model.DrawerElement
import org.secuso.pfacore.model.DrawerMenu
import org.secuso.pfacore.ui.theme.secuso

abstract class DrawerActivity : AppCompatActivity(), Drawer {

    @Composable
    abstract fun Content(application: PFApplication)

    protected val title: MutableState<String> = mutableStateOf(PFApplication.instance.name)

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
                    name = getString(R.string.nav_bugs)
                    icon = R.drawable.ic_bugs
                    clazz = ErrorReportActivity::class.java
                }
                activity {
                    name = getString(R.string.nav_about)
                    icon = R.drawable.ic_info
                    clazz = AboutActivity::class.java
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = PFApplication.instance
        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val drawer = remember { drawer() }

            WithTheme {
                window.statusBarColor = MaterialTheme.colorScheme.secuso.toArgb()
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            DrawerMenuComp(drawer) { isActiveDrawerElement(it) }
                        }
                    },
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(title, { scope.launch { drawerState.apply { if (isClosed) open() else close() } } }) {
                                Icon(imageVector = Icons.Filled.Menu, contentDescription = if (drawerState.isClosed) "open menu" else "close menu" , tint = Color.White)
                            }
                        }
                    ) {
                        Box(modifier = Modifier.padding(it)) {
                            Content(application)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerHeader(drawer: DrawerMenu) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(MaterialTheme.colorScheme.secuso)) {
        Box(
            Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
                .background(Color.White)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)) {
            Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Image(painter = painterResource(drawer.icon), contentDescription = drawer.name)
                Column(Modifier.height(IntrinsicSize.Max).align(Alignment.CenterVertically), verticalArrangement = Arrangement.Center) {
                    Box(Modifier.padding(start = 16.dp)) {
                        Text(text = drawer.name, color = MaterialTheme.colorScheme.secuso, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerMenuComp(drawer: DrawerMenu, selected: (DrawerElement) -> Boolean) {
    val context = LocalContext.current
    DrawerHeader(drawer)
    for (section in drawer.sections) {
        for (item in section.items) {
            NavigationDrawerItem(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                label = { Text(item.name) },
                icon = if (item.icon != null) { -> Icon(painter = painterResource(item.icon!!), contentDescription = item.name) } else null,
                onClick = { if (context is Activity) item.onClick(context) },
                colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer),
                selected = selected(item)
            )
        }
        if (section != drawer.sections.last()) {
            HorizontalDivider(modifier = Modifier.padding(all = 8.dp))
        }
    }
}

@Preview
@Composable
fun DrawerPreview() {
    val drawer = DrawerMenu.build {
        name = "Beispiel"
        icon = R.drawable.ic_info
        section {
            action {
                name = "test"
                onClick = {}
            }
        }
        section {
            activity {
                name = "Help"
                icon = R.drawable.ic_help
                clazz = HelpActivity::class.java
            }
            activity {
                name = "Settings"
                icon = R.drawable.ic_settings
                clazz = SettingsActivity::class.java
            }
            activity {
                name = "Über"
                icon = R.drawable.ic_info
                clazz = AboutActivity::class.java
            }
        }
    }
    DrawerMenuComp(drawer) { it.name == "test" }
}