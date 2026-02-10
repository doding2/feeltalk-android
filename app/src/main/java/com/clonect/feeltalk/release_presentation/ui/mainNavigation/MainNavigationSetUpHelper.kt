package com.clonect.feeltalk.release_presentation.ui.mainNavigation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationBarView
import java.lang.ref.WeakReference

fun NavigationBarView.setupWithMainNavController(navController: NavController) {
    NavigationUI.setupWithMainNavController(this, navController)
}

private fun NavigationUI.setupWithMainNavController(
    navigationBarView: NavigationBarView,
    navController: NavController,
) {
    navigationBarView.setOnItemSelectedListener { item ->
        onMainNavDestinationSelected(
            item,
            navController
        )
    }
    val weakReference = WeakReference(navigationBarView)
    navController.addOnDestinationChangedListener(
        object : NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                val view = weakReference.get()
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this)
                    return
                }
                view.menu.forEach { item ->
                    if (destination.matchDestination(item.itemId)) {
                        item.isChecked = true
                    }
                }
            }
        })
}

private fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
    hierarchy.any { it.id == destId }

private fun onMainNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
    val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
//    if (
//        navController.currentDestination!!.parent!!.findNode(item.itemId)
//                is ActivityNavigator.Destination
//    ) {
//        builder.setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
//            .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
//            .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
//            .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
//    } else {
//        builder.setEnterAnim(androidx.navigation.ui.R.animator.nav_default_enter_anim)
//            .setExitAnim(androidx.navigation.ui.R.animator.nav_default_exit_anim)
//            .setPopEnterAnim(androidx.navigation.ui.R.animator.nav_default_pop_enter_anim)
//            .setPopExitAnim(androidx.navigation.ui.R.animator.nav_default_pop_exit_anim)
//    }
    if (item.order and Menu.CATEGORY_SECONDARY == 0) {
        builder.setPopUpTo(
            navController.graph.findStartDestination().id,
            inclusive = false,
            saveState = true
        )
    }
    val options = builder.build()
    return try {
        // TODO provide proper API instead of using Exceptions as Control-Flow.
        navController.navigate(item.itemId, null, options)
        // Return true only if the destination we've navigated to matches the MenuItem
        navController.currentDestination?.matchDestination(item.itemId) == true
    } catch (e: IllegalArgumentException) {
        false
    }
}