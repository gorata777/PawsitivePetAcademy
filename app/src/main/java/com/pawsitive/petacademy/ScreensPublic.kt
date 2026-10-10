package com.pawsitive.petacademy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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

/* ---------- Mobile 01 · Home ---------- */
@Composable
fun HomeScreen(nav: NavHostController, state: AppState) {
    PawScaffold(nav, Routes.HOME) {
        PhotoBox("Dog and cat on sofa", 170.dp)
        Chip("Learn with care")
        Text("Welcome to Pawsitive Pet Academy", color = Forest, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 32.sp)
        Body("Expert pet-care education for everyone.")
        PrimaryButton("View Courses", { nav.go(Routes.COURSES) }, arrow = true)
        Spacer(Modifier.height(4.dp))
        Text("Featured Courses", color = Forest, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Body("A little knowledge makes a big difference.")
        Repo.courses.take(3).forEach { c ->
            CourseCard(c) { state.selectedCourse = c; nav.go(Routes.DETAILS) }
        }
    }
}

/* ---------- Mobile 02 · About Us ---------- */
@Composable
fun AboutScreen(nav: NavHostController) {
    PawScaffold(nav) {
        ScreenTitle("About Us", "Better knowledge. Happier pets.")
        PhotoBox("Our team with pets", 150.dp)
        PawCard { CardTitle("Our History"); Body("Pawsitive Pet Academy was established in 2023 in Durban. The Academy provides training courses for pet owners and aspiring pet-care professionals.") }
        PawCard { CardTitle("Our Vision"); Body("Quality pet-care education for all.") }
        PawCard { CardTitle("Our Mission"); Body("To empower pet owners with practical and compassionate knowledge, helping them give their animals thoughtful, responsible care.") }
        PawCard {
            CardTitle("Our Goals")
            Body("10+ courses"); Body("5 000 students by 2026"); Body("Partnerships with vets and shelters")
            Chip("Future targets · Not achieved metrics")
        }
    }
}

/* ---------- Mobile 03 · Courses ---------- */
@Composable
fun CoursesScreen(nav: NavHostController, state: AppState) {
    PawScaffold(nav) {
        ScreenTitle("Our Courses", "Choose from ${Repo.courses.size} expert-led pet-care courses")
        Chip("All courses · Self-paced learning")
        Repo.courses.forEach { c -> CourseCard(c) { state.selectedCourse = c; nav.go(Routes.DETAILS) } }
    }
}

/* ---------- Mobile 04 · Course Details ---------- */
@Composable
fun CourseDetailsScreen(nav: NavHostController, state: AppState) {
    val c = state.selectedCourse
    PawScaffold(nav) {
        PhotoBox(c.name, 190.dp, c.image)
        Chip("Beginner · Online · ${c.modules.size} modules")
        Text(c.name, color = Forest, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 30.sp)
        Body("A stronger bond starts with understanding.")
        Text("R${c.price}", color = Forest, fontWeight = FontWeight.Bold, fontSize = 26.sp)
        PawCard { CardTitle("Course Purpose"); Body(c.purpose) }
        PawCard {
            CardTitle("Course Content")
            c.modules.forEachIndexed { i, m ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.White)
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${i + 1}", color = Forest, fontSize = 12.sp, modifier = Modifier.clip(RoundedCornerShape(50)).background(Cream).padding(horizontal = 8.dp, vertical = 4.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Module ${i + 1}: ${m.title}", color = Forest, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                HorizontalDivider(color = Cream)
            }
        }
        PrimaryButton("Register", { nav.go(Routes.REGISTER) }, arrow = true)
        OutlineButton("Back", { nav.popBackStack() })
    }
}

/* ---------- Mobile 05 · Calculate Fees ---------- */
@Composable
fun CalculateFeesScreen(nav: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(setOf<Int>()) }
    var loyalty by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<FeeResult?>(null) }

    PawScaffold(nav) {
        ScreenTitle("Calculate Fees", "Plan your learning. See your savings.")
        PawCard {
            CardTitle("Customer Details")
            LabeledField("Full Name", name, { name = it })
            LabeledField("Email Address", email, { email = it })
            LabeledField("Phone Number", phone, { phone = it })
        }
        PawCard {
            CardTitle("Select Courses")
            Repo.feeOrder.map { Repo.course(it) }.forEach { c ->
                LabeledCheck(c.name, c.id in selected, { on -> selected = if (on) selected + c.id else selected - c.id; result = null }, "R${c.price}")
            }
        }
        PawCard(color = Sage) {
            CardTitle("Discount Information")
            Text("10% off for 3+ courses", color = Forest, fontSize = 13.sp)
            Text("15% off for 5+ courses", color = Forest, fontSize = 13.sp)
            Text("5% loyalty discount", color = Forest, fontSize = 13.sp)
            Body("The highest course-bundle discount applies. Loyalty is added only for eligible returning students.")
            LabeledCheck("Apply loyalty discount", loyalty, { loyalty = it; result = null })
            Body(if (loyalty) "Loyalty discount selected." else "Not selected in this estimate.")
        }
        PawCard {
            CardTitle("Fee Summary")
            val r = result
            if (r != null) {
                Chip("${r.count} courses · ${r.percent}% discount")
                SummaryLine("Subtotal", "R${r.subtotal}")
                SummaryLine("Discount (${r.percent}%)", "−R${r.discount}")
                HorizontalDivider(color = Line)
                Row { Text("Total Fee", color = Forest, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Text("R${r.total}", color = Forest, fontSize = 22.sp) }
            } else Body("Select courses and tap Calculate.")
            PrimaryButton("Calculate", { result = calculateFee(selected, loyalty) }, icon = Icons.Default.Calculate)
            OutlineButton("Clear", { selected = emptySet(); loyalty = false; result = null; name = ""; email = ""; phone = "" })
        }
    }
}

@Composable
fun SummaryLine(label: String, value: String) {
    Row { Text(label, color = Muted, fontSize = 13.sp, modifier = Modifier.weight(1f)); Text(value, color = Forest, fontSize = 13.sp) }
}

/* ---------- Mobile 06 · Registration ---------- */
@Composable
fun RegistrationScreen(nav: NavHostController, state: AppState) {
    val c = state.selectedCourse
    var name by remember { mutableStateOf(state.studentName) }
    var email by remember { mutableStateOf(state.studentEmail) }
    var phone by remember { mutableStateOf(state.studentPhone) }
    var option by remember { mutableStateOf("Online · Self-paced") }
    var level by remember { mutableStateOf("Beginner") }
    var agree by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    val nameErr = if (name.isBlank()) "Please enter your full name" else null
    val emailErr = if (!Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(email)) "Enter a valid email address" else null
    val phoneErr = if (phone.filter { it.isDigit() }.length < 9) "Enter a valid phone number" else null
    val allOk = nameErr == null && emailErr == null && phoneErr == null && agree

    PawScaffold(nav) {
        ScreenTitle("Course Registration", "One step closer to better pet care.")
        PawCard {
            CardTitle("Registration summary")
            PhotoBox(c.name, 130.dp, c.image)
            Text(c.name, color = Forest, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Body("${c.modules.size} modules · Online · Beginner friendly")
            SummaryLine("Course fee", "R${c.price}")
            SummaryLine("Discount", "R0")
            Row { Text("Total due", color = Forest, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Text("R${c.price}", color = Forest, fontSize = 22.sp) }
            Chip("Payment pending")
            Body("Your place is registered. Payment instructions will follow by email. No payment has been taken.")
        }
        PawCard {
            CardTitle("Student details")
            LabeledField("Full Name", name, { name = it }, if (submitted) nameErr else null, if (nameErr == null) "Name confirmed" else null)
            LabeledField("Email", email, { email = it }, if (submitted) emailErr else null, if (emailErr == null) "Valid email address" else null)
            LabeledField("Phone number", phone, { phone = it }, if (submitted) phoneErr else null, if (phoneErr == null) "Number looks good" else null)
            DropdownField("Preferred learning option", option, listOf("Online · Self-paced", "Online · Guided"), { option = it })
            DropdownField("Experience level", level, listOf("Beginner", "Intermediate", "Advanced"), { level = it })
            LabeledCheck("I agree to the Terms and conditions", agree, { agree = it })
            if (allOk) Body("✓ All required details are complete. You are ready to register.")
            else if (submitted) Text("Please complete all required details and accept the terms.", color = Color(0xFFB3261E), fontSize = 12.sp)
            PrimaryButton("Complete Registration", {
                submitted = true
                if (allOk) {
                    state.studentName = name; state.studentEmail = email; state.studentPhone = phone
                    nav.go(Routes.SUCCESS)
                }
            }, arrow = true)
            OutlineButton("Cancel", { nav.popBackStack() })
        }
    }
}

/* ---------- Mobile 07 · Registration Successful ---------- */
@Composable
fun RegistrationSuccessScreen(nav: NavHostController, state: AppState) {
    val c = state.selectedCourse
    PawScaffold(nav) {
        Box(Modifier.align(Alignment.CenterHorizontally).size(64.dp).clip(RoundedCornerShape(50)).background(Sage), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Check, null, tint = Forest, modifier = Modifier.size(30.dp))
        }
        Text("Registration Successful!", color = Forest, fontWeight = FontWeight.Bold, fontSize = 26.sp)
        Body("You're all set, ${state.studentName.substringBefore(' ')}! Your place in ${c.name} is confirmed. We've sent your registration details and payment instructions to your email.")
        PawCard {
            CardTitle("Your registration")
            Text(c.name, color = Forest, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Body(state.studentName); Body(state.studentEmail)
            SummaryLine("Registration reference", state.registrationRef)
            SummaryLine("Course fee", "R${c.price}")
            SummaryLine("Payment status", "Pending")
            Body("Online · Self-paced · Beginner")
        }
        PrimaryButton("View My Courses", { nav.go(Routes.MY_COURSES) }, arrow = true)
        OutlineButton("Back to Home", { nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } })
        PhotoBox("Dog and cat", 110.dp)
    }
}
