package org.secuso.pfacore.model.about

/**
 * Provide the necessary Information to build the About-Section for a PFA.
 * @param name The name of the app to be shown
 * @param version The version name of the app
 * @param authors A String containing all authors
 * @param repo The url the repo is located at
 *
 * @author Patrick Schneider
 */
data class About(
    val name: String,
    val version: String,
    val authors: String,
    val repo: String
)