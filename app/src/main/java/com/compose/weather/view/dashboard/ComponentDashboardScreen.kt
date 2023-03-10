package com.compose.weather.view.dashboard

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.compose.weather.R
import com.compose.weather.common.empty
import com.compose.weather.navigtion.HomeNavigation
import com.compose.weather.navigtion.Route
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ComponentDashboardScreen(
    username: String,
    logout: () -> Unit
) {

    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val navItems = remember {
        listOf(
            BottomNavItem(
                title = "Home",
                route = Route.HomeNav.Home.route,
                icon = Icons.Rounded.Home,
                header = "Hello $username",
            ),
            BottomNavItem(
                title = "Settings",
                route = Route.HomeNav.Settings.route,
                icon = Icons.Rounded.Settings,
            )
        )
    }
    // Show topBar, drawerContent & bottomBar for only these screens
    val topBottomBarDrawer = remember {
        listOf(
            Route.HomeNav.Home.route,
            Route.HomeNav.Settings.route
        )
    }
    val backStackEntry = navController.currentBackStackEntryAsState()
    val current = backStackEntry.value?.destination?.route
    Log.d("route", "Route $current")

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (topBottomBarDrawer.contains(current)) {
                val title = navItems.find { it.route == current }?.header
                TopBar(title) {
                    coroutineScope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            }
        },
        drawerContent = {
            if (topBottomBarDrawer.contains(current)) {
                DrawerContent { route ->
                    coroutineScope.launch {
                        // delay for the ripple effect
                        delay(timeMillis = 250)
                        scaffoldState.drawerState.close()
                        navController.navigate(route)
                    }
                }
            }
        },
        bottomBar = {
            if (topBottomBarDrawer.contains(current)) {
                BottomBar(navItems, backStackEntry, navController)
            }
        }
    ) { paddingValues ->
        HomeNavigation(navController, paddingValues, username) {
            logout.invoke()
        }
    }
}

@Composable
private fun TopBar(title: String?, onNavIconClick: () -> Unit) {
    TopAppBar(
        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
        title = { Text(text = title ?: String.empty()) },
        navigationIcon = {
            IconButton(
                onClick = {
                    onNavIconClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Open Drawer"
                )
            }
        }
    )
}

@Composable
private fun DrawerContent(onItemClick: (route: String) -> Unit) {
    val list by remember {
        mutableStateOf(listOf(R.string.profile, R.string.about))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Profile Pic",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Cyan)
        )
    }

    list.forEach { string ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable {
                    if (string == R.string.profile) {
                        onItemClick.invoke(Route.HomeNav.Profile.route)
                    } else if (string == R.string.about) {
                        onItemClick.invoke(Route.HomeNav.About.route)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = string),
                fontSize = 18.sp
            )
        }
        Divider()
    }
}

@Composable
private fun BottomBar(
    navItems: List<BottomNavItem>,
    backStackEntry: State<NavBackStackEntry?>,
    navController: NavHostController
) {
    NavigationBar {
        navItems.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        // To avoid calling same destination multiple times
                        launchSingleTop = true
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "${item.title} Icon",
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ComponentDashboardScreen("") {}
}