package com.pawsitive.petacademy

import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Module(val title: String, val description: String)

data class Course(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val price: Int,
    val purpose: String,
    val modules: List<Module>,
    // Put your photo in res/drawable and pass e.g. R.drawable.canine_training
    @DrawableRes val image: Int? = null
)

data class Enrollment(val courseId: Int, val progress: Int) // progress 0..100

private fun generic(vararg titles: String) =
    titles.map { Module(it, "Work through the lessons and activities for this module.") }

object Repo {
    val courses = listOf(
        Course(
            1, "Canine Obedience Training", "Behaviour",
            "Build trust with positive reinforcement and everyday commands.", 1500,
            "Learn to build trust and teach everyday skills through positive reinforcement. Use rewards, clear cues and compassionate practice to help your dog learn with confidence.",
            listOf(
                Module("Understanding dog behaviour", "Read canine body language and recognise everyday needs."),
                Module("Basic commands", "Practise sit, stay and recall with reward-based techniques."),
                Module("Socialisation", "Build confidence through safe, positive introductions."),
                Module("Advanced techniques", "Apply your skills to more challenging everyday situations.")
            )
        ),
        Course(2, "Pet Grooming", "Everyday care", "Learn safe grooming techniques for healthy coats and comfortable pets.", 1500,
            "Learn safe, gentle grooming for healthy coats and comfortable pets.",
            generic("Coat and skin basics", "Tools and safety", "Bathing and brushing", "Nail and ear care")),
        Course(3, "Pet First Aid", "Health & safety", "Learn calm, practical responses to common pet emergencies.", 750,
            "Learn calm, practical responses to common pet emergencies.",
            generic("Staying calm", "Common emergencies", "Wound care", "When to call the vet")),
        Course(4, "Puppy Care", "Early learning", "Learn everyday puppy care, development and gentle socialisation.", 750,
            "Learn everyday puppy care, development and gentle socialisation.",
            generic("Welcoming your puppy", "Feeding and health", "House training", "Gentle socialisation")),
        Course(5, "Pet Business Management", "Business", "Build practical skills to plan and manage a pet-care business.", 1500,
            "Build practical skills to plan and manage a pet-care business.",
            generic("Business planning", "Pricing and finance", "Marketing", "Client care")),
        Course(6, "Basic Dog Walking", "Dog walking", "Learn safe handling, leash skills and responsible dog walking.", 750,
            "Learn safe handling, leash skills and responsible dog walking.",
            generic("Safe handling", "Leash skills", "Walking groups", "Responsible walking")),
        Course(7, "Animal Behaviour", "Behaviour", "Understand animal behaviour and encourage positive habits.", 1500,
            "Understand animal behaviour and encourage positive habits.",
            generic("Reading behaviour", "Motivation and learning", "Positive habits", "Solving common problems"))
    )

    // Order used on the Calculate Fees wireframe
    val feeOrder = listOf(6, 7, 3, 4, 5, 2, 1)

    fun course(id: Int) = courses.first { it.id == id }

    val enrollments = listOf(
        Enrollment(1, 50), Enrollment(2, 25), Enrollment(3, 0), Enrollment(4, 100)
    )
}

/** Fee rules: 10% for 3+ courses, 15% for 5+ (highest bundle wins), plus 5% loyalty if eligible. */
data class FeeResult(val count: Int, val subtotal: Int, val percent: Int, val discount: Int, val total: Int)

fun calculateFee(courseIds: Set<Int>, loyalty: Boolean): FeeResult {
    val subtotal = courseIds.sumOf { Repo.course(it).price }
    val bundle = when {
        courseIds.size >= 5 -> 15
        courseIds.size >= 3 -> 10
        else -> 0
    }
    val percent = bundle + if (loyalty && courseIds.isNotEmpty()) 5 else 0
    val discount = subtotal * percent / 100
    return FeeResult(courseIds.size, subtotal, percent, discount, subtotal - discount)
}

/** Simple app-wide state shared between screens. */
class AppState {
    var selectedCourse by mutableStateOf(Repo.courses.first())
    var registrationRef by mutableStateOf("PPA-2026-01042")
    var studentName by mutableStateOf("Sarah Smith")
    var studentEmail by mutableStateOf("smithsarah@pawsitive.za.com")
    var studentPhone by mutableStateOf("+27 82 555 0148")
    var emailReminders by mutableStateOf(true)
    var academyNews by mutableStateOf(false)
}
