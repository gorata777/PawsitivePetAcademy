package com.pawsitive.petacademy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun StudentCard(state: AppState) {
    PawCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(state.studentName)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(state.studentName, color = Forest, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Body(state.studentEmail); Body(state.studentPhone)
            }
        }
        Chip("Student · Example account")
    }
}

@Composable
fun Avatar(name: String) {
    val initials = name.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    Box(Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(Sage), contentAlignment = Alignment.Center) {
        Text(initials, color = Forest, fontWeight = FontWeight.Bold)
    }
}

/* ---------- Mobile 08 · My Courses ---------- */
@Composable
fun MyCoursesScreen(nav: NavHostController, state: AppState) {
    val active = Repo.enrollments.filter { it.progress < 100 }
    PawScaffold(nav, Routes.MY_COURSES) {
        Text("Welcome back, ${state.studentName.substringBefore(' ')}", color = Forest, fontWeight = FontWeight.Bold, fontSize = 26.sp)
        Body("Keep growing your pet-care confidence.")
        StudentCard(state)
        Text("My Courses", color = Forest, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        active.forEach { e ->
            val c = Repo.course(e.courseId)
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White)) {
                PhotoBox(c.name, 120.dp, c.image, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip("Enrolled · Online learning")
                    Text(c.name, color = Forest, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    ProgressRow("Overall progress", e.progress)
                    PrimaryButton("Continue Course", { state.selectedCourse = c; nav.go(Routes.PROGRESS) }, arrow = true)
                }
            }
        }
        Body("${active.size} active courses · ${Repo.enrollments.size - active.size} completed course")
    }
}

/* ---------- Mobile 09 · Course Progress ---------- */
@Composable
fun CourseProgressScreen(nav: NavHostController, state: AppState) {
    val c = state.selectedCourse
    val progress = Repo.enrollments.firstOrNull { it.courseId == c.id }?.progress ?: 0
    val done = progress * c.modules.size / 100
    PawScaffold(nav) {
        ScreenTitle(c.name.replace("Canine Obedience Training", "Dog Training Basics"), "Your learning journey")
        PhotoBox(c.name, 150.dp, c.image)
        PawCard {
            CardTitle("Course Progress")
            ProgressRow("Overall progress", progress)
            Body("$done of ${c.modules.size} modules completed.${c.modules.getOrNull(done)?.let { " ${it.title} is next." } ?: ""}")
        }
        c.modules.forEachIndexed { i, m ->
            val completed = i < done; val current = i == done; val locked = i > done
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(if (current) Sage else Color.White).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    when { completed -> Icons.Default.CheckCircle; current -> Icons.Default.PlayCircle; else -> Icons.Default.Lock },
                    null, tint = Forest, modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Module ${i + 1}: ${m.title}", color = Forest, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Body(m.description)
                    Text(
                        when { completed -> "Completed"; current -> "Current module · Ready to begin"; else -> "Locked · Complete Module $i to unlock" },
                        color = Forest, fontWeight = FontWeight.SemiBold, fontSize = 11.sp
                    )
                }
            }
        }
        PrimaryButton("Continue Learning", { }, icon = Icons.Default.PlayArrow)
        OutlineButton("Back to My Courses", { nav.popBackStack() })
    }
}

/* ---------- Mobile 10 · Profile ---------- */
@Composable
fun ProfileScreen(nav: NavHostController, state: AppState) {
    PawScaffold(nav, Routes.PROFILE) {
        ScreenTitle("My Profile", "Your details and learning preferences.")
        StudentCard(state)
        PawCard {
            CardTitle("Personal information")
            Text(state.studentName, color = Forest, fontSize = 13.sp)
            Text(state.studentEmail, color = Forest, fontSize = 13.sp)
            Text(state.studentPhone, color = Forest, fontSize = 13.sp)
            Body("Learning option: Online · Self-paced")
        }
        PawCard {
            CardTitle("Learning overview")
            SummaryLine("Enrolled courses", "${Repo.enrollments.size}")
            SummaryLine("Completed courses", "${Repo.enrollments.count { it.progress == 100 }}")
            Repo.enrollments.forEach { e ->
                Body("${Repo.course(e.courseId).name} · ${if (e.progress == 100) "Completed" else "${e.progress}%"}")
            }
        }
        PawCard {
            CardTitle("Account settings")
            LabeledCheck("Email me learning reminders", state.emailReminders, { state.emailReminders = it })
            LabeledCheck("Send academy news and updates", state.academyNews, { state.academyNews = it })
            Body("Manage your privacy, communication preferences and account security.")
            OutlineButton("Change password", { })
        }
        PrimaryButton("Edit Profile", { }, icon = Icons.Default.Edit)
        OutlineButton("Logout", { nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } })
    }
}

/* ---------- Mobile 11 · Contact / Support ---------- */
@Composable
fun ContactScreen(nav: NavHostController, state: AppState) {
    var name by remember { mutableStateOf(state.studentName) }
    var email by remember { mutableStateOf(state.studentEmail) }
    var subject by remember { mutableStateOf("Help with course registration") }
    var message by remember { mutableStateOf("Hello! I'd like to find out more about the online learning option for Canine Obedience Training.") }
    var sent by remember { mutableStateOf(false) }

    PawScaffold(nav, Routes.CONTACT) {
        ScreenTitle("Contact Us", "A friendly team, here to help.")
        PhotoBox("Cat being stroked", 130.dp)
        Body("Demo academy contact information")
        InfoCard(Icons.Default.Phone, "Phone", "+27 21 555 1234")
        InfoCard(Icons.Default.Email, "Email", "smithsarah@pawsitive.za.com")
        InfoCard(Icons.Default.LocationOn, "Location", "Durban, South Africa")
        InfoCard(Icons.Default.Schedule, "Opening hours", "Mon–Fri · 09:00–17:00 SAST")
        PawCard {
            CardTitle("Send us a message")
            LabeledField("Name", name, { name = it })
            LabeledField("Email", email, { email = it })
            DropdownField("Subject", subject, listOf("Help with course registration", "Fees and payments", "Course content", "Other"), { subject = it })
            LabeledField("Message", message, { message = it }, singleLine = false, minLines = 3)
            PrimaryButton("Send Message", { if (message.isNotBlank()) sent = true }, icon = Icons.AutoMirrored.Filled.Send)
            Body(if (sent) "Thanks! Your message has been sent." else "We aim to reply within two working days.")
        }
        PawCard {
            CardTitle("Need a little guidance?")
            Body("Find answers about registration, payment, course access and certificates.")
            OutlineButton("FAQ / Help & Support", { }, arrow = true)
        }
    }
}

@Composable
fun InfoCard(icon: ImageVector, title: String, value: String) {
    PawCard {
        Icon(icon, null, tint = Forest, modifier = Modifier.size(20.dp))
        Text(title, color = Forest, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Body(value)
    }
}

/* ---------- Mobile 12 · Mobile Menu ---------- */
@Composable
fun MenuScreen(nav: NavHostController) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, Routes.HOME),
        Triple("About Us", Icons.Default.FavoriteBorder, Routes.ABOUT),
        Triple("Courses", Icons.Default.MenuBook, Routes.COURSES),
        Triple("Calculate Fees", Icons.Default.Description, Routes.FEES),
        Triple("My Courses", Icons.Default.School, Routes.MY_COURSES),
        Triple("Profile", Icons.Default.Person, Routes.PROFILE),
        Triple("Contact", Icons.Default.ChatBubbleOutline, Routes.CONTACT)
    )
    Column(Modifier.fillMaxSize().background(Cream).statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Pets, null, tint = Forest, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text("Pawsitive Pet Academy", color = Forest, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Default.Close, "Close", tint = Forest, modifier = Modifier.clickable { nav.popBackStack() })
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("EXPLORE THE ACADEMY", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            items.forEachIndexed { i, (label, icon, route) ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(if (i == 0) Sage else Color.White)
                        .clickable { nav.navigate(route) { popUpTo(Routes.HOME); launchSingleTop = true } }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, null, tint = Forest, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(14.dp))
                    Text(label, color = Forest, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Muted)
                }
            }
            Body("Practical knowledge. Compassionate care.")
            PrimaryButton("Continue my learning", { nav.navigate(Routes.MY_COURSES) { popUpTo(Routes.HOME); launchSingleTop = true } })
        }
    }
}
