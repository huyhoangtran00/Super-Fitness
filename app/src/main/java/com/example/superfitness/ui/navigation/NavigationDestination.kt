package com.example.superfitness.ui.navigation

/**
 * Interface that describe the navigation destination for the app
 */
interface NavigationDestination {
    /**
     * Unique name to define the route for a composable
     */
    val route: String

    /**
     * String resource id to that contains the title to be displayed for the screen
     */
    val titleRes: Int
}