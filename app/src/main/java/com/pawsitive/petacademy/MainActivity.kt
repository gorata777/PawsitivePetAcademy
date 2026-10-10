package com.pawsitive.petacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PawTheme { PawApp() } }
    }
}

@Composable
fun PawApp() {
    val nav = rememberNavController()
    val state = remember { AppState() }
    NavHost(nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(nav, state) }                       // Mobile 01
        composable(Routes.ABOUT) { AboutScreen(nav) }                            // Mobile 02
        composable(Routes.COURSES) { CoursesScreen(nav, state) }                 // Mobile 03
        composable(Routes.DETAILS) { CourseDetailsScreen(nav, state) }           // Mobile 04
        composable(Routes.FEES) { CalculateFeesScreen(nav) }                     // Mobile 05
        composable(Routes.REGISTER) { RegistrationScreen(nav, state) }           // Mobile 06
        composable(Routes.SUCCESS) { RegistrationSuccessScreen(nav, state) }     // Mobile 07
        composable(Routes.MY_COURSES) { MyCoursesScreen(nav, state) }            // Mobile 08
        composable(Routes.PROGRESS) { CourseProgressScreen(nav, state) }         // Mobile 09
        composable(Routes.PROFILE) { ProfileScreen(nav, state) }                 // Mobile 10
        composable(Routes.CONTACT) { ContactScreen(nav, state) }                 // Mobile 11
        composable(Routes.MENU) { MenuScreen(nav) }                              // Mobile 12
    }
}
