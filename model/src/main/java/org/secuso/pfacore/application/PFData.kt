package org.secuso.pfacore.application

import androidx.lifecycle.LiveData
import org.secuso.pfacore.model.Theme
import org.secuso.pfacore.model.about.About
import org.secuso.pfacore.model.help.Help
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.pfacore.model.preferences.settings.ISettings
import org.secuso.pfacore.model.tutorial.Tutorial

/**
 * This class provides the necessary data to build the empty-shell for a PFA.
 * It is intended as a single-source-of-truth for the application data, such as settings, preference, items in the help-section, ... .
 *
 * The recommended way to use this class is in conjunction with a PFApplicationData class, which is a singleton, as follows in the example below.
 * This ensures availability of the settings and preferences with enough flexibility for every app whilst building the data only once.
 *
 *      class PFApplicationData private constructor(context: Context) {
 *
 *          lateinit var theme: ISettingData<String>
 *              private set
 *          lateinit var firstTimeLaunch: Preferable<Boolean>
 *              private set
 *          lateinit var includeDeviceDataInReport: Preferable<Boolean>
 *              private set
 *
 *          // Add other preferences and settings as needed
 *
 *          private val preferences = appPreferences(context) {
 *              preferences {
 *                  // firstTimeLaunch is an extension provided by the ui-* libraries
 *                  firstTimeLaunch = firstTimeLaunch
 *              }
 *              settings {
 *                  // appearance is also an extension to directly build an appearance section.
 *                  appearance {
 *                      theme = SettingThemeSelector().build().invoke(this)
 *                  }
 *                  category("Error Report") {
 *                      // also an extension
 *                      includeDeviceDataInReport = deviceInformationOnErrorReport
 *                  }
 *              }
 *          }
 *
 *          private val help = Help.build(context) {
 *              item {
 *                  title { resource(R.string.help_whatis) }
 *                  description { resource(R.string.help_whatis_answer) }
 *              }
 *              item {
 *                  title { resource(R.string.help_feature_one) }
 *                  description { resource(R.string.help_feature_one_answer) }
 *              }
 *          }
 *
 *          private val about = About(
 *              name = context.resources.getString(R.string.app_name),
 *              version = BuildConfig.VERSION_NAME,
 *              authors = context.resources.getString(R.string.about_author_names),
 *              repo = context.resources.getString(R.string.repo_url)
 *          )
 *
 *          private val tutorial = buildTutorial {
 *              stage {
 *                  title = context.getString(R.string.slide1_heading)
 *                  images = listOf(R.mipmap.ic_splash)
 *                  description = context.getString(R.string.slide1_text)
 *              }
 *              stage {
 *                  title = context.getString(R.string.slide2_heading)
 *                  images = listOf(R.mipmap.ic_splash)
 *                  description = context.getString(R.string.slide2_text)
 *              }
 *          }
 *
 *          val data = PFData(
 *              about = about,
 *              help = help,
 *              settings = preferences.settings,
 *              tutorial = tutorial,
 *              theme = theme.state.map { Theme.valueOf(it) },
 *              firstLaunch = firstTimeLaunch,
 *              includeDeviceDataInReport = includeDeviceDataInReport,
 *          )
 *
 *          companion object {
 *              private var _instance: PFApplicationData? = null
 *              fun instance(context: Context): PFApplicationData {
 *                  if (_instance == null) {
 *                      _instance = PFApplicationData(context)
 *                  }
 *                  return _instance!!
 *              }
 *          }
 *      }
 *
 * @see Help
 * @see ISettings
 * @see org.secuso.pfacore.model.preferences.settings.Settings
 * @see Tutorial
 *
 * @author Patrick Schneider
 */
data class PFData<SD: ISettings<*>, HD: Help<*>, TD: Tutorial<*>>(
    val settings: SD,
    val about: About,
    val help: HD? = null,
    val tutorial: TD,
    val theme: LiveData<Theme>,
    val firstLaunch: Preferable<Boolean>,
    val includeDeviceDataInReport: Preferable<Boolean>
)