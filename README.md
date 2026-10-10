# Pawsitive Pet Academy — Android Studio Project

This is an Android app built with Kotlin and Jetpack Compose.

## Open and run
1. Extract the ZIP to a normal folder.
2. In Android Studio choose **File → Open** and select the `PawsitivePetAcademy` folder — the folder containing `settings.gradle.kts`.
3. This project is configured for **Gradle 8.9**, Android Gradle Plugin **8.7.3**, Kotlin **2.0.21**, JDK **17**, and Android SDK **35**.
4. Allow Android Studio to download Gradle and required dependencies. If prompted to install Android SDK Platform 35 or Build Tools, accept.
5. If Gradle Sync does not start automatically, choose **File → Sync Project with Gradle Files**.
6. When sync succeeds, choose the `app` run configuration, select an emulator/device, and press **Run ▶**.

## If Android Studio still reports a Gradle version/configuration error
- Open **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**.
- Set **Gradle distribution** to **Wrapper** / `gradle-wrapper.properties`.
- Set **Gradle JDK** to **JDK 17** (Android Studio's Embedded JDK 17 or another installed JDK 17).
- Click **Apply**, then **OK**, and run **File → Sync Project with Gradle Files**.
- Do not create a run configuration manually until Gradle sync succeeds.

## Project details
- Application ID: `com.pawsitive.petacademy`
- Minimum Android version: Android 7.0 (API 24)
- Compile/target SDK: 35
- Main activity: `app/src/main/java/com/pawsitive/petacademy/MainActivity.kt`
- UI: Jetpack Compose + Material 3
- Navigation: Navigation Compose

## Feature limitations
This is a local demo app. Registration and progress are held in app memory; there is no backend, account authentication, email delivery, or real payment processing. Contact/support actions may show local demo feedback rather than sending messages. Do not use it to collect real student data until those services are implemented.
