package com.pawsitive.petacademy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

object Routes {
    const val HOME = "home"; const val ABOUT = "about"; const val COURSES = "courses"
    const val DETAILS = "details"; const val FEES = "fees"; const val REGISTER = "register"
    const val SUCCESS = "success"; const val MY_COURSES = "my_courses"; const val PROGRESS = "progress"
    const val PROFILE = "profile"; const val CONTACT = "contact"; const val MENU = "menu"
}

fun NavHostController.go(route: String) = navigate(route) { launchSingleTop = true }

/** Common screen frame: top bar, optional bottom nav, scrolling cream background. */
@Composable
fun PawScaffold(
    nav: NavHostController,
    bottomTab: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = Cream,
        topBar = { PawTopBar(onMenu = { nav.go(Routes.MENU) }) },
        bottomBar = { if (bottomTab != null) PawBottomBar(bottomTab, nav) }
    ) { pad ->
        Column(
            Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun PawTopBar(onMenu: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Color.White).statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Pets, null, tint = Forest, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        Text("Pawsitive Pet Academy", color = Forest, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.Menu, "Menu", tint = Forest, modifier = Modifier.clickable { onMenu() })
    }
}

@Composable
fun PawBottomBar(current: String, nav: NavHostController) {
    val tabs = listOf(
        Triple("Home", Routes.HOME, Icons.Default.Home),
        Triple("My Courses", Routes.MY_COURSES, Icons.Default.MenuBook),
        Triple("Profile", Routes.PROFILE, Icons.Default.Person),
        Triple("Support", Routes.CONTACT, Icons.Default.ChatBubbleOutline)
    )
    NavigationBar(containerColor = Color.White) {
        tabs.forEach { (label, route, icon) ->
            NavigationBarItem(
                selected = current == route,
                onClick = { nav.go(route) },
                icon = { Icon(icon, label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Forest, selectedTextColor = Forest,
                    indicatorColor = Sage, unselectedIconColor = Muted, unselectedTextColor = Muted
                )
            )
        }
    }
}

@Composable
fun ScreenTitle(title: String, subtitle: String? = null) {
    Column {
        Text(title, color = Forest, fontWeight = FontWeight.Bold, fontSize = 26.sp)
        if (subtitle != null) Text(subtitle, color = Muted, fontSize = 13.sp)
    }
}

@Composable
fun PawCard(modifier: Modifier = Modifier, color: Color = Color.White, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(color).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp), content = content
    )
}

@Composable
fun CardTitle(text: String) = Text(text, color = Forest, fontWeight = FontWeight.Bold, fontSize = 16.sp)

@Composable
fun Body(text: String, modifier: Modifier = Modifier) = Text(text, color = Muted, fontSize = 13.sp, modifier = modifier)

@Composable
fun Chip(text: String) {
    Text(
        text, color = Forest, fontSize = 11.sp, fontWeight = FontWeight.Medium,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(Sage).padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, arrow: Boolean = false, icon: ImageVector? = null) {
    Button(
        onClick = onClick, modifier = modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Forest, contentColor = Color.White)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        if (arrow) { Spacer(Modifier.width(8.dp)); Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(16.dp)) }
        if (icon != null) { Spacer(Modifier.width(8.dp)); Icon(icon, null, Modifier.size(16.dp)) }
    }
}

@Composable
fun OutlineButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, arrow: Boolean = false) {
    OutlinedButton(
        onClick = onClick, modifier = modifier.fillMaxWidth().height(44.dp), shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Line),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Forest)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        if (arrow) { Spacer(Modifier.width(8.dp)); Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(16.dp)) }
    }
}

/** Shows the course photo if one is supplied, otherwise a green placeholder. */
@Composable
fun PhotoBox(label: String, height: Dp = 160.dp, image: Int? = null, shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(16.dp)) {
    if (image != null) {
        Image(painterResource(image), label, Modifier.fillMaxWidth().height(height).clip(shape), contentScale = ContentScale.Crop)
    } else {
        Box(
            Modifier.fillMaxWidth().height(height).clip(shape)
                .background(Brush.verticalGradient(listOf(Sage, Color(0xFF8FB58A)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Pets, null, tint = Forest, modifier = Modifier.size(36.dp))
                Text(label, color = Forest, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun CourseCard(course: Course, onView: () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White)) {
        PhotoBox(course.name, 120.dp, course.image, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(course.category)
            Text(course.name, color = Forest, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Body(course.description)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("R${course.price}", color = Forest, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
                Text("Self-paced · Online", color = Muted, fontSize = 11.sp)
            }
            OutlineButton("View Course", onView, arrow = true)
        }
    }
}

@Composable
fun LabeledField(label: String, value: String, onChange: (String) -> Unit, error: String? = null, okText: String? = null, singleLine: Boolean = true, minLines: Int = 1) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, color = Muted, fontSize = 11.sp)
        OutlinedTextField(
            value = value, onValueChange = onChange, singleLine = singleLine, minLines = minLines,
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), isError = error != null,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = Forest),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Forest, unfocusedBorderColor = Line),
            trailingIcon = { if (okText != null && error == null) Icon(Icons.Default.Check, null, tint = Forest) }
        )
        if (error != null) Text(error, color = Color(0xFFB3261E), fontSize = 11.sp)
        else if (okText != null) Text("✓ $okText", color = Muted, fontSize = 11.sp)
    }
}

@Composable
fun DropdownField(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, color = Muted, fontSize = 11.sp)
        Box {
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).border(1.dp, Line, RoundedCornerShape(10.dp))
                    .clickable { open = true }.padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(value, color = Forest, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Default.KeyboardArrowDown, null, tint = Forest)
            }
            DropdownMenu(open, { open = false }) {
                options.forEach { o -> DropdownMenuItem(text = { Text(o) }, onClick = { onSelect(o); open = false }) }
            }
        }
    }
}

@Composable
fun LabeledCheck(text: String, checked: Boolean, onChange: (Boolean) -> Unit, trailing: String? = null) {
    Row(Modifier.fillMaxWidth().clickable { onChange(!checked) }, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked, onChange, colors = CheckboxDefaults.colors(checkedColor = Forest))
        Text(text, color = Forest, fontSize = 13.sp, modifier = Modifier.weight(1f))
        if (trailing != null) Text(trailing, color = Forest, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun ProgressRow(label: String, percent: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row { Text(label, color = Forest, fontSize = 12.sp, modifier = Modifier.weight(1f)); Text("$percent%", color = Forest, fontSize = 12.sp) }
        LinearProgressIndicator(progress = { percent / 100f }, Modifier.fillMaxWidth().height(5.dp).clip(CircleShape), color = Forest, trackColor = Sage.copy(alpha = 0.5f))
    }
}
